package com.workspace.fatjar.crm.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户 BO（Service 层业务对象）
 * <p>
 * 设计说明：
 *   1. 位于 Service 层，由 CrmCustomerService 产出/接收，Controller 经 @RequestBody 接收
 *   2. 公共字段（id/createTime/updateTime）继承自 {@link BaseBO}，不含 createBy/updateBy/deleted 等审计字段
 *   3. 与 DO 通过 {@link com.workspace.fatjar.crm.convert.CrmCustomerConverter}（MapStruct）双向转换
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
public class CrmCustomerBO extends BaseBO {

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
