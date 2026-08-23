package com.workspace.fatjar.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色实体（对应 sys_role 表）
 * <p>
 * 角色是 RBAC 模型的中间桥梁：用户通过 sys_user_role 关联多个角色，
 * 角色通过 sys_role_menu 关联多个菜单/按钮，从而获得对应权限。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth.sys_role")
public class SysRole extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色编码（唯一，如 admin、viewer，前端按钮显隐控制用） */
    private String roleCode;

    /** 角色名称（如「超级管理员」） */
    private String roleName;

    /** 状态：0=启用 1=禁用（见 CommonStatusEnum） */
    private Integer status;

    /** 备注 */
    private String remark;
}
