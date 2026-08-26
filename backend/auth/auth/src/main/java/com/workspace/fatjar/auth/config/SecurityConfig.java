package com.workspace.fatjar.auth.config;

import com.workspace.fatjar.auth.filter.JwtAuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * <p>
 * 职责：
 *   1. 配置 SecurityFilterChain：禁用 CSRF、无状态会话、定义授权规则、注入 JWT 过滤器
 *   2. 放行登录接口与 API 文档入口；其余接口必须认证
 *   3. 在 UsernamePasswordAuthenticationFilter 之前注入 JwtAuthenticationFilter
 *   4. 配置异常处理：未认证/无权限统一返回 R 格式 JSON（避免 Spring Security 默认白页）
 * <p>
 * 重要约定：
 *   - PasswordEncoder Bean 由 fatjar-spring-boot-starter-security 的 SecurityBaseConfig 注册，
 *     本类不重复注册以避免重复 Bean 启动失败；业务层（如 AuthServiceImpl）直接 @Autowired 使用即可
 *   - JwtAuthenticationFilter 标注 @Component 后会被 Spring Boot 自动注册到 servlet 容器，
 *     为避免与 Spring Security 内部链重复执行，通过 FilterRegistrationBean 显式禁用 servlet 注册
 *   - RestAuthenticationEntryPoint / RestAccessDeniedHandler 同为 @Component，构造器注入
 *
 * @author fatjar
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** JWT 鉴权过滤器（由 Spring 容器注入，标注 @Component） */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    /** 未认证处理器（未登录访问需认证接口时返回 401 + R JSON） */
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    /** 访问拒绝处理器（已登录但无权限时返回 403 + R JSON） */
    private final RestAccessDeniedHandler accessDeniedHandler;

    /**
     * 构造器注入 JWT 过滤器与异常处理器
     * <p>
     * 注意：PasswordEncoder Bean 由 starter-security 的 SecurityBaseConfig 注册，
     * 此处无需重复声明，业务层直接通过 @Autowired PasswordEncoder 使用即可。
     *
     * @param jwtAuthenticationFilter JWT 鉴权过滤器
     * @param authenticationEntryPoint 未认证处理器
     * @param accessDeniedHandler     访问拒绝处理器
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           RestAuthenticationEntryPoint authenticationEntryPoint,
                           RestAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    /**
     * 配置 SecurityFilterChain（核心安全规则）
     * <p>
     * 规则：
     *   - CSRF：禁用（前后端分离 + JWT，无 cookie 会话，不需要 CSRF 防护）
     *   - 会话：STATELESS（无状态，每次请求通过 JWT 鉴权）
     *   - 授权：/auth/login、API 文档入口 permitAll；其余 authenticated
     *   - 异常：未认证 → RestAuthenticationEntryPoint（401）；无权限 → RestAccessDeniedHandler（403）
     *   - 过滤器：JwtAuthenticationFilter 在 UsernamePasswordAuthenticationFilter 之前
     *
     * @param http HttpSecurity 构建器
     * @return SecurityFilterChain
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（前后端分离 + JWT 不需要 CSRF 防护）
                .csrf(csrf -> csrf.disable())
                // 无状态会话（不创建 HttpSession）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 授权规则
                .authorizeHttpRequests(auth -> auth
                        // ============== auth 模块白名单 ==============
                        // 登录接口：始终放行
                        .requestMatchers("/auth/login").permitAll()
                        // 图形验证码接口：登录前需要取验证码，必须放行
                        .requestMatchers("/auth/captcha").permitAll()
                        // 用户自助注册：登录前调用，必须放行
                        .requestMatchers("/auth/register").permitAll()
                        // ============== Knife4j / Swagger 文档入口放行 ==============
                        .requestMatchers("/doc.html").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/favicon.ico").permitAll()
                        // ============== Actuator 健康检查：K8s 探针必须匿名可访问 ==============
                        .requestMatchers("/actuator/**").permitAll()
                        // ============== Spring Boot /error 页面：避免过滤器返回白页 ==============
                        .requestMatchers("/error").permitAll()
                        // 其余接口必须认证
                        .anyRequest().authenticated()
                )
                // 异常处理：未认证/无权限统一返回 R 格式 JSON（code/message/traceId）
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // 在 UsernamePasswordAuthenticationFilter 之前注入 JWT 鉴权过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 禁用 JwtAuthenticationFilter 的 servlet 容器自动注册
     * <p>
     * 背景：JwtAuthenticationFilter 标注 @Component 后，Spring Boot 默认会将其注册到 servlet 容器
     * 作为外部 Filter，与 Spring Security 内部链 addFilterBefore 注入的实例形成双重执行。
     * OncePerRequestFilter 自身的 alreadyFilteredAttributeName 机制虽能避免重复执行，
     * 但为减少请求处理路径开销，显式禁用 servlet 容器注册，仅保留 Spring Security 链内执行。
     *
     * @param filter JWT 鉴权过滤器
     * @return FilterRegistrationBean（enabled=false 表示禁用 servlet 容器注册）
     */
    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }
}
