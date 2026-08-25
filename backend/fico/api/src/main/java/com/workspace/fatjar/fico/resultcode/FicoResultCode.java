package com.workspace.fatjar.fico.resultcode;

import com.workspace.fatjar.common.result.ResultCode;

/**
 * 财务会计模块结果码枚举（编码段 20xxx）
 * <p>
 * 实现 {@link ResultCode} 契约，由 {@link com.workspace.fatjar.fico.exception.FicoBizException} 统一引用。
 * 编码段约定：fico=20xxx。
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum FicoResultCode implements ResultCode {

    /** 凭证不存在 */
    DATA_NOT_FOUND(20001, "凭证不存在"),

    /** 操作失败 */
    OPERATION_FAILED(20002, "操作失败"),

    /** 余额不足 */
    BALANCE_NOT_ENOUGH(20003, "余额不足");

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
    FicoResultCode(int code, String message) {
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
