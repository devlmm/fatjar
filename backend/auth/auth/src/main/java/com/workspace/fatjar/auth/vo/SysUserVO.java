package com.workspace.fatjar.auth.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户视图对象（Controller 层返回前端）
 * <p>
 * 安全设计：不含 password 字段，从结构上避免响应泄露哈希密文。
 * 公共字段继承自 {@link BaseVO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统用户信息")
public class SysUserVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名（登录账号） */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 昵称（真实姓名） */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

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
