package com.workspace.fatjar.bi.exception;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.ResultCode;
import java.io.Serial;

/**
 * BI 模块业务异常（增强 bi 异常语义）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BizException}，复用统一异常体系与全局异常处理
 *   2. 仅暴露 bi 模块专属结果码的构造，调用方使用 {@link com.workspace.fatjar.bi.resultcode.BiResultCode}
 *   3. 由 starter-web 的 GlobalExceptionHandler 兜底转换为 R&lt;Void&gt; 返回
 *
 * @author fatjar
 * @since 1.0.0
 */
public class BiBizException extends BizException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 构造 BI 业务异常（指定结果码）
     *
     * @param resultCode 结果码（如 BiResultCode.DATA_NOT_FOUND）
     */
    public BiBizException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造 BI 业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码
     * @param detail     补充信息（拼接到错误信息后）
     */
    public BiBizException(ResultCode resultCode, String detail) {
        super(resultCode, detail);
    }
}
