package com.workspace.fatjar.crm.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户 DO（与 crm.customer 表一一对应）
 * <p>
 * 设计说明：
 *   1. 位于 Mapper 层，由 {@link com.workspace.fatjar.crm.mapper.CrmCustomerMapper} 操作
 *   2. 公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 {@link BaseDO}
 *   3. 仅声明业务字段，列名按驼峰转下划线自动映射（customerName -> customer_name）
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
@TableName("crm.customer")
public class CrmCustomerDO extends BaseDO {

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
