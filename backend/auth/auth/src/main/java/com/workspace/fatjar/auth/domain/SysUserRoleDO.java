package com.workspace.fatjar.auth.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-角色关联数据对象（对应 auth.sys_user_role 表）
 * <p>
 * 关联表，仅 user_id/role_id/create_time 字段。继承 {@link BaseDO} 后，
 * BaseDO 中 update_time/create_by/update_by/deleted 在该表无对应列，
 * MyBatis-Plus 按列读写，多余审计列不影响基础 CRUD（save/removeById/selectList 等）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth.sys_user_role")
public class SysUserRoleDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    private Long userId;

    /** 角色 ID */
    private Long roleId;
}
