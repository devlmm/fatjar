package com.workspace.fatjar.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户-角色关联实体（对应 sys_user_role 表）
 * <p>
 * 设计说明：
 *   1. 用户与角色为多对多关系，通过本中间表关联
 *   2. 简单两字段关联表，不继承 BaseEntity（无 createTime/updateTime 等审计需求）
 *   3. 仅保留 id + userId + roleId 三个字段，由 BaseMapper 提供基础 CRUD
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@TableName("auth.sys_user_role")
public class SysUserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID（雪花算法 生成，IdType.INPUT 由应用层填充） */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 用户 ID（关联 sys_user.id） */
    private Long userId;

    /** 角色 ID（关联 sys_role.id） */
    private Long roleId;
}
