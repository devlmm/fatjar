package com.workspace.fatjar.framework.web.config;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.CommonResultCode;
import com.workspace.fatjar.common.result.R;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 职责：拦截所有 Controller 抛出的异常，统一转换为 R&lt;Void&gt; 返回，并注入 traceId 便于排障。
 * <p>
 * 处理顺序（按异常类型精确匹配优先）：
 *   1. BizException                       -> 业务异常，沿用其 code 与 message
 *   2. MethodArgumentNotValidException     -> @RequestBody 参数校验失败（@Valid）
 *   3. BindException                      -> 表单参数绑定 + 校验失败
 *   4. ConstraintViolationException       -> @RequestParam / @PathVariable 校验失败（@Validated）
 *   5. HttpRequestMethodNotSupportedException -> 请求方法不支持（如 POST 接口用 GET 访问）
 *   6. NoHandlerFoundException            -> 接口不存在（404，需 spring.mvc.throw-exception-if-no-handler-found=true）
 *   7. HttpMessageNotReadableException    -> 请求体不可读（JSON 格式错误）
 *   8. MaxUploadSizeExceededException     -> 上传文件超过限制
 *   9. Exception                          -> 兜底未知异常，返回系统异常码
 * <p>
 * 说明：Spring Security 的 AccessDeniedException / AuthenticationException 由
 * SecurityConfig 配置的 RestAccessDeniedHandler / RestAuthenticationEntryPoint 处理
 * （在过滤器链内被 ExceptionTranslationFilter 捕获，不经过 @ControllerAdvice）。
 * <p>
 * traceId 来源：TraceIdFilter 在请求进入时写入 MDC，此处取出回填到 R。
 *
 * @author fatjar
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     *
     * @param e 业务异常（携带错误码与文案）
     * @return 统一返回结果，code 取自异常，message 取自异常
     */
    @ExceptionHandler(BizException.class)
    public R<Void> handleBizException(BizException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return fillTraceId(R.fail(e.getCode(), e.getMessage()));
    }

    /**
     * 处理 @RequestBody 参数校验失败异常
     *
     * @param e 校验异常
     * @return 统一返回结果，错误码 PARAM_INVALID，message 拼接所有字段错误
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败（Body）：{}", detail);
        return fillTraceId(R.fail(CommonResultCode.PARAM_INVALID, detail));
    }

    /**
     * 处理表单参数绑定 + 校验失败异常
     *
     * @param e 绑定异常
     * @return 统一返回结果，错误码 PARAM_INVALID
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数绑定校验失败（Form）：{}", detail);
        return fillTraceId(R.fail(CommonResultCode.PARAM_INVALID, detail));
    }

    /**
     * 处理 @RequestParam / @PathVariable 校验失败异常
     *
     * @param e 约束违反异常
     * @return 统一返回结果，错误码 PARAM_INVALID
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolation(ConstraintViolationException e) {
        String detail = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败（Param）：{}", detail);
        return fillTraceId(R.fail(CommonResultCode.PARAM_INVALID, detail));
    }

    /**
     * 处理请求方法不支持异常
     * <p>
     * 场景：如 POST 接口被 GET 访问，或 PUT 接口被 POST 访问。
     * 返回 code=10007 METHOD_NOT_ALLOWED，message 含实际与支持的方法。
     *
     * @param e 方法不支持异常
     * @return 统一返回结果，错误码 METHOD_NOT_ALLOWED
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不支持：{}", e.getMessage());
        return fillTraceId(R.fail(CommonResultCode.METHOD_NOT_ALLOWED, e.getMessage()));
    }

    /**
     * 处理接口不存在异常（404）
     * <p>
     * 生效前提：application.yml 配置 spring.mvc.throw-exception-if-no-handler-found=true
     * （未关闭 add-mappings 以保留 webjars/knife4j 静态资源）。
     *
     * @param e 找不到 handler 异常
     * @return 统一返回结果，错误码 DATA_NOT_FOUND，message 含请求 URL
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNoHandlerFound(NoHandlerFoundException e) {
        log.warn("接口不存在：{}", e.getRequestURL());
        return fillTraceId(R.fail(CommonResultCode.DATA_NOT_FOUND, "接口不存在: " + e.getRequestURL()));
    }

    /**
     * 处理请求体不可读异常
     * <p>
     * 场景：@RequestBody 传入非法 JSON、Content-Type 不匹配、必填 body 缺失。
     *
     * @param e 消息不可读异常
     * @return 统一返回结果，错误码 PARAM_INVALID
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public R<Void> handleMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("请求体不可读：{}", e.getMessage());
        return fillTraceId(R.fail(CommonResultCode.PARAM_INVALID, "请求体格式错误"));
    }

    /**
     * 处理上传文件超过限制异常
     * <p>
     * 场景：上传文件大小超过 spring.servlet.multipart.max-file-size 配置。
     *
     * @param e 上传超限异常
     * @return 统一返回结果，错误码 PARAM_INVALID
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public R<Void> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        log.warn("上传文件超限：{}", e.getMessage());
        return fillTraceId(R.fail(CommonResultCode.PARAM_INVALID, "上传文件超过限制"));
    }

    /**
     * 兜底处理所有未知异常
     *
     * @param e 未知异常
     * @return 统一返回结果，错误码 SYSTEM_ERROR
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return fillTraceId(R.fail(CommonResultCode.SYSTEM_ERROR, e.getMessage()));
    }

    /**
     * 从 MDC 取 traceId 填入返回结果
     *
     * @param r 原始返回结果
     * @param <T> 数据类型
     * @return 带有 traceId 的返回结果（MDC 中无 traceId 时为 null）
     */
    private <T> R<T> fillTraceId(R<T> r) {
        r.setTraceId(MDC.get("traceId"));
        return r;
    }
}
