package com.workspace.fatjar.common.exception;

import lombok.Getter;

/**
 * 业务异常（统一业务层抛出的运行时异常）
 * <p>
 * 设计说明：
 *   1. 继承 RuntimeException，避免受检异常对业务代码的侵入
 *   2. 持有 ErrorCode 枚举，统一错误码与文案，便于前端处理与国际化
 *   3. 由 GlobalExceptionHandler（starter-web）兜底转换为 R<Void> 返回
 * <p>
 * 使用示例：
 *   if (stock < num) {
 *       throw new BizException(ErrorCode.STOCK_NOT_ENOUGH, "当前库存：" + stock);
 *   }
 *
 * @author fatjar
 * @since 1.0.0
 */
@Getter
public class BizException extends RuntimeException {

    /** 错误码（对应 ErrorCode 枚举值） */
    private final int code;

    /**
     * 构造业务异常
     *
     * @param errorCode 错误码枚举
     */
    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 构造业务异常（自定义补充信息）
     *
     * @param errorCode 错误码枚举
     * @param detail    补充信息（会拼接到错误信息后）
     */
    public BizException(ErrorCode errorCode, String detail) {
        super(errorCode.getMessage() + (detail == null ? "" : ": " + detail));
        this.code = errorCode.getCode();
    }

    /**
     * 构造业务异常（带原始异常）
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public BizException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
    }
}
