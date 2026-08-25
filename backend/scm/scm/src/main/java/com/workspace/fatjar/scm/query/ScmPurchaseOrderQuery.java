package com.workspace.fatjar.scm.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 采购订单分页查询条件
 * <p>
 * 继承 {@link PageQuery}（current/size），附加订单编号模糊查询与状态精确查询。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScmPurchaseOrderQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 订单编号（模糊查询） */
    private String orderNo;

    /** 状态：0=待审批 1=已审批 2=已驳回（精确查询，null 表示不限） */
    private Integer status;
}
