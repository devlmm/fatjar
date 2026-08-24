package com.workspace.fatjar.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.auth.bo.SysUserBO;
import com.workspace.fatjar.auth.domain.SysUserDO;
import com.workspace.fatjar.auth.dto.LoginDTO;
import com.workspace.fatjar.auth.dto.LoginResultDTO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.dto.RegisterDTO;
import com.workspace.fatjar.auth.dto.UserDTO;
import com.workspace.fatjar.auth.query.SysUserQuery;
import com.workspace.fatjar.common.result.PageResult;
import java.util.List;

/**
 * 权限模块内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;SysUserDO&gt;，自动拥有基础 CRUD（save/getById/update/remove 等）
 *   2. 方法签名与 AuthApi 门面接口保持一致，便于实现类一次实现两个接口（双契约）
 *   3. 实现类 AuthServiceImpl 同时 implements AuthService + AuthApi
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 * <p>
 * 与 AuthApi 的关系：AuthService 是「内部视角」（面向 service 层），AuthApi 是「外部视角」（面向跨模块调用），
 * 二者方法签名一致但语义不同，便于后续按需扩展内部独有方法。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface AuthService extends IService<SysUserDO> {

    /**
     * 用户登录（账号 + 密码 + 验证码）
     *
     * @param dto 登录请求对象
     * @return 登录结果（Token + 用户信息 + 角色 + 权限）
     */
    LoginResultDTO login(LoginDTO dto);

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
     * @param dto 注册请求对象
     */
    void register(RegisterDTO dto);

    /**
     * 分页查询用户（返回 BO 分页结果）
     *
     * @param query 分页查询条件（username/nickname/status + current/size）
     * @return BO 分页结果
     */
    PageResult<SysUserBO> pageBO(SysUserQuery query);

    /**
     * 根据用户 ID 查询用户（返回 BO）
     *
     * @param id 用户 ID
     * @return 用户 BO，用户不存在返回 null
     */
    SysUserBO getBOById(Long id);

    /**
     * 新增用户（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 用户业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(SysUserBO bo);

    /**
     * 修改用户（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 用户业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(SysUserBO bo);

    /**
     * 根据 ID 删除用户（逻辑删除）
     *
     * @param id 用户 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
