package com.workspace.fatjar.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 角色-菜单关联实体（对应 sys_role_menu 表）
 * <p>
 * 设计说明：
 *   1. 角色与菜单为多对多关系，通过本中间表关联
 *   2. 简单两字段关联表，不继承 BaseEntity（无审计字段需求）
 *   3. 仅保留 id + roleId + menuId 三个字段，由 BaseMapper 提供基础 CRUD
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@TableName("auth.sys_role_menu")
public class SysRoleMenu implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID（雪花 Algorithm 生成，IdType.INPUT 由应用层填充） */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 角色 ID（关联 sys_role.id） */
    private Long roleId;

    /** 菜单 ID（关联 sys_menu.id） */
    private Long menuId;
}
