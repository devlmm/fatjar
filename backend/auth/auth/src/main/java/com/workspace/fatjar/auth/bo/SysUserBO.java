package com.workspace.fatjar.auth.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户业务对象（Service 层输入输出）
 * <p>
 * 字段同 SysUserDO 业务字段；与 DO 通过 MapStruct 双向转换。
 * 公共字段（id/createTime/updateTime）继承自 {@link BaseBO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统用户业务对象")
public class SysUserBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名（登录账号） */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 昵称（真实姓名） */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    /** 密码（新增/修改时为明文，Service/Controller 层负责 BCrypt 加密） */
    @Schema(description = "密码")
    private String password;

    /** 手机号 */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "admin@workspace.com")
    private String email;

    /** 状态：0=正常 1=禁用 */
    @Schema(description = "状态：0=正常 1=禁用", example = "0")
    private Integer status;

    /** 租户 ID */
    @Schema(description = "租户 ID", example = "1")
    private Long tenantId;
}
