package com.workspace.fatjar.bi.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 报表 VO（Controller 层返回前端）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BaseVO}，公共字段（id/createTime/updateTime）由父类提供
 *   2. 由 Controller 通过 {@link com.workspace.fatjar.bi.convert.BiReportConverter} 从 BO 转换
 *   3. 不含敏感字段与审计人字段
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BiReportVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 报表名称 */
    private String reportName;

    /** 报表类型 */
    private String reportType;

    /** 数据源标识 */
    private String dataSource;

    /** 状态：0=草稿 1=已发布 */
    private Integer status;
}
