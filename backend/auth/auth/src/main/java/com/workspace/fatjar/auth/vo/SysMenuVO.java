package com.workspace.fatjar.auth.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单视图对象（Controller 层返回前端）
 * <p>
 * 公共字段继承自 {@link BaseVO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统菜单信息")
public class SysMenuVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 父菜单 ID（顶级菜单 parentId=0） */
    @Schema(description = "父菜单 ID（顶级为 0）", example = "0")
    private Long parentId;

    /** 菜单名称（显示文本） */
    @Schema(description = "菜单名称", example = "用户管理")
    private String name;

    /** 路由路径（前端路由 path） */
    @Schema(description = "路由路径", example = "/system/user")
    private String path;

    /** 组件路径（前端 Vue 组件路径，按钮型菜单可为空） */
    @Schema(description = "组件路径", example = "system/user/index")
    private String component;

    /** 图标（菜单图标 class 名） */
    @Schema(description = "图标", example = "icon-user")
    private String icon;

    /** 菜单类型：0=目录 1=菜单 2=按钮 */
    @Schema(description = "类型：0=目录 1=菜单 2=按钮", example = "1")
    private Integer type;

    /** 权限标识（按钮型菜单使用，如 system:user:add） */
    @Schema(description = "权限标识", example = "system:user:add")
    private String permission;

    /** 排序值（升序排列） */
    @Schema(description = "排序值", example = "1")
    private Integer sort;

    /** 状态：0=正常 1=禁用 */
    @Schema(description = "状态：0=正常 1=禁用", example = "0")
    private Integer status;
}
