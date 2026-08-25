package com.workspace.fatjar.auth.query;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色分页查询条件
 * <p>
 * 支持角色名称/角色编码模糊查询、状态精确查询；current/size 继承自 {@link PageQuery}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色分页查询条件")
public class SysRoleQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色名称（模糊） */
    @Schema(description = "角色名称（模糊）", example = "管理")
    private String roleName;

    /** 角色编码（模糊） */
    @Schema(description = "角色编码（模糊）", example = "admin")
    private String roleCode;

    /** 状态：0=正常 1=禁用（精确） */
    @Schema(description = "状态：0=正常 1=禁用", example = "0")
    private Integer status;
}
