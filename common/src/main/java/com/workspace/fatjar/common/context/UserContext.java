package com.workspace.fatjar.common.context;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

/**
 * 登录用户上下文（ThreadLocal 内传递）
 * <p>
 * 由 JwtAuthenticationFilter 解析 Token 后注入，业务层通过 UserContextHolder 获取。
 * 包含用户 ID、用户名、角色编码集合、权限标识集合、租户 ID。
 * <p>
 * 设计说明：实现 Serializable 便于存入 Redis 共享会话。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Builder
public class UserContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 用户 ID */
    private Long userId;
    /** 用户名（登录账号） */
    private String username;
    /** 真实姓名（昵称） */
    private String nickname;
    /** 角色编码集合（用于 RBAC） */
    @Builder.Default
    private Set<String> roles = Collections.emptySet();
    /** 权限标识集合（菜单按钮接口权限，如 system:user:add） */
    @Builder.Default
    private Set<String> permissions = Collections.emptySet();
    /** 租户 ID（多租户隔离，单租户场景可空） */
    private Long tenantId;
    /** 登录 Token（便于调用方携带下游链路） */
    private String token;

    /**
     * 判断是否拥有指定权限
     *
     * @param permission 权限标识（如 system:user:add）
     * @return true 表示拥有
     */
    public boolean hasPermission(String permission) {
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        // 超管（*:*:*）拥有所有权限
        return permissions.contains("*:*:*") || permissions.contains(permission);
    }

    /**
     * 判断是否拥有指定角色
     *
     * @param role 角色编码
     * @return true 表示拥有
     */
    public boolean hasRole(String role) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }
        // 超管角色 admin 拥有所有
        return roles.contains("admin") || roles.contains(role);
    }
}
