package com.workspace.fatjar.scm.exception;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.ResultCode;
import lombok.Getter;

/**
 * 供应链管理模块业务异常
 * <p>
 * 继承 {@link BizException}，增强 scm 模块异常语义；持有 {@link ScmResultCode} 等结果码。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Getter
public class ScmBizException extends BizException {

    /**
     * 构造供应链业务异常（指定结果码）
     *
     * @param resultCode 结果码枚举
     */
    public ScmBizException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造供应链业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码枚举
     * @param detail     补充信息
     */
    public ScmBizException(ResultCode resultCode, String detail) {
        super(resultCode, detail);
    }
}
