package com.workspace.fatjar.auth.filter;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.workspace.fatjar.auth.util.JwtUtil;
import com.workspace.fatjar.common.constant.CommonConstants;
import com.workspace.fatjar.common.context.UserContext;
import com.workspace.fatjar.common.context.UserContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 鉴权过滤器
 * <p>
 * 职责：
 *   1. 从 Authorization Header 取 Bearer Token
 *   2. JwtUtil 解析 userId
 *   3. 从 Redis 缓存（REDIS_KEY_TOKEN+userId）读取登录时缓存的 UserContext JSON
 *   4. 校验 Token 与缓存中 Token 一致性（防止伪造）
 *   5. 注入 UserContextHolder（业务层通过 UserContextHolder.get() 获取当前用户）
 *   6. 构造 UsernamePasswordAuthenticationToken 注入 SecurityContext（Spring Security 鉴权基础）
 *   7. 请求结束时清理 ThreadLocal（finally 中 clear）
 * <p>
 * 设计说明：
 *   1. 继承 OncePerRequestFilter，保证每个请求只过滤一次（避免 forward / include 重复触发）
 *   2. 标注 @Component 由 Spring 容器管理，OncePerRequestFilter 自身机制保证不重复执行
 *   3. principal 使用 Spring Security 内置的 User 对象，authorities 包含 ROLE_前缀的角色与权限标识
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** JWT 工具（解析 Token） */
    private final JwtUtil jwtUtil;
    /** Redis 操作模板（读取 UserContext 缓存） */
    private final StringRedisTemplate redisTemplate;

    /**
     * 过滤逻辑（核心实现）
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 取 Authorization Header
            String header = request.getHeader(CommonConstants.HEADER_AUTHORIZATION);
            if (header != null && header.startsWith(CommonConstants.TOKEN_PREFIX)) {
                // 截取 Bearer 之后的 Token
                String token = header.substring(CommonConstants.TOKEN_PREFIX.length());
                // 2. 解析 userId
                Long userId = jwtUtil.parseUserId(token);
                if (userId != null) {
                    // 3. 从 Redis 加载 UserContext
                    UserContext ctx = loadUserContext(userId, token);
                    if (ctx != null) {
                        // 4. 注入 ThreadLocal
                        UserContextHolder.set(ctx);
                        // 5. 构造 Spring Security Authentication，注入 SecurityContext
                        UsernamePasswordAuthenticationToken authentication = buildAuthentication(ctx);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
            // 继续过滤器链
            filterChain.doFilter(request, response);
        } finally {
            // 6. 请求结束清理 ThreadLocal，避免内存泄漏
            UserContextHolder.clear();
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 从 Redis 加载 UserContext（含 Token 一致性校验）
     *
     * @param userId 用户 ID
     * @param token  当前请求携带的 Token
     * @return UserContext，缓存不存在或 Token 不匹配返回 null
     */
    private UserContext loadUserContext(Long userId, String token) {
        String ctxKey = CommonConstants.REDIS_KEY_TOKEN + userId;
        String ctxJson = redisTemplate.opsForValue().get(ctxKey);
        if (ctxJson == null) {
            return null;
        }
        JSONObject obj = JSONUtil.parseObj(ctxJson);
        String cachedToken = obj.getStr("token");
        // Token 一致性校验，防止使用旧 Token 或伪造 Token
        if (cachedToken == null || !cachedToken.equals(token)) {
            log.warn("Token 不一致：userId={}, cached={}, request={}", userId, cachedToken, token);
            return null;
        }
        // 解析角色与权限集合
        Set<String> roles = obj.getJSONArray("roles") != null
                ? new HashSet<>(obj.getJSONArray("roles").toList(String.class))
                : new HashSet<>();
        Set<String> perms = obj.getJSONArray("permissions") != null
                ? new HashSet<>(obj.getJSONArray("permissions").toList(String.class))
                : new HashSet<>();
        return UserContext.builder()
                .userId(obj.getLong("userId"))
                .username(obj.getStr("username"))
                .nickname(obj.getStr("nickname"))
                .roles(roles)
                .permissions(perms)
                .tenantId(obj.getLong("tenantId"))
                .token(token)
                .build();
    }

    /**
     * 构造 Spring Security Authentication 对象
     * <p>
     * principal 使用 Spring Security 内置的 User；
     * authorities 包含 ROLE_前缀的角色编码（用于 @PreAuthorize("hasRole('admin')")）
     * 与权限标识（用于 @PreAuthorize("hasAuthority('system:user:add')")）
     *
     * @param ctx 用户上下文
     * @return Authentication 对象
     */
    private UsernamePasswordAuthenticationToken buildAuthentication(UserContext ctx) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 角色编码加 ROLE_ 前缀（Spring Security hasRole 默认去除前缀匹配）
        if (ctx.getRoles() != null) {
            ctx.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
        }
        // 权限标识直接作为 authority
        if (ctx.getPermissions() != null) {
            ctx.getPermissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p)));
        }
        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(ctx.getUsername(), "", authorities);
        // 第二个参数 credentials 设为 null（已登录态无需凭证）
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
