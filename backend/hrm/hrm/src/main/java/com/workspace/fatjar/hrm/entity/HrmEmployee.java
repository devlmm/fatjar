package com.workspace.fatjar.hrm.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工实体（对应 hrm.employee 表）
 * <p>
 * 字段说明：
 *   - empNo：工号，企业内部员工唯一标识
 *   - name：员工姓名，前端展示用
 *   - deptId：所属部门 ID（关联部门表，跨模块引用）
 *   - position：职位名称
 *   - phone：联系电话
 *   - email：邮箱
 *   - status：员工状态（0=在职，1=离职），逻辑删除由 deleted 字段承载
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("hrm.employee")
public class HrmEmployee extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工号（员工唯一标识） */
    private String empNo;

    /** 员工姓名 */
    private String name;

    /** 部门 ID（关联部门表） */
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
