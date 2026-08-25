package com.workspace.fatjar.bi.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表分页查询参数
 * <p>
 * 继承 {@link PageQuery}（current/size），追加报表筛选条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BiReportQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 报表名称（模糊查询） */
    private String reportName;

    /** 报表类型（精确查询） */
    private String reportType;

    /** 状态：0=草稿 1=已发布 */
    private Integer status;
}
