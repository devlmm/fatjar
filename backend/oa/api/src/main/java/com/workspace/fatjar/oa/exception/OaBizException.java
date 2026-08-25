package com.workspace.fatjar.oa.exception;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.ResultCode;
import java.io.Serial;

/**
 * OA 模块业务异常（增强 oa 异常语义）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BizException}，复用统一异常体系与全局异常处理
 *   2. 仅暴露 oa 模块专属结果码的构造，调用方使用 {@link com.workspace.fatjar.oa.resultcode.OaResultCode}
 *   3. 由 starter-web 的 GlobalExceptionHandler 兜底转换为 R&lt;Void&gt; 返回
 *
 * @author fatjar
 * @since 1.0.0
 */
public class OaBizException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造 OA 业务异常（指定结果码）
     *
     * @param resultCode 结果码（如 OaResultCode.DATA_NOT_FOUND）
     */
    public OaBizException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造 OA 业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码
     * @param detail     补充信息（拼接到错误信息后）
     */
    public OaBizException(ResultCode resultCode, String detail) {
        super(resultCode, detail);
    }
}
