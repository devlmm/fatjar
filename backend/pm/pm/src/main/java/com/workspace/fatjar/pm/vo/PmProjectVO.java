package com.workspace.fatjar.pm.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目 VO（Controller 层返回前端）
 * <p>
 * 设计说明：
 *   1. 位于 Controller 层，由 PmProjectController 通过 Converter 从 BO 转换，面向前端展示
 *   2. 公共字段（id/createTime/updateTime）继承自 {@link BaseVO}，不含敏感字段与审计人字段
 *   3. 与 BO 通过 {@link com.workspace.fatjar.pm.convert.PmProjectConverter}（MapStruct）单向转换（BO -> VO）
 *   4. startDate/endDate 使用 java.time.LocalDate（日期粒度，非 LocalDateTime）
 * <p>
 * 字段含义：
 *   - status：项目状态（0=规划中，1=进行中，2=已完成，3=已取消）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PmProjectVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 项目编号（唯一） */
    private String projectNo;

    /** 项目名称 */
    private String projectName;

    /** 项目经理 ID（关联 hrm.employee） */
    private Long managerId;

    /** 开始日期 */
    private LocalDate startDate;

    /** 结束日期 */
    private LocalDate endDate;

    /** 项目预算 */
    private BigDecimal budget;

    /** 项目状态：0=规划中 1=进行中 2=已完成 3=已取消 */
    private Integer status;
}
