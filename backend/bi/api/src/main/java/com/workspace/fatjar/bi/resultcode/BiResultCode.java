package com.workspace.fatjar.bi.resultcode;

import com.workspace.fatjar.common.result.ResultCode;

/**
 * BI 模块结果码枚举（编码段 26xxx）
 * <p>
 * 设计说明：
 *   1. 实现 {@link ResultCode} 契约，由 {@link com.workspace.fatjar.bi.exception.BiBizException} 统一引用
 *   2. 仅承载 bi 模块专属结果码（数据不存在、操作失败等），通用结果码见 CommonResultCode
 *   3. 编码段 26xxx 与 ResultCode 顶层注释约定一致（bi=26xxx）
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum BiResultCode implements ResultCode {

    /** 报表不存在 */
    DATA_NOT_FOUND(26001, "报表不存在"),

    /** 操作失败 */
    OPERATION_FAILED(26002, "操作失败");

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
    BiResultCode(int code, String message) {
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
