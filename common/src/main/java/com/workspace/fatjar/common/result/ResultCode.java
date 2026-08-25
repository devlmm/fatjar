package com.workspace.fatjar.common.result;

/**
 * 结果码契约接口（统一错误码与文案来源）
 * <p>
 * 设计说明：
 *   1. 通用结果码放 Common 模块（{@link CommonResultCode}，编码段 10xxx）
 *   2. 各业务 api 模块定义专属枚举实现本接口（auth=11xxx, fico=20xxx, scm=21xxx,
 *      mes=22xxx, hrm=23xxx, crm=24xxx, pm=25xxx, bi=26xxx, oa=27xxx）
 *   3. 约定：code == 0 表示成功，非 0 表示失败；{@link BizException} 持有本接口实例
 * <p>
 * 使用示例：
 *   public enum AuthResultCode implements ResultCode {
 *       BAD_CREDENTIALS(11001, "用户名或密码错误");
 *       private final int code;
 *       private final String message;
 *       AuthResultCode(int code, String message) { this.code = code; this.message = message; }
 *       public int getCode() { return code; }
 *       public String getMessage() { return message; }
 *   }
 *
 * @author fatjar
 * @since 1.0.0
 * @see CommonResultCode
 * @see com.workspace.fatjar.common.exception.BizException
 */
public interface ResultCode {

    /**
     * 获取错误码数值
     *
     * @return 状态码（0=成功，非 0=失败）
     */
    int getCode();

    /**
     * 获取提示信息
     *
     * @return 文案（用于前端展示或日志）
     */
    String getMessage();
}
