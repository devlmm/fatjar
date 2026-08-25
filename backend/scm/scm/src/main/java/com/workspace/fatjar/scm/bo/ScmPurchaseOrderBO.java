package com.workspace.fatjar.scm.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购订单业务对象（BO，Service 层输入输出）
 * <p>
 * 位于 Service 层，承载业务模型。公共字段（id/createTime/updateTime）继承自 {@link BaseBO}，
 * 本类仅声明业务字段。BO 与 DO 通过 MapStruct 双向转换。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScmPurchaseOrderBO extends BaseBO {

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
