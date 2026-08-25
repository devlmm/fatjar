package com.workspace.fatjar.auth.dto;

import com.workspace.fatjar.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录请求 DTO（用户登录入参）
 * <p>
 * 包含用户名、密码与图形验证码相关字段；验证码在 captcha-enabled=true 时强制校验。
 * 主键 ID 继承自 {@link BaseDTO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "登录请求")
public class LoginDTO extends BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名（登录账号） */
    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（明文，登录时经 BCrypt 校验） */
    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码缓存 key（GET /auth/captcha 返回） */
    @Schema(description = "验证码缓存 key")
    private String captchaKey;

    /** 用户输入的验证码字符 */
    @Schema(description = "验证码")
    private String captcha;
}
