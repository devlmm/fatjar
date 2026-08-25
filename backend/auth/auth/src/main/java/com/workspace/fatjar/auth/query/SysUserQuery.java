package com.workspace.fatjar.auth.query;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户分页查询条件
 * <p>
 * 支持用户名/昵称模糊查询、状态精确查询；current/size 继承自 {@link PageQuery}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询条件")
public class SysUserQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户名（模糊） */
    @Schema(description = "用户名（模糊）", example = "admin")
    private String username;

    /** 昵称（模糊） */
    @Schema(description = "昵称（模糊）", example = "管理")
    private String nickname;

    /** 状态：0=正常 1=禁用（精确） */
    @Schema(description = "状态：0=正常 1=禁用", example = "0")
    private Integer status;
}
