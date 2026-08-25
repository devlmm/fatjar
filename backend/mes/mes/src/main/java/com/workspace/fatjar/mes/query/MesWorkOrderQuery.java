package com.workspace.fatjar.mes.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单分页查询条件
 * <p>
 * 继承 {@link PageQuery}（current/size），附加工单编号模糊查询与状态精确查询。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MesWorkOrderQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工单编号（模糊查询） */
    private String workOrderNo;

    /** 状态：0=新建 1=生产中 2=已完成（精确查询，null 表示不限） */
    private Integer status;
}
