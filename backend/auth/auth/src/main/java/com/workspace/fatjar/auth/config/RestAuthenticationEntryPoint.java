package com.workspace.fatjar.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workspace.fatjar.common.result.CommonResultCode;
import com.workspace.fatjar.common.result.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 未认证端点（未登录访问需认证接口时触发）
 * <p>
 * 触发场景：
 *   1. 未携带 Authorization 头访问需认证接口
 *   2. 携带的 Token 已失效或被 Redis 清除
 *   3. Token 一致性校验失败（JwtAuthenticationFilter 未注入 SecurityContext）
 * <p>
 * 响应：HTTP 401 + R&lt;Void&gt; JSON（code=10002 UNAUTHORIZED），
 * 统一为前端可解析的 R 格式（code/message/data/traceId），避免 Spring Security 默认白页。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /** Jackson 序列化器（Spring Boot 自动配置，构造器注入） */
    private final ObjectMapper objectMapper;

    /**
     * 触发未认证响应
     * <p>
     * 写入 R JSON 到响应体，设置 401 状态码与 UTF-8 编码。
     *
     * @param request       HTTP 请求
     * @param response      HTTP 响应
     * @param authException 认证异常
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.warn("未认证访问：uri={}, msg={}", request.getRequestURI(), authException.getMessage());
        R<Void> r = R.fail(CommonResultCode.UNAUTHORIZED, "请先登录");
        r.setTraceId(MDC.get("traceId"));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(r));
    }
}
