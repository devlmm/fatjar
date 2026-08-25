package com.workspace.fatjar.auth.exception;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.result.ResultCode;

/**
 * 权限模块业务异常
 * <p>
 * 继承 {@link BizException}，持有 {@link ResultCode} 实例（通常是
 * {@link com.workspace.fatjar.auth.resultcode.AuthResultCode}），由全局异常处理器兜底转换为统一返回 R。
 *
 * @author fatjar
 * @since 1.0.0
 */
public class AuthBizException extends BizException {

    /**
     * 构造权限模块业务异常（指定结果码）
     *
     * @param resultCode 结果码枚举（实现 ResultCode 接口）
     */
    public AuthBizException(ResultCode resultCode) {
        super(resultCode);
    }

    /**
     * 构造权限模块业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码枚举
     * @param detail     补充信息（会拼接到错误信息后）
     */
    public AuthBizException(ResultCode resultCode, String detail) {
        super(resultCode, detail);
    }
}
