package com.workspace.fatjar.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.workspace.fatjar.common.exception.ErrorCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一返回结果封装 R
 * <p>
 * 所有 Controller 接口统一返回 R&lt;T&gt;，前端按 code 判断成功失败。
 * 约定：code == 0 表示成功，非 0 表示失败；message 为提示文案；data 为业务数据。
 * <p>
 * 使用示例：
 *   return R.ok(pageResult);
 *   return R.fail(ErrorCode.PARAM_INVALID, "商品名不能为空");
 *
 * @param <T> 业务数据类型
 * @author fatjar
 * @since 1.0.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // null 字段不序列化，减小响应体
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 状态码：0=成功，非 0=失败 */
    private int code;
    /** 提示信息 */
    private String message;
    /** 业务数据 */
    private T data;
    /** 链路追踪 ID（由 MDC + Filter 注入，便于排障） */
    private String traceId;

    /** 默认构造 */
    public R() {
    }

    /**
     * 全参构造
     *
     * @param code    状态码
     * @param message 提示信息
     * @param data    业务数据
     */
    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功返回（无数据）
     *
     * @param <T> 数据类型
     * @return R 实例
     */
    public static <T> R<T> ok() {
        return new R<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null);
    }

    /**
     * 成功返回（带数据）
     *
     * @param data 业务数据
     * @param <T>  数据类型
     * @return R 实例
     */
    public static <T> R<T> ok(T data) {
        return new R<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /**
     * 失败返回（错误码枚举）
     *
     * @param errorCode 错误码枚举
     * @param <T>       数据类型
     * @return R 实例
     */
    public static <T> R<T> fail(ErrorCode errorCode) {
        return new R<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 失败返回（错误码 + 补充信息）
     *
     * @param errorCode 错误码枚举
     * @param detail    补充信息
     * @param <T>       数据类型
     * @return R 实例
     */
    public static <T> R<T> fail(ErrorCode errorCode, String detail) {
        String msg = errorCode.getMessage() + (detail == null ? "" : ": " + detail);
        return new R<>(errorCode.getCode(), msg, null);
    }

    /**
     * 失败返回（自定义 code/message）
     *
     * @param code    状态码
     * @param message 提示信息
     * @param <T>     数据类型
     * @return R 实例
     */
    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }

    /**
     * 判断是否成功
     *
     * @return true 表示 code == 0
     */
    public boolean isSuccess() {
        return this.code == ErrorCode.SUCCESS.getCode();
    }
}
