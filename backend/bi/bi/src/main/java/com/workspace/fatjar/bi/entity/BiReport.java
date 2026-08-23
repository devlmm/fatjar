package com.workspace.fatjar.bi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BI 报表实体（对应 bi.report 表，独立数据库 schema=bi）
 * <p>
 * 字段说明：
 *   - reportName：报表名称，前端展示用
 *   - reportType：报表类型（table/chart/dashboard/pivot 等）
 *   - dataSource：数据源标识（mysql_main/clickhouse_log 等，对应配置中心数据源）
 *   - status：状态（0=草稿，1=已发布；草稿仅创建人可见，已发布全员可见）
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bi.report")
public class BiReport extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 报表名称 */
    private String reportName;

    /** 报表类型（table/chart/dashboard 等） */
    private String reportType;

    /** 数据源标识（mysql_main/clickhouse_log 等） */
    private String dataSource;

    /** 状态：0=草稿 1=已发布 */
    private Integer status;
}
