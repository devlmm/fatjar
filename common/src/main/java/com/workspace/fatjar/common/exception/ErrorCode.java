package com.workspace.fatjar.common.exception;

/**
 * 错误码枚举（统一业务错误码定义）
 * <p>
 * 编码规则：
 *   - 10xxx：通用类错误（参数/系统/权限）
 *   - 11xxx：认证鉴权错误（auth 模块）
 *   - 20xxx：ERP 业务错误
 *   - 30xxx：OA  业务错误
 *   - 40xxx：CRM 业务错误
 *   - 50xxx：EMS 业务错误
 * <p>
 * 使用方式：throw new BizException(ErrorCode.PARAM_INVALID, "商品名不能为空");
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum ErrorCode {

    /* ============ 通用错误 10xxx ============ */
    SUCCESS(0, "操作成功"),
    SYSTEM_ERROR(10000, "系统异常"),
    PARAM_INVALID(10001, "参数校验失败"),
    DATA_NOT_FOUND(10002, "数据不存在"),
    DATA_DUPLICATED(10003, "数据已存在"),
    OPERATION_FAILED(10004, "操作失败"),
    UNSUPPORTED_OPERATION(10005, "不支持的操作"),

    /* ============ 认证鉴权 11xxx ============ */
    UNAUTHORIZED(11001, "未登录或登录已过期"),
    FORBIDDEN(11002, "无权限访问"),
    TOKEN_INVALID(11003, "Token 无效"),
    TOKEN_EXPIRED(11004, "Token 已过期"),
    ACCOUNT_LOCKED(11005, "账号已被锁定"),
    ACCOUNT_DISABLED(11006, "账号已被禁用"),
    BAD_CREDENTIALS(11007, "用户名或密码错误"),

    /* ============ ERP 业务 20xxx ============ */
    STOCK_NOT_ENOUGH(20001, "库存不足"),
    STOCK_VERSION_CONFLICT(20002, "库存版本冲突，请重试"),

    /* ============ EMS 资金 50xxx ============ */
    BALANCE_NOT_ENOUGH(50001, "账户余额不足"),
    ACCOUNT_FROZEN(50002, "账户已被冻结");

    /** 错误码 */
    private final int code;
    /** 错误信息 */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
