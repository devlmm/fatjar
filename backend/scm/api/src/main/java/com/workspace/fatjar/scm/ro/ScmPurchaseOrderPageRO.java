package com.workspace.fatjar.scm.ro;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购订单分页查询请求对象 RO（Request Object）
 * <p>
 * 继承 PageQuery 获取分页参数（current/size），并附加订单专有查询条件。
 * 由 Controller 绑定查询参数，配合 Hibernate-Validator 校验。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "采购订单分页查询")
public class ScmPurchaseOrderPageRO extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 订单编号（模糊查询，可空） */
    @Schema(description = "订单编号（模糊）", example = "PO-202608")
    private String orderNo;

    /** 状态（精确匹配，可空：0=待审批 1=已审批 2=已驳回） */
    @Schema(description = "状态：0=待审批 1=已审批 2=已驳回", example = "0")
    private Integer status;
}
