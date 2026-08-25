package com.workspace.fatjar.oa.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批分页查询参数
 * <p>
 * 继承 {@link PageQuery}（current/size），追加审批筛选条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OaApprovalQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 审批标题（模糊查询） */
    private String title;

    /** 审批类型（精确查询） */
    private String type;

    /** 状态：0=待审批 1=已通过 2=已驳回 */
    private Integer status;
}
