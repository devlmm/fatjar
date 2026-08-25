package com.workspace.fatjar.auth.dto;

import com.workspace.fatjar.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户自助注册请求 DTO
 * <p>
 * 字段约束：用户名长度 3~32，密码长度 6~64。
 * 主键 ID 继承自 {@link BaseDTO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户注册请求")
public class RegisterDTO extends BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名（登录账号，唯一） */
    @Schema(description = "用户名", example = "newuser")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度需在 3~32 之间")
    private String username;

    /** 密码（明文，注册时经 BCrypt 加密后入库） */
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需在 6~64 之间")
    private String password;

    /** 昵称（为空时回填为用户名） */
    @Schema(description = "昵称", example = "新用户")
    private String nickname;
}
