package com.workspace.fatjar.crm.resultcode;

import com.workspace.fatjar.common.result.ResultCode;

/**
 * 客户关系模块结果码枚举（CRM 模块，编码段 24xxx）
 * <p>
 * 设计说明：
 *   1. 实现 {@link ResultCode} 契约，由 {@link com.workspace.fatjar.crm.exception.CrmBizException} 统一引用
 *   2. 仅承载 CRM 模块专属结果码（客户不存在、操作失败）
 *   3. 通用结果码（成功、系统错误、参数无效等）仍使用 {@link com.workspace.fatjar.common.result.CommonResultCode}
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum CrmResultCode implements ResultCode {

    /** 客户不存在（按 ID 查询为空或已被逻辑删除） */
    DATA_NOT_FOUND(24001, "客户不存在"),

    /** 操作失败（新增/更新/删除未生效） */
    OPERATION_FAILED(24002, "操作失败");

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
    CrmResultCode(int code, String message) {
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
