package com.workspace.fatjar.auth.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户数据对象（对应 auth.sys_user 表）
 * <p>
 * 仅在 Mapper/Service 内部使用；对外用 UserDTO，对 Controller 用 SysUserVO，对 Service 用 SysUserBO。
 * 公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 {@link BaseDO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth.sys_user")
public class SysUserDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名（登录账号，唯一） */
    private String username;

    /** 昵称（真实姓名） */
    private String nickname;

    /** 密码（BCrypt 密文，VARCHAR(128)） */
    private String password;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态：0=正常 1=禁用 */
    private Integer status;

    /** 租户 ID */
    private Long tenantId;
}
