package com.workspace.fatjar.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户实体（对应 sys_user 表）
 * <p>
 * 字段说明：
 *   - username：登录账号，全局唯一
 *   - nickname：真实姓名，前端展示用
 *   - password：BCrypt 加密后的密码（注册/修改时由 PasswordEncoder.encode 处理）
 *   - phone：手机号（可用于找回密码 / 登录）
 *   - email：邮箱（可用于找回密码）
 *   - status：状态（0=启用，1=禁用，见 CommonStatusEnum）
 *   - tenantId：租户 ID（多租户隔离用，单租户场景可空）
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth.sys_user")
public class SysUser extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 登录账号（唯一） */
    private String username;

    /** 昵称（真实姓名） */
    private String nickname;

    /** 密码（BCrypt 加密） */
    private String password;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态：0=启用 1=禁用（见 CommonStatusEnum） */
    private Integer status;

    /** 租户 ID（多租户隔离） */
    private Long tenantId;
}
