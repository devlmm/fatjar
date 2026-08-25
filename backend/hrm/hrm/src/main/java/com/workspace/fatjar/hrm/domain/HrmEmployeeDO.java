package com.workspace.fatjar.hrm.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工 DO（与 hrm.employee 表一一对应）
 * <p>
 * 设计说明：
 *   1. 位于 Mapper 层，由 {@link com.workspace.fatjar.hrm.mapper.HrmEmployeeMapper} 操作
 *   2. 公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 {@link BaseDO}
 *   3. 仅声明业务字段，列名按驼峰转下划线自动映射（empNo -> emp_no）
 * <p>
 * 字段含义：
 *   - status：员工状态（0=在职，1=离职）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hrm.employee")
public class HrmEmployeeDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工号（唯一） */
    private String empNo;

    /** 姓名 */
    private String name;

    /** 部门 ID */
    private Long deptId;

    /** 职位 */
    private String position;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态：0=在职 1=离职 */
    private Integer status;
}
