package com.workspace.fatjar.pm.dto;

import com.workspace.fatjar.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目 DTO（跨模块传递的项目基础信息）
 * <p>
 * 仅包含对外必要字段（不含 createTime/updateTime/createBy/updateBy/deleted 等审计字段，
 * 亦不含 startDate/endDate 等非跨模块必需字段）。
 * 主键 ID 继承自 {@link BaseDTO}。
 * <p>
 * 字段含义：
 *   - managerName：项目经理姓名（由 HrmEmployeeApi 反查，DO 中不存储，仅用于跨模块展示）
 *   - status：项目状态（0=规划中，1=进行中，2=已完成，3=已取消）
 *   - budget：项目预算（BigDecimal，金额场景避免浮点精度丢失）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "项目信息")
public class PmProjectDTO extends BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 项目编号 */
    @Schema(description = "项目编号", example = "PRJ2026001")
    private String projectNo;

    /** 项目名称 */
    @Schema(description = "项目名称", example = "fatjar 平台一期")
    private String projectName;

    /** 项目经理 ID（关联 hrm 员工） */
    @Schema(description = "项目经理 ID", example = "1001")
    private Long managerId;

    /** 项目经理姓名（跨模块反查，DO 不存储） */
    @Schema(description = "项目经理姓名", example = "张三")
    private String managerName;

    /** 项目状态：0=规划中 1=进行中 2=已完成 3=已取消 */
    @Schema(description = "项目状态：0=规划中 1=进行中 2=已完成 3=已取消", example = "1")
    private Integer status;

    /** 项目预算 */
    @Schema(description = "项目预算", example = "1000000.00")
    private BigDecimal budget;
}
