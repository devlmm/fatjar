package com.workspace.fatjar.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单实体（对应 sys_menu 表）
 * <p>
 * 同时承载三种功能：
 *   - 目录（type=0）：仅用于分组，前端渲染为可折叠菜单组
 *   - 菜单（type=1）：实际页面，对应前端路由 path 与组件 component
 *   - 按钮（type=2）：不展示为菜单，仅作为接口/按钮权限标识 permission
 * 通过 parentId 构建父子层级，顶级菜单 parentId=0。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth.sys_menu")
public class SysMenu extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 父菜单 ID（顶级为 0） */
    private Long parentId;

    /** 菜单名称（显示文本） */
    private String name;

    /** 路由路径（前端 router.path） */
    private String path;

    /** 组件路径（前端 Vue 组件） */
    private String component;

    /** 图标 class */
    private String icon;

    /** 类型：0=目录 1=菜单 2=按钮 */
    private Integer type;

    /** 权限标识（按钮型菜单使用，如 system:user:add） */
    private String permission;

    /** 排序值（升序） */
    private Integer sort;

    /** 状态：0=启用 1=禁用 */
    private Integer status;
}
