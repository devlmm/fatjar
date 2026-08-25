package com.workspace.fatjar.fico.exception;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.ResultCode;
import lombok.Getter;

/**
 * 财务会计模块业务异常
 * <p>
 * 继承 {@link BizException}，增强 fico 模块异常语义；持有 {@link FicoResultCode} 等结果码。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Getter
public class FicoBizException extends BizException {

    /**
     * 构造财务会计业务异常（指定结果码）
     *
     * @param resultCode 结果码枚举
     */
    public FicoBizException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造财务会计业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码枚举
     * @param detail     补充信息
     */
    public FicoBizException(ResultCode resultCode, String detail) {
        super(resultCode, detail);
    }
}
