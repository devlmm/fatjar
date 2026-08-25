package com.workspace.fatjar.crm.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户 VO（Controller 层返回前端）
 * <p>
 * 设计说明：
 *   1. 位于 Controller 层，由 CrmCustomerController 通过 Converter 从 BO 转换，面向前端展示
 *   2. 公共字段（id/createTime/updateTime）继承自 {@link BaseVO}，不含敏感字段与审计人字段
 *   3. 与 BO 通过 {@link com.workspace.fatjar.crm.convert.CrmCustomerConverter}（MapStruct）单向转换（BO -> VO）
 * <p>
 * 字段含义：
 *   - level：客户等级（0=普通，1=VIP，2=战略）
 *   - status：客户状态（0=潜在，1=正式，2=流失）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmCustomerVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 客户名称 */
    private String customerName;

    /** 联系人 */
    private String contact;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 客户等级：0=普通 1=VIP 2=战略 */
    private Integer level;

    /** 客户状态：0=潜在 1=正式 2=流失 */
    private Integer status;
}
