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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 访问拒绝处理器（已登录但无权限时触发）
 * <p>
 * 触发场景：
 *   1. 已登录用户访问需要更高权限的接口（@PreAuthorize 校验失败）
 *   2. 角色不匹配（如普通用户访问 admin 专属接口）
 * <p>
 * 响应：HTTP 403 + R&lt;Void&gt; JSON（code=10003 FORBIDDEN），
 * 与前端 R 类解析逻辑一致（admin-vue3 与 uniapp 均按 code/message 解析）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    /** Jackson 序列化器（Spring Boot 自动配置，构造器注入） */
    private final ObjectMapper objectMapper;

    /**
     * 触发访问拒绝响应
     * <p>
     * 写入 R JSON 到响应体，设置 403 状态码与 UTF-8 编码。
     *
     * @param request      HTTP 请求
     * @param response     HTTP 响应
     * @param accessDeniedException 访问拒绝异常
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.warn("访问拒绝：uri={}, msg={}", request.getRequestURI(), accessDeniedException.getMessage());
        R<Void> r = R.fail(CommonResultCode.FORBIDDEN);
        r.setTraceId(MDC.get("traceId"));
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(r));
    }
}
