package com.workspace.fatjar.auth.dto;

import com.workspace.fatjar.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户 DTO（跨模块传递的用户基础信息）
 * <p>
 * 跨模块调用 AuthApi.getUserById 时返回，仅包含对外必要字段（不含密码、租户等敏感/内部字段）。
 * 主键 ID 继承自 {@link BaseDTO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户信息")
public class UserDTO extends BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名（登录账号） */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 昵称（真实姓名） */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    /** 状态（0=启用，1=禁用，见 CommonStatusEnum） */
    @Schema(description = "状态：0=启用 1=禁用", example = "0")
    private Integer status;

    /** 角色编码集合（关联 sys_role.role_code） */
    @Schema(description = "角色编码集合", example = "[\"admin\"]")
    private List<String> roles = Collections.emptyList();
}
