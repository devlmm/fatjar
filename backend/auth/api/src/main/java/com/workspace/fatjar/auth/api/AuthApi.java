package com.workspace.fatjar.auth.api;

import com.workspace.fatjar.auth.dto.LoginDTO;
import com.workspace.fatjar.auth.dto.LoginResultDTO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.dto.UserDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

/**
 * 权限模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-auth-api 模块，对外暴露 auth 模块的核心能力
 *   2. 实现类 AuthServiceImpl 同时实现本接口与内部 AuthService 接口，一个实现满足双契约
 *   3. 其他业务模块（erp/oa/crm/ems）通过依赖 fatjar-auth-api 即可调用本接口，
 *      无需感知 auth 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 实际 HTTP 入口由 AuthController 调用，门面接口主要服务于跨模块 RPC 风格调用
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "权限模块门面", description = "暴露给其他业务模块调用的权限能力契约")
public interface AuthApi {

    /**
     * 用户登录（账号 + 密码 + 验证码）
     * <p>
     * 业务流程：
     *   1. 校验验证码（captchaKey 取自 Redis，比对 captcha 内容）
     *   2. 根据用户名查询用户，校验账号是否存在、是否禁用
     *   3. 使用 BCrypt PasswordEncoder.matches 比对密码
     *   4. 生成 JWT Token，将用户角色与权限集合缓存到 Redis
     *   5. 组装 LoginResultDTO 返回（含 Token、用户基础信息、角色、权限）
     *
     * @param dto 登录请求对象（用户名、密码、验证码、验证码 key）
     * @return 登录结果（Token + 用户信息 + 角色 + 权限）
     * @author fatjar
     * @since 1.0.0
     */
    LoginResultDTO login(LoginDTO dto);

    /**
     * 根据用户 ID 查询用户基础信息（跨模块调用入口）
     * <p>
     * 返回内容包含用户基础字段以及关联的角色编码集合，便于其他业务模块做权限判断。
     *
     * @param userId 用户 ID
     * @return 用户 DTO（id/username/nickname/status/roles），用户不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    UserDTO getUserById(Long userId);

    /**
     * 根据用户 ID 获取菜单树（递归构建父子层级）
     * <p>
     * 用于前端渲染侧边栏菜单与按钮权限标识。仅返回该用户有权访问的菜单。
     *
     * @param userId 用户 ID
     * @return 菜单树（顶级菜单 parentId=0），按 sort 升序排列
     * @author fatjar
     * @since 1.0.0
     */
    List<MenuDTO> getMenuTreeByUserId(Long userId);

    /**
     * 校验用户是否拥有指定权限标识
     * <p>
     * 判断顺序：
     *   1. 优先从 Redis 缓存的权限集合判断（REDIS_KEY_PERMISSIONS+userId）
     *   2. 缓存未命中则回源 DB 查询并回填缓存
     *   3. 拥有通配权限 *:*:* 视为超管，直接放行
     *
     * @param userId     用户 ID
     * @param permission 权限标识（如 system:user:add）
     * @return true 表示拥有该权限
     * @author fatjar
     * @since 1.0.0
     */
    boolean hasPermission(Long userId, String permission);
}
