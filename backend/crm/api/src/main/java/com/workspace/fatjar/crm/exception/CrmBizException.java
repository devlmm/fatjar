package com.workspace.fatjar.crm.exception;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.ResultCode;

/**
 * 客户关系模块业务异常
 * <p>
 * 设计说明：
 *   1. 继承 {@link BizException}，承载 CRM 模块专属错误码 {@link com.workspace.fatjar.crm.resultcode.CrmResultCode}
 *   2. 由 Controller / Service 在业务校验失败时抛出（如客户不存在、增删改未生效）
 *   3. 由 starter-web 的全局异常处理器兜底转换为 R&lt;Void&gt; 返回前端
 *
 * @author fatjar
 * @since 1.0.0
 */
public class CrmBizException extends BizException {

    /**
     * 构造客户关系业务异常（指定结果码）
     *
     * @param resultCode 结果码枚举（实现 ResultCode 接口）
     */
    public CrmBizException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造客户关系业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码枚举
     * @param detail     补充信息（拼接到错误信息后）
     */
    public CrmBizException(ResultCode resultCode, String detail) {
        super(resultCode, detail);
    }
}
