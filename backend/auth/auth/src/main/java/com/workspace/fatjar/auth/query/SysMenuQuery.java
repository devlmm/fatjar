package com.workspace.fatjar.auth.query;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单分页查询条件
 * <p>
 * 支持菜单名称模糊查询、类型/状态精确查询；current/size 继承自 {@link PageQuery}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单分页查询条件")
public class SysMenuQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 菜单名称（模糊） */
    @Schema(description = "菜单名称（模糊）", example = "用户")
    private String name;

    /** 类型：0=目录 1=菜单 2=按钮（精确） */
    @Schema(description = "类型：0=目录 1=菜单 2=按钮", example = "1")
    private Integer type;

    /** 状态：0=正常 1=禁用（精确） */
    @Schema(description = "状态：0=正常 1=禁用", example = "0")
    private Integer status;
}
