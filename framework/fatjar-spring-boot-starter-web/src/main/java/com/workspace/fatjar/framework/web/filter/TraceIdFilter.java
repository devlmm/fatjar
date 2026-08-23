package com.workspace.fatjar.framework.web.filter;

import com.workspace.fatjar.common.constant.CommonConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

import java.io.IOException;
import java.util.UUID;

/**
 * 链路追踪 TraceId 过滤器
 * <p>
 * 职责：每个请求进入时，从 X-Trace-Id 请求头获取或自动生成 32 位无横线 UUID 作为 traceId，
 *       写入 MDC（key=traceId）供日志关联与全局异常处理器回填；响应头回写 X-Trace-Id 供调用方排障；
 *       请求结束时 finally 清除 MDC，避免 ThreadLocal 复用导致串号。
 * <p>
 * 装配方式：本类既是 @AutoConfiguration（被 imports 文件加载），又直接实现 Filter。
 *           通过 @Bean 注册 FilterRegistrationBean 并 setFilter(this)，与配置类同实例，
 *           Spring Boot 会自动去重，避免重复注册。
 *           注：Spring Boot 3.2.5 的 @AutoConfiguration 不支持 proxyBeanMethods 属性
 *           （仅 @Configuration 支持），如需关闭代理请改用 @Configuration(proxyBeanMethods=false)。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
public class TraceIdFilter implements Filter {

    /** 过滤器名称 */
    private static final String FILTER_NAME = "traceIdFilter";

    /**
     * 过滤逻辑：生成 / 透传 traceId，写入 MDC，放行后回写响应头，finally 清理 MDC
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 优先从上游透传的 X-Trace-Id 取，未携带则生成 32 位无横线 UUID
        String traceId = req.getHeader(CommonConstants.HEADER_TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }
        // 写入 MDC，供日志框架 %X{traceId} 输出，GlobalExceptionHandler 也会读取
        MDC.put(CommonConstants.MDC_TRACE_ID, traceId);
        // 响应头回写，便于调用方 / 网关记录链路
        resp.setHeader(CommonConstants.HEADER_TRACE_ID, traceId);

        try {
            chain.doFilter(request, response);
        } finally {
            // 必须清理，防止线程池复用导致 traceId 串号
            MDC.remove(CommonConstants.MDC_TRACE_ID);
        }
    }

    /**
     * 注册 FilterRegistrationBean，将本过滤器注入 Servlet 容器
     * <p>
     * 之所以 setFilter(this)：本 @AutoConfiguration 类自身是单例 Bean 且实现了 Filter，
     * 与注册器引用同一实例可让 Spring Boot 自动去重，避免重复注册两个过滤器。
     *
     * @return FilterRegistrationBean 包装实例
     */
    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration() {
        FilterRegistrationBean<TraceIdFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(this);
        registration.addUrlPatterns("/*");
        registration.setName(FILTER_NAME);
        // 最高优先级，确保 traceId 在最外层被注入，覆盖所有日志与异常处理
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
