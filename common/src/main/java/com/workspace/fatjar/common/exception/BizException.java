package com.workspace.fatjar.common.exception;

import com.workspace.fatjar.common.result.CommonResultCode;
import com.workspace.fatjar.common.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常（统一业务层抛出的运行时异常）
 * <p>
 * 设计说明：
 *   1. 继承 RuntimeException，避免受检异常对业务代码的侵入
 *   2. 持有 {@link ResultCode} 实例，统一错误码与文案，便于前端处理与国际化
 *   3. 由 GlobalExceptionHandler（starter-web）兜底转换为 R&lt;Void&gt; 返回
 *   4. 各业务模块可继承本类定义模块专属异常（如 FicoBizException、ScmBizException），
 *      增强异常语义；模块异常类放各业务模块的 api 包内
 * <p>
 * 使用示例：
 *   if (stock &lt; num) {
 *       throw new BizException(CommonResultCode.PARAM_INVALID, "当前库存：" + stock);
 *   }
 *   if (balance &lt; amount) {
 *       throw new FicoBizException(FicoResultCode.BALANCE_NOT_ENOUGH, "余额：" + balance);
 *   }
 *
 * @author fatjar
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    /** 错误码（对应 ResultCode 实例） */
    private final ResultCode resultCode;

    /**
     * 构造业务异常（指定结果码）
     *
     * @param resultCode 结果码枚举（实现 ResultCode 接口）
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /**
     * 构造业务异常（结果码 + 补充信息）
     *
     * @param resultCode 结果码枚举
     * @param detail     补充信息（会拼接到错误信息后）
     */
    public BizException(ResultCode resultCode, String detail) {
        super(resultCode.getMessage() + (detail == null ? "" : ": " + detail));
        this.resultCode = resultCode;
    }

    /**
     * 构造业务异常（结果码 + 原始异常）
     *
     * @param resultCode 结果码枚举
     * @param cause      原始异常
     */
    public BizException(ResultCode resultCode, Throwable cause) {
        super(resultCode.getMessage(), cause);
        this.resultCode = resultCode;
    }

    /**
     * 获取错误码数值
     * <p>
     * 便捷方法，等价于 {@code getResultCode().getCode()}。
     *
     * @return 错误码数值
     */
    public int getCode() {
        return resultCode.getCode();
    }

    /**
     * 兼容构造：仅消息（默认 SYSTEM_ERROR）
     *
     * @param message 异常消息
     */
    public BizException(String message) {
        super(message);
        this.resultCode = CommonResultCode.SYSTEM_ERROR;
    }
}
