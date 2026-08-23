package com.workspace.fatjar.auth.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.auth.api.AuthApi;
import com.workspace.fatjar.auth.dto.LoginResultDTO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.dto.UserDTO;
import com.workspace.fatjar.auth.entity.SysMenu;
import com.workspace.fatjar.auth.entity.SysRole;
import com.workspace.fatjar.auth.entity.SysUser;
import com.workspace.fatjar.auth.mapper.SysMenuMapper;
import com.workspace.fatjar.auth.mapper.SysRoleMapper;
import com.workspace.fatjar.auth.mapper.SysUserMapper;
import com.workspace.fatjar.auth.ro.LoginRO;
import com.workspace.fatjar.auth.ro.RegisterRO;
import com.workspace.fatjar.auth.service.AuthService;
import com.workspace.fatjar.auth.util.JwtUtil;
import com.workspace.fatjar.common.constant.CommonConstants;
import com.workspace.fatjar.common.context.UserContext;
import com.workspace.fatjar.common.enums.CommonStatusEnum;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 权限模块 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;SysUserMapper, SysUser&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements AuthService + AuthApi，一个实现满足「内部」与「门面」双契约
 *   3. login 流程：验证码校验 -> 用户名查用户 -> 状态校验 -> BCrypt 密码比对 -> 生成 JWT -> 缓存权限角色到 Redis
 *   4. hasPermission 优先读 Redis 缓存（REDIS_KEY_PERMISSIONS+userId），未命中回源 DB 并回填缓存
 *   5. 菜单树构建：查菜单列表 -> 按 parentId 分组 -> 递归挂 children -> 取顶级（parentId=0）
 * <p>
 * 依赖注入（构造器注入，由 Lombok @RequiredArgsConstructor 生成）：
 *   - SysRoleMapper roleMapper        ：角色查询
 *   - SysMenuMapper menuMapper        ：菜单/权限查询
 *   - PasswordEncoder passwordEncoder ：BCrypt 密码校验（由 starter-security 的 SecurityBaseConfig 注册 Bean）
 *   - JwtUtil jwtUtil                  ：JWT 生成/解析
 *   - StringRedisTemplate redisTemplate：Redis 缓存
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements AuthService, AuthApi {

    /** 角色 Mapper（查询用户关联角色） */
    private final SysRoleMapper roleMapper;
    /** 菜单 Mapper（查询用户菜单与权限标识） */
    private final SysMenuMapper menuMapper;
    /** 密码编码器（BCrypt，由 starter-security 注册） */
    private final PasswordEncoder passwordEncoder;
    /** JWT 工具 */
    private final JwtUtil jwtUtil;
    /** Redis 操作模板（缓存 Token/权限/角色） */
    private final StringRedisTemplate redisTemplate;

    /** JWT 过期时间（秒，默认 86400 = 1 天，与 Token 缓存 TTL 保持一致） */
    @Value("${fatjar.jwt.expire:86400}")
    private long jwtExpire;

    /**
     * 图形验证码开关（DEV 默认关闭，保证开箱即用；SIT/PRD 通过 Nacos 配置 fatjar.auth.captcha-enabled=true 打开）
     * <p>
     * 关闭时：前端无需获取/提交验证码，后端直接跳过验证码校验环节。
     * 打开时：必须先 GET /auth/captcha 取 captchaKey+图形，再提交 captcha / captchaKey 两字段。
     */
    @Value("${fatjar.auth.captcha-enabled:false}")
    private boolean captchaEnabled;

    /**
     * 用户登录
     * <p>
     * 业务流程：
     *   1. 若 captchaEnabled=true：从 Redis 取验证码（key=REDIS_KEY_CAPTCHA+captchaKey），比对内容；否则跳过
     *   2. 用户名查询用户，校验是否存在
     *   3. 校验账号状态（禁用抛 ACCOUNT_DISABLED）
     *   4. BCrypt 比对密码（passwordEncoder.matches(raw, hashed)）
     *   5. 查询关联角色与权限标识集合
     *   6. 生成 JWT Token
     *   7. 缓存 Token / UserContext / 角色 / 权限到 Redis（统一 TTL）
     *   8. 组装 LoginResultDTO 返回
     *
     * @param ro 登录请求
     * @return 登录结果
     */
    @Override
    public LoginResultDTO login(LoginRO ro) {
        // 1. 验证码校验（仅在开启时执行；关闭则跳过，保证 admin/admin123 DEV 开箱即用）
        if (captchaEnabled) {
            String captchaCacheKey = CommonConstants.REDIS_KEY_CAPTCHA + ro.getCaptchaKey();
            String cachedCaptcha = redisTemplate.opsForValue().get(captchaCacheKey);
            if (cachedCaptcha == null) {
                throw new BizException(ErrorCode.PARAM_INVALID, "验证码已过期或不存在");
            }
            if (ro.getCaptcha() == null || !cachedCaptcha.equalsIgnoreCase(ro.getCaptcha().trim())) {
                throw new BizException(ErrorCode.PARAM_INVALID, "验证码错误");
            }
            // 验证码一次性使用，校验后删除
            redisTemplate.delete(captchaCacheKey);
        }

        // 2. 查询用户
        SysUser user = baseMapper.selectByUsername(ro.getUsername());
        if (user == null) {
            // 出于安全考虑，不区分用户不存在与密码错误，统一返回 BAD_CREDENTIALS
            throw new BizException(ErrorCode.BAD_CREDENTIALS);
        }

        // 3. 校验状态
        if (user.getStatus() != null
                && user.getStatus() == CommonStatusEnum.DISABLE.getCode()) {
            throw new BizException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 4. 校验密码（BCrypt matches）
        if (!passwordEncoder.matches(ro.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.BAD_CREDENTIALS);
        }

        // 5. 查询角色与权限
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getId());
        List<String> roleCodes = roles.stream()
                .map(SysRole::getRoleCode)
                .filter(code -> code != null && !code.isEmpty())
                .distinct()
                .collect(Collectors.toList());
        List<String> permissions = menuMapper.selectPermissionsByUserId(user.getId());

        // 6. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 7. 缓存到 Redis（Token + UserContext + 角色 + 权限，统一 TTL）
        UserContext ctx = UserContext.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .roles(new HashSet<>(roleCodes))
                .permissions(new HashSet<>(permissions))
                .tenantId(user.getTenantId())
                .token(token)
                .build();
        // 缓存 UserContext（JwtAuthenticationFilter 解析 Token 后从此处读取上下文）
        String tokenKey = CommonConstants.REDIS_KEY_TOKEN + user.getId();
        redisTemplate.opsForValue().set(tokenKey, JSONUtil.toJsonStr(ctx), jwtExpire, TimeUnit.SECONDS);
        // 单独缓存角色编码集合（便于权限判断快速读取）
        String rolesKey = CommonConstants.REDIS_KEY_ROLES + user.getId();
        redisTemplate.opsForValue().set(rolesKey, JSONUtil.toJsonStr(roleCodes), jwtExpire, TimeUnit.SECONDS);
        // 单独缓存权限标识集合（hasPermission 使用）
        String permsKey = CommonConstants.REDIS_KEY_PERMISSIONS + user.getId();
        redisTemplate.opsForValue().set(permsKey, JSONUtil.toJsonStr(permissions), jwtExpire, TimeUnit.SECONDS);

        log.info("用户登录成功：userId={}, username={}", user.getId(), user.getUsername());

        // 8. 组装返回
        LoginResultDTO result = new LoginResultDTO();
        result.setToken(token);
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        result.setNickname(user.getNickname());
        result.setRoles(roleCodes);
        result.setPermissions(permissions);
        return result;
    }

    /**
     * 根据用户 ID 查询用户基础信息（含角色编码集合）
     *
     * @param userId 用户 ID
     * @return 用户 DTO，不存在返回 null
     */
    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        SysUser user = getById(userId);
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setStatus(user.getStatus());
        // 查询关联角色编码
        List<SysRole> roles = roleMapper.selectRolesByUserId(userId);
        dto.setRoles(roles.stream()
                .map(SysRole::getRoleCode)
                .filter(code -> code != null && !code.isEmpty())
                .collect(Collectors.toList()));
        return dto;
    }

    /**
     * 根据用户 ID 获取菜单树
     * <p>
     * 流程：查询用户可见菜单列表 -> 转换为 DTO -> 按 parentId 分组 -> 递归挂 children -> 返回顶级
     *
     * @param userId 用户 ID
     * @return 菜单树
     */
    @Override
    public List<MenuDTO> getMenuTreeByUserId(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        List<SysMenu> menus = menuMapper.selectMenusByUserId(userId);
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        return buildMenuTree(menus);
    }

    /**
     * 校验用户是否拥有指定权限
     * <p>
     * 流程：
     *   1. 优先从 Redis 缓存读取权限集合（REDIS_KEY_PERMISSIONS+userId）
     *   2. 缓存未命中则回源 DB 查询并回填缓存
     *   3. 拥有通配权限 *:*:* 视为超管，直接放行
     *
     * @param userId     用户 ID
     * @param permission 权限标识
     * @return true 表示拥有
     */
    @Override
    public boolean hasPermission(Long userId, String permission) {
        if (userId == null || permission == null || permission.isEmpty()) {
            return false;
        }
        // 优先读缓存
        String permsKey = CommonConstants.REDIS_KEY_PERMISSIONS + userId;
        String permsJson = redisTemplate.opsForValue().get(permsKey);
        Set<String> perms;
        if (permsJson != null) {
            List<String> cached = JSONUtil.toList(permsJson, String.class);
            perms = new HashSet<>(cached);
        } else {
            // 缓存未命中，回源 DB
            List<String> list = menuMapper.selectPermissionsByUserId(userId);
            perms = new HashSet<>(list);
            // 回填缓存
            redisTemplate.opsForValue().set(permsKey, JSONUtil.toJsonStr(list), jwtExpire, TimeUnit.SECONDS);
        }
        // 超管通配权限放行
        if (perms.contains("*:*:*")) {
            return true;
        }
        return perms.contains(permission);
    }

    /**
     * 构建菜单树（按 parentId 分组递归挂 children）
     * <p>
     * 算法：
     *   1. 将所有 SysMenu 转换为 MenuDTO
     *   2. 按 parentId 分组（Map&lt;parentId, List&lt;MenuDTO&gt;&gt;）
     *   3. 遍历每个 DTO，从 Map 取其子节点列表挂到 children
     *   4. 返回 parentId == 0 的顶级菜单列表
     *
     * @param menus 数据库菜单列表
     * @return 菜单树
     */
    private List<MenuDTO> buildMenuTree(List<SysMenu> menus) {
        List<MenuDTO> dtos = menus.stream()
                .map(this::toMenuDTO)
                .collect(Collectors.toList());
        // 按 parentId 分组
        Map<Long, List<MenuDTO>> groupedByParent = dtos.stream()
                .collect(Collectors.groupingBy(MenuDTO::getParentId));
        // 设置 children
        for (MenuDTO dto : dtos) {
            List<MenuDTO> children = groupedByParent.get(dto.getId());
            if (children != null && !children.isEmpty()) {
                dto.setChildren(children);
            } else {
                dto.setChildren(new ArrayList<>());
            }
        }
        // 返回顶级菜单（parentId = 0）
        return dtos.stream()
                .filter(dto -> dto.getParentId() != null && dto.getParentId() == 0L)
                .collect(Collectors.toList());
    }

    /**
     * SysMenu 转 MenuDTO（字段逐一拷贝）
     *
     * @param menu 菜单实体
     * @return 菜单 DTO
     */
    private MenuDTO toMenuDTO(SysMenu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setParentId(menu.getParentId());
        dto.setName(menu.getName());
        dto.setPath(menu.getPath());
        dto.setComponent(menu.getComponent());
        dto.setIcon(menu.getIcon());
        dto.setType(menu.getType());
        dto.setPermission(menu.getPermission());
        dto.setSort(menu.getSort());
        return dto;
    }

    /**
     * 用户自助注册实现
     * <p>
     * 说明：
     *   - createBy / updateBy 类型已经统一为 String（BaseEntity），
     *     无登录态下 MetaObjectHandler 会兜底填 "system"；注册场景显式设置昵称与状态，
     *     id 字段由 IdGeneratorHolder 在 insert 时自动生成（雪花 ID）。
     */
    @Override
    public void register(RegisterRO ro) {
        // 1. 用户名唯一性校验（与未删除记录比对）
        Long existCount = baseMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, ro.getUsername())
                .eq(SysUser::getDeleted, CommonConstants.NOT_DELETED));
        if (existCount != null && existCount > 0) {
            throw new BizException(ErrorCode.PARAM_INVALID, "用户名已被占用");
        }
        // 2. 构建用户对象并入库（默认租户 DEFAULT_TENANT_ID，昵称=用户名，账号启用）
        SysUser user = new SysUser();
        user.setUsername(ro.getUsername());
        user.setNickname(ro.getUsername());
        user.setPassword(passwordEncoder.encode(ro.getPassword()));
        user.setStatus(CommonConstants.STATUS_ENABLE);
        user.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        // 注意：createBy / updateBy 在无登录态下会被 MetaObjectHandler 兜底填 "system"，符合最小权限审计预期
        baseMapper.insert(user);
        log.info("用户注册成功：username={}, userId={}", ro.getUsername(), user.getId());
    }
}
