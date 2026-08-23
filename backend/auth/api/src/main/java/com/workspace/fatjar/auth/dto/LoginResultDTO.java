package com.workspace.fatjar.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * 登录结果 DTO（Data Transfer Object）
 * <p>
 * 登录成功后返回前端的对象，包含 Token 与用户基础信息、角色编码集合、权限标识集合。
 * 前端通常将 Token 缓存到 localStorage，后续请求在 Authorization Header 携带 Bearer Token。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "登录结果")
public class LoginResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 登录 Token（Bearer 模式，前端后续请求在 Authorization Header 携带） */
    @Schema(description = "登录 Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    /** 用户 ID */
    @Schema(description = "用户 ID", example = "1234567890")
    private Long userId;

    /** 用户名（登录账号） */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /** 真实姓名（昵称，前端展示用） */
    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    /** 角色编码集合（前端用于按钮显隐控制） */
    @Schema(description = "角色编码集合", example = "[\"admin\",\"viewer\"]")
    private List<String> roles = Collections.emptyList();

    /** 权限标识集合（前端用于按钮显隐控制，如 system:user:add） */
    @Schema(description = "权限标识集合", example = "[\"system:user:add\",\"system:user:edit\"]")
    private List<String> permissions = Collections.emptyList();
}
