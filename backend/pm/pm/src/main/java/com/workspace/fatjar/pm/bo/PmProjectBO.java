package com.workspace.fatjar.pm.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目 BO（Service 层业务对象）
 * <p>
 * 设计说明：
 *   1. 位于 Service 层，由 PmProjectService 产出/接收，Controller 经 @RequestBody 接收
 *   2. 公共字段（id/createTime/updateTime）继承自 {@link BaseBO}，不含 createBy/updateBy/deleted 等审计字段
 *   3. 与 DO 通过 {@link com.workspace.fatjar.pm.convert.PmProjectConverter}（MapStruct）双向转换
 *   4. managerId 跨库关联 hrm.employee，saveBO 时由 ServiceImpl 调用 HrmEmployeeApi 校验存在性
 *   5. startDate/endDate 使用 java.time.LocalDate（日期粒度，非 LocalDateTime）
 * <p>
 * 字段含义：
 *   - status：项目状态（0=规划中，1=进行中，2=已完成，3=已取消）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PmProjectBO extends BaseBO {

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
