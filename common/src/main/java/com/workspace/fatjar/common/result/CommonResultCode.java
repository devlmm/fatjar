package com.workspace.fatjar.common.result;

/**
 * 通用结果码枚举（Common 模块，编码段 10xxx）
 * <p>
 * 设计说明：
 *   1. 实现 {@link ResultCode} 契约，由 {@link com.workspace.fatjar.common.exception.BizException}
 *      与 {@link R} 统一引用
 *   2. 仅承载与具体业务无关的通用结果码（成功、系统错误、参数校验、鉴权等）
 *   3. 业务专属结果码（如 VOUCHER_NOT_FOUND、PURCHASE_ORDER_NOT_FOUND）放各业务 api 模块
 * <p>
 * 使用示例：
 *   return R.ok(data);
 *   throw new BizException(CommonResultCode.PARAM_INVALID, "用户名不能为空");
 *   return R.fail(CommonResultCode.SYSTEM_ERROR);
 *
 * @author fatjar
 * @since 1.0.0
 */
public enum CommonResultCode implements ResultCode {

    /** 成功 */
    SUCCESS(0, "成功"),

    /** 系统错误（未预期异常） */
    SYSTEM_ERROR(10000, "系统错误"),

    /** 参数无效（校验失败） */
    PARAM_INVALID(10001, "参数无效"),

    /** 未认证（未登录或 Token 失效） */
    UNAUTHORIZED(10002, "未认证"),

    /** 无权限（已登录但无访问权限） */
    FORBIDDEN(10003, "无权限"),

    /** 数据不存在（按 ID 查询为空） */
    DATA_NOT_FOUND(10004, "数据不存在"),

    /** 操作失败（新增/更新/删除未生效） */
    OPERATION_FAILED(10005, "操作失败"),

    /** 不支持的操作（状态机非法流转等） */
    UNSUPPORTED_OPERATION(10006, "不支持的操作"),

    /** 请求方法不允许 */
    METHOD_NOT_ALLOWED(10007, "请求方法不允许"),

    /** 请求过于频繁（限流触发） */
    TOO_MANY_REQUESTS(10008, "请求过于频繁"),

    /** 服务不可用 */
    SERVICE_UNAVAILABLE(10009, "服务不可用");

    /** 状态码 */
    private final int code;

    /** 提示信息 */
    private final String message;

    /**
     * 枚举构造
     *
     * @param code    状态码
     * @param message 提示信息
     */
    CommonResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
