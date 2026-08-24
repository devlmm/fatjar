package com.workspace.fatjar.framework.web.config;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.CommonResultCode;
import com.workspace.fatjar.common.result.R;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 职责：拦截所有 Controller 抛出的异常，统一转换为 R&lt;Void&gt; 返回，并注入 traceId 便于排障。
 * <p>
 * 处理顺序（按异常类型精确匹配优先）：
 *   1. BizException          -> 业务异常，沿用其 code 与 message
 *   2. MethodArgumentNotValidException -> @RequestBody 参数校验失败（@Valid）
 *   3. BindException         -> 表单参数绑定 + 校验失败
 *   4. ConstraintViolationException -> @RequestParam / @PathVariable 校验失败（@Validated）
 *   5. Exception             -> 兜底未知异常，返回系统异常码
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
