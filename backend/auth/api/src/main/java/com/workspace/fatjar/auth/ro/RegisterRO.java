package com.workspace.fatjar.auth.ro;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 注册请求对象 RO（Request Object）
 * <p>
 * 用于接收前端注册请求参数，配合 Hibernate-Validator 进行非空与长度校验。
 * 注册成功后由后端 BCrypt 加密密码入库，默认账号启用、默认租户 id=1。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "注册请求对象")
public class RegisterRO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 注册用户名（唯一） */
    @Schema(description = "用户名", example = "newuser", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 32, message = "用户名长度必须在 3-32 个字符之间")
    private String username;

    /** 注册密码（明文，由后端 BCrypt.encode 入库） */
    @Schema(description = "密码", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度必须在 6-64 个字符之间")
    private String password;
}
