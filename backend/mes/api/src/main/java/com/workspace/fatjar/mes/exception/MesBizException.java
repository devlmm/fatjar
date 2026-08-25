package com.workspace.fatjar.mes.exception;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.ResultCode;
import lombok.Getter;

/**
 * 制造执行系统模块业务异常
 * <p>
 * 继承 {@link BizException}，增强 mes 模块异常语义；持有 {@link MesResultCode} 等结果码。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Getter
public class MesBizException extends BizException {

    /**
     * 构造制造执行业务异常（指定结果码）
     *
     * @param resultCode 结果码枚举
     */
    public MesBizException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造制造执行业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码枚举
     * @param detail     补充信息
     */
    public MesBizException(ResultCode resultCode, String detail) {
        super(resultCode, detail);
    }
}
