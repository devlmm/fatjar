package com.workspace.fatjar.auth.resultcode;

import com.workspace.fatjar.common.result.ResultCode;

/**
 * 权限模块结果码枚举（编码段 11xxx）
 * <p>
 * 实现 {@link ResultCode} 契约，由 {@link com.workspace.fatjar.auth.exception.AuthBizException}
 * 与统一返回 R 引用。
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum AuthResultCode implements ResultCode {

    /** 用户名或密码错误 */
    BAD_CREDENTIALS(11001, "用户名或密码错误"),
    /** 账号已禁用 */
    ACCOUNT_DISABLED(11002, "账号已禁用"),
    /** 验证码已过期 */
    CAPTCHA_EXPIRED(11003, "验证码已过期"),
    /** 验证码错误 */
    CAPTCHA_INVALID(11004, "验证码错误"),
    /** 用户名已被占用 */
    USERNAME_EXISTS(11005, "用户名已被占用"),
    /** 用户不存在 */
    USER_NOT_FOUND(11006, "用户不存在"),
    /** 未授权 */
    UNAUTHORIZED(11007, "未授权");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String message;

    /**
     * 枚举构造
     *
     * @param code    状态码
     * @param message 提示信息
     */
    AuthResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
