package com.workspace.fatjar.scm.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购订单数据对象（DO，对应 scm.purchase_order 表）
 * <p>
 * 位于 Mapper 层，与数据库表一一对应。公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）
 * 继承自 {@link BaseDO}，本类仅声明业务字段。deptId 跨库关联 auth.sys_user。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm.purchase_order")
public class ScmPurchaseOrderDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 订单编号（业务唯一） */
    private String orderNo;

    /** 供应商名称 */
    private String supplierName;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 部门 ID（跨库关联 auth.sys_user） */
    private Long deptId;

    /** 状态：0=待审批 1=已审批 2=已驳回 */
    private Integer status;
}
