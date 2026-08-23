package com.workspace.fatjar.bi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 报表 DTO（跨模块传递的报表基础信息）
 * <p>
 * 跨模块调用门面方法时返回，仅包含对外必要字段（不含内部审计字段 createBy/updateBy/deleted 等），
 * 避免数据库结构变更影响调用方。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "报表信息")
public class BiReportDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 报表 ID */
    @Schema(description = "报表 ID", example = "1234567890")
    private Long id;

    /** 报表名称 */
    @Schema(description = "报表名称", example = "月度销售汇总")
    private String reportName;

    /** 报表类型（如 table/chart/dashboard） */
    @Schema(description = "报表类型", example = "table")
    private String reportType;

    /** 数据源标识（如 mysql_main/clickhouse_log） */
    @Schema(description = "数据源", example = "mysql_main")
    private String dataSource;

    /** 状态：0=草稿 1=已发布 */
    @Schema(description = "状态：0=草稿 1=已发布", example = "1")
    private Integer status;
}
