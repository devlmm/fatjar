package com.workspace.fatjar.bi.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表 DO（与数据库表 bi.report 一一对应）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BaseDO}，公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）由父类提供
 *   2. 本类仅声明报表业务字段，@TableName 指定库表名 bi.report
 *   3. 仅在 Mapper/Service 内部使用，不跨模块传递
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bi.report")
public class BiReportDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 报表名称 */
    private String reportName;

    /** 报表类型（如 table/chart/dashboard） */
    private String reportType;

    /** 数据源标识（如 mysql_main/clickhouse_log） */
    private String dataSource;

    /** 状态：0=草稿 1=已发布 */
    private Integer status;
}
