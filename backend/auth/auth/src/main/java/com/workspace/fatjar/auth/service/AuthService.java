package com.workspace.fatjar.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.auth.dto.LoginResultDTO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.dto.UserDTO;
import com.workspace.fatjar.auth.entity.SysUser;
import com.workspace.fatjar.auth.ro.LoginRO;
import com.workspace.fatjar.auth.ro.RegisterRO;
import java.util.List;

/**
 * 权限模块内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;SysUser&gt;，自动拥有基础 CRUD（save/getById/update/remove 等）
 *   2. 方法签名与 AuthApi 门面接口保持一致，便于实现类一次实现两个接口（双契约）
 *   3. 实现类 AuthServiceImpl 同时 implements AuthService + AuthApi
 * <p>
 * 与 AuthApi 的关系：AuthService 是「内部视角」（面向 service 层），AuthApi 是「外部视角」（面向跨模块调用），
 * 二者方法签名一致但语义不同，便于后续按需扩展内部独有方法。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface AuthService extends IService<SysUser> {

    /**
     * 用户登录（账号 + 密码 + 验证码）
     *
     * @param ro 登录请求对象
     * @return 登录结果（Token + 用户信息 + 角色 + 权限）
     */
    LoginResultDTO login(LoginRO ro);

    /**
     * 根据用户 ID 查询用户基础信息
     *
     * @param userId 用户 ID
     * @return 用户 DTO，用户不存在返回 null
     */
    UserDTO getUserById(Long userId);

    /**
     * 根据用户 ID 获取菜单树
     *
     * @param userId 用户 ID
     * @return 菜单树
     */
    List<MenuDTO> getMenuTreeByUserId(Long userId);

    /**
     * 校验用户是否拥有指定权限
     *
     * @param userId     用户 ID
     * @param permission 权限标识
     * @return true 表示拥有
     */
    boolean hasPermission(Long userId, String permission);

    /**
     * 用户自助注册
     * <p>
     * 业务规则：
     *   1. 用户名唯一性校验（与未删除记录比对），重复抛 "用户名已被占用"
     *   2. 密码 BCrypt 加密入库
     *   3. 账号默认启用，默认租户 id=DEFAULT_TENANT_ID，昵称=用户名
     *   4. 默认不分配任何角色（最小权限原则，管理员可在后台分配）
     *
     * @param ro 注册请求对象
     */
    void register(RegisterRO ro);
}
