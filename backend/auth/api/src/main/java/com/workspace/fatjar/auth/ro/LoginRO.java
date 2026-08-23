package com.workspace.fatjar.auth.ro;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 登录请求对象 RO（Request Object）
 * <p>
 * 用于接收前端登录请求参数，配合 Hibernate-Validator 进行非空校验，
 * 由 starter-web 全局拦截校验失败并转换为 R&lt;Void&gt; 错误响应。
 * <p>
 * 验证码策略：captcha / captchaKey 为<b>可选字段</b>，由后端配置 {@code fatjar.auth.captcha-enabled} 决定是否校验：
 *   - false（默认，DEV 推荐）：两字段留空即可登录，跳过验证码校验，便于开箱即用
 *   - true（SIT/PRD 安全加固）：必须传有效值，否则 400 / 验证码错误
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "登录请求对象")
public class LoginRO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 登录账号（用户名） */
    @Schema(description = "用户名", example = "admin", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 登录密码（明文，由后端 BCrypt.matches 与数据库密文比对；默认演示账号 admin/admin123） */
    @Schema(description = "密码", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码（可选；captcha-enabled=true 时必填，由 GET /auth/captcha 获取） */
    @Schema(description = "验证码（可选，SIT/PRD开启验证码时必填）", example = "a1b2")
    private String captcha;

    /** 验证码缓存 key（可选；captcha-enabled=true 时必填，GET /auth/captcha 返回） */
    @Schema(description = "验证码缓存 key（可选，SIT/PRD开启验证码时必填）", example = "uuid-xxxx-xxxx")
    private String captchaKey;
}
