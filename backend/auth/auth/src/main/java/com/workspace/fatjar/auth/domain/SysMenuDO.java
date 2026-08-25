package com.workspace.fatjar.auth.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统菜单数据对象（对应 auth.sys_menu 表）
 * <p>
 * 公共审计字段继承自 {@link BaseDO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("auth.sys_menu")
public class SysMenuDO extends BaseDO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 父菜单 ID（顶级菜单 parentId=0） */
    private Long parentId;

    /** 菜单名称（显示文本） */
    private String name;

    /** 路由路径（前端路由 path） */
    private String path;

    /** 组件路径（前端 Vue 组件路径，按钮型菜单可为空） */
    private String component;

    /** 图标（菜单图标 class 名） */
    private String icon;

    /** 菜单类型：0=目录 1=菜单 2=按钮 */
    private Integer type;

    /** 权限标识（按钮型菜单使用，如 system:user:add） */
    private String permission;

    /** 排序值（升序排列） */
    private Integer sort;

    /** 状态：0=正常 1=禁用 */
    private Integer status;
}
