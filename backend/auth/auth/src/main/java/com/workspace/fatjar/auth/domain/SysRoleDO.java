package com.workspace.fatjar.auth.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色数据对象（对应 auth.sys_role 表）
 * <p>
 * 公共审计字段继承自 {@link BaseDO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth.sys_role")
public class SysRoleDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色编码（业务唯一，如 admin） */
    private String roleCode;

    /** 角色名称（展示用，如 管理员） */
    private String roleName;

    /** 状态：0=正常 1=禁用 */
    private Integer status;

    /** 备注 */
    private String remark;
}
