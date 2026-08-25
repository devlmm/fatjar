package com.workspace.fatjar.scm.resultcode;

import com.workspace.fatjar.common.result.ResultCode;

/**
 * 供应链管理模块结果码枚举（编码段 21xxx）
 * <p>
 * 实现 {@link ResultCode} 契约，由 {@link com.workspace.fatjar.scm.exception.ScmBizException} 统一引用。
 * 编码段约定：scm=21xxx。
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum ScmResultCode implements ResultCode {

    /** 采购订单不存在 */
    DATA_NOT_FOUND(21001, "采购订单不存在"),

    /** 操作失败 */
    OPERATION_FAILED(21002, "操作失败"),

    /** 预算不足 */
    BUDGET_NOT_ENOUGH(21003, "预算不足");

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
    ScmResultCode(int code, String message) {
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
