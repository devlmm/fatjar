package com.workspace.fatjar.pm.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目 DO（与 pm.project 表一一对应）
 * <p>
 * 设计说明：
 *   1. 位于 Mapper 层，由 {@link com.workspace.fatjar.pm.mapper.PmProjectMapper} 操作
 *   2. 公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 {@link BaseDO}
 *   3. 仅声明业务字段，列名按驼峰转下划线自动映射（projectNo -> project_no）
 *   4. managerId 跨库关联 hrm.employee.id，本表不存储项目经理姓名（由 HrmEmployeeApi 反查）
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
@TableName("pm.project")
public class PmProjectDO extends BaseDO {

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
