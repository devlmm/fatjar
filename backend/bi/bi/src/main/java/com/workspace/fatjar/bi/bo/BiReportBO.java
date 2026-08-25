package com.workspace.fatjar.bi.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表 BO（Service 层业务对象）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BaseBO}，公共字段（id/createTime/updateTime）由父类提供
 *   2. 与 {@link com.workspace.fatjar.bi.domain.BiReportDO} 通过 MapStruct 双向转换
 *   3. 不含 createBy/updateBy/deleted 等审计字段（由 DO 承载）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BiReportBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 报表名称 */
    private String reportName;

    /** 报表类型（如 table/chart/dashboard） */
    private String reportType;

    /** 数据源标识 */
    private String dataSource;

    /** 状态：0=草稿 1=已发布 */
    private Integer status;
}
