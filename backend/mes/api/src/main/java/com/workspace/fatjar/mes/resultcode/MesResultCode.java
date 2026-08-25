package com.workspace.fatjar.mes.resultcode;

import com.workspace.fatjar.common.result.ResultCode;

/**
 * 制造执行系统模块结果码枚举（编码段 22xxx）
 * <p>
 * 实现 {@link ResultCode} 契约，由 {@link com.workspace.fatjar.mes.exception.MesBizException} 统一引用。
 * 编码段约定：mes=22xxx。
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum MesResultCode implements ResultCode {

    /** 工单不存在 */
    DATA_NOT_FOUND(22001, "工单不存在"),

    /** 操作失败 */
    OPERATION_FAILED(22002, "操作失败");

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
    MesResultCode(int code, String message) {
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
