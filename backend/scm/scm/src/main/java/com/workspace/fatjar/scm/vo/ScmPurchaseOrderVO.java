package com.workspace.fatjar.scm.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购订单视图对象（VO，Controller 层返回前端）
 * <p>
 * 位于 Controller 层，面向前端展示。公共字段（id/createTime/updateTime）继承自 {@link BaseVO}，
 * 本类仅声明展示字段。VO 由 Converter 从 BO 单向转换。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "采购订单信息")
public class ScmPurchaseOrderVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 订单编号（业务唯一） */
    @Schema(description = "订单编号", example = "PO-202608-0001")
    private String orderNo;

    /** 供应商名称 */
    @Schema(description = "供应商名称", example = "华盛科技")
    private String supplierName;

    /** 订单总金额 */
    @Schema(description = "订单总金额", example = "98000.00")
    private BigDecimal totalAmount;

    /** 部门 ID（跨库关联 auth.sys_user） */
    @Schema(description = "部门 ID", example = "1")
    private Long deptId;

    /** 状态：0=待审批 1=已审批 2=已驳回 */
    @Schema(description = "状态：0=待审批 1=已审批 2=已驳回", example = "0")
    private Integer status;
}
