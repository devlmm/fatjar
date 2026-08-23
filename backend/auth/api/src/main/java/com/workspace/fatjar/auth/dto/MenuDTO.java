package com.workspace.fatjar.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * 菜单 DTO（用于前端渲染侧边栏与按钮权限）
 * <p>
 * 通过递归构建父子层级：顶级菜单 parentId=0，子菜单挂在父菜单的 children 列表中。
 * 字段对应数据库 sys_menu 表（除业务字段外，含 children 列表用于树形结构）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "菜单")
public class MenuDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 菜单 ID */
    @Schema(description = "菜单 ID", example = "1")
    private Long id;

    /** 父菜单 ID（顶级菜单 parentId=0） */
    @Schema(description = "父菜单 ID（顶级为 0）", example = "0")
    private Long parentId;

    /** 菜单名称（显示文本，如「用户管理」） */
    @Schema(description = "菜单名称", example = "用户管理")
    private String name;

    /** 路由路径（前端路由 path，如 /system/user） */
    @Schema(description = "路由路径", example = "/system/user")
    private String path;

    /** 组件路径（前端 Vue 组件路径，按钮型菜单可为空） */
    @Schema(description = "组件路径", example = "system/user/index")
    private String component;

    /** 图标（菜单图标 class 名，前端图标库） */
    @Schema(description = "图标", example = "icon-user")
    private String icon;

    /** 菜单类型：0=目录 1=菜单 2=按钮 */
    @Schema(description = "类型：0=目录 1=菜单 2=按钮", example = "1")
    private Integer type;

    /** 权限标识（按钮型菜单使用，如 system:user:add） */
    @Schema(description = "权限标识", example = "system:user:add")
    private String permission;

    /** 排序值（升序排列，便于前端按序展示） */
    @Schema(description = "排序值", example = "1")
    private Integer sort;

    /** 子菜单列表（递归构建的树形结构） */
    @Schema(description = "子菜单列表")
    private List<MenuDTO> children = new ArrayList<>();

    /**
     * 添加子菜单（便于递归构建时链式追加）
     *
     * @param child 子菜单
     */
    public void addChild(MenuDTO child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }
}
