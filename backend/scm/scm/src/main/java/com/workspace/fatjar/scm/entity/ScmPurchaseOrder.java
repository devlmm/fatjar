package com.workspace.fatjar.scm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购订单实体（对应 scm.purchase_order 表）
 * <p>
 * 字段说明：
 *   - orderNo：订单编号，业务唯一
 *   - supplierName：供应商名称
 *   - totalAmount：订单总金额
 *   - deptId：归属部门 ID（用于跨模块调用 FICO 预算校验）
 *   - status：状态（0=待审批，1=已审批，2=已驳回）
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("scm.purchase_order")
public class ScmPurchaseOrder extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 订单编号（业务唯一） */
    private String orderNo;

    /** 供应商名称 */
    private String supplierName;

    /** 订单总金额 */
    private BigDecimal totalAmount;

    /** 归属部门 ID（用于跨模块 FICO 预算校验） */
    private Long deptId;

    /** 状态：0=待审批 1=已审批 2=已驳回 */
    private Integer status;
}
