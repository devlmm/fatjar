package com.workspace.fatjar.bi.ro;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表分页查询请求对象 RO（Request Object）
 * <p>
 * 继承通用 PageQuery（提供 current/size 分页参数），扩展报表专属查询条件。
 * 配合 Hibernate-Validator 校验分页范围，由 starter-web 全局拦截校验失败。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报表分页查询请求")
public class BiReportPageRO extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 报表名称（模糊查询） */
    @Schema(description = "报表名称（模糊）", example = "销售")
    private String reportName;

    /** 状态：0=草稿 1=已发布（精确匹配，可空） */
    @Schema(description = "状态：0=草稿 1=已发布", example = "1")
    private Integer status;
}
