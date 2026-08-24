package com.workspace.fatjar.scm.dto;

import com.workspace.fatjar.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购订单 DTO（跨模块传递的订单基础信息）
 * <p>
 * 跨模块调用时返回，仅包含对外必要字段
 * （不含内部字段 deptId 与审计字段 createTime/updateTime/createBy/updateBy/deleted）。
 * 主键 ID 继承自 {@link BaseDTO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "采购订单信息")
public class ScmPurchaseOrderDTO extends BaseDTO {

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

    /** 状态：0=待审批 1=已审批 2=已驳回 */
    @Schema(description = "状态：0=待审批 1=已审批 2=已驳回", example = "0")
    private Integer status;
}
