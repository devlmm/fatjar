package com.workspace.fatjar.pm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目实体（对应 pm.project 表）
 * <p>
 * 字段说明：
 *   - projectNo：项目编号，项目唯一标识
 *   - projectName：项目名称
 *   - managerId：项目经理 ID（关联 hrm.employee，跨模块引用，仅存 ID 不存姓名）
 *   - startDate：项目开始日期
 *   - endDate：项目结束日期
 *   - budget：项目预算（BigDecimal 金额，避免浮点精度丢失）
 *   - status：项目状态（0=规划中，1=进行中，2=已完成，3=已取消）
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("pm.project")
public class PmProject extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 项目编号（唯一） */
    private String projectNo;

    /** 项目名称 */
    private String projectName;

    /** 项目经理 ID（关联 hrm 员工） */
    private Long managerId;

    /** 项目开始日期 */
    private LocalDate startDate;

    /** 项目结束日期 */
    private LocalDate endDate;

    /** 项目预算 */
    private BigDecimal budget;

    /** 项目状态：0=规划中 1=进行中 2=已完成 3=已取消 */
    private Integer status;
}
