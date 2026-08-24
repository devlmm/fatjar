package com.workspace.fatjar.auth.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.auth.api.AuthApi;
import com.workspace.fatjar.auth.bo.SysUserBO;
import com.workspace.fatjar.auth.convert.SysUserConverter;
import com.workspace.fatjar.auth.domain.SysMenuDO;
import com.workspace.fatjar.auth.domain.SysRoleDO;
import com.workspace.fatjar.auth.domain.SysUserDO;
import com.workspace.fatjar.auth.dto.LoginDTO;
import com.workspace.fatjar.auth.dto.LoginResultDTO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.dto.RegisterDTO;
import com.workspace.fatjar.auth.dto.UserDTO;
import com.workspace.fatjar.auth.exception.AuthBizException;
import com.workspace.fatjar.auth.mapper.SysMenuMapper;
import com.workspace.fatjar.auth.mapper.SysRoleMapper;
import com.workspace.fatjar.auth.mapper.SysUserMapper;
import com.workspace.fatjar.auth.query.SysUserQuery;
import com.workspace.fatjar.auth.resultcode.AuthResultCode;
import com.workspace.fatjar.auth.service.AuthService;
import com.workspace.fatjar.auth.util.JwtUtil;
import com.workspace.fatjar.common.constant.CommonConstants;
import com.workspace.fatjar.common.result.PageResult;
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
import org.springframework.util.StringUtils;

/**
 * 权限模块 Service 实现（登录 / 注册 / 用户查询 / 菜单树 / 权限校验 / 用户 CRUD）
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;SysUserMapper, SysUserDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements AuthService + AuthApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link SysUserConverter}（MapStruct）双向转换
 *   4. 登录逻辑保留：BCrypt 密码校验、JWT 生成、Redis 缓存 UserContext/角色/权限、验证码校验
 *   5. 权限校验优先读 Redis 缓存（REDIS_KEY_PERMISSIONS+userId），缓存未命中回源 DB 并回填
 * <p>
 * 事务说明：register 写库（含密码加密）建议事务（由 ServiceImpl 默认 save 提供事务）；
 * 其余登录/查询/权限校验均为只读或缓存操作，无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO>
        implements AuthService, AuthApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final SysUserConverter converter;
    /** 角色 Mapper（查询用户关联角色） */
    private final SysRoleMapper sysRoleMapper;
    /** 菜单 Mapper（查询用户可访问菜单与权限标识） */
    private final SysMenuMapper sysMenuMapper;
    /** JWT 工具（生成 Token） */
    private final JwtUtil jwtUtil;
    /** Redis 操作模板（缓存 Token/角色/权限/验证码） */
    private final StringRedisTemplate redisTemplate;
    /** 密码编码器（BCrypt，注册/修改用户时加密密码） */
    private final PasswordEncoder passwordEncoder;

    /** 是否开启图形验证码校验（DEV 默认 false 便于开箱即用，SIT/PRD 建议开启） */
    @Value("${fatjar.auth.captcha-enabled:false}")
    private boolean captchaEnabled;

    /** 登录态缓存过期时间（秒，默认 86400 = 1 天，与 JWT 过期时间对齐） */
    @Value("${fatjar.auth.token-expire-seconds:86400}")
    private long tokenExpireSeconds;

    /**
     * 用户登录（账号 + 密码 + 验证码）
     * <p>
     * 业务流程：
     *   1. 校验验证码（captchaKey 取自 Redis，比对 captcha 内容；captcha-enabled=false 时跳过）
     *   2. 根据用户名查询用户，校验账号是否存在、是否禁用
     *   3. 使用 BCrypt PasswordEncoder.matches 比对密码
     *   4. 生成 JWT Token，将用户角色与权限集合缓存到 Redis
     *   5. 组装 LoginResultDTO 返回（含 Token、用户基础信息、角色、权限）
     *
     * @param dto 登录请求对象（用户名、密码、验证码、验证码 key）
     * @return 登录结果（Token + 用户信息 + 角色 + 权限）
     */
    @Override
    public LoginResultDTO login(LoginDTO dto) {
        // 1) 验证码校验（可由配置开关）
        if (captchaEnabled) {
            validateCaptcha(dto.getCaptchaKey(), dto.getCaptcha());
        }
        // 2) 根据用户名查询用户
        SysUserDO user = baseMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new AuthBizException(AuthResultCode.BAD_CREDENTIALS);
        }
        // 3) 账号禁用校验（status=1 表示禁用）
        if (user.getStatus() != null && user.getStatus() == CommonConstants.STATUS_DISABLE) {
            throw new AuthBizException(AuthResultCode.ACCOUNT_DISABLED);
        }
        // 4) BCrypt 密码校验
        if (!StringUtils.hasText(user.getPassword())
                || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthBizException(AuthResultCode.BAD_CREDENTIALS);
        }
        // 5) 查询角色编码集合与权限标识集合
        List<SysRoleDO> roles = sysRoleMapper.selectRolesByUserId(user.getId());
        List<String> roleCodes = roles == null ? Collections.emptyList()
                : roles.stream().map(SysRoleDO::getRoleCode).filter(StringUtils::hasText).collect(Collectors.toList());
        List<String> permissions = sysMenuMapper.selectPermissionsByUserId(user.getId());
        if (permissions == null) {
            permissions = Collections.emptyList();
        }
        // 6) 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        // 7) 缓存到 Redis：UserContext JSON / 角色集合 / 权限集合
        cacheLoginContext(user, token, roleCodes, permissions);
        // 8) 组装返回结果
        LoginResultDTO result = new LoginResultDTO();
        result.setToken(token);
        result.setUserId(user.getId());
        result.setUsername(user.getUsername());
        result.setNickname(user.getNickname());
        result.setRoles(roleCodes);
        result.setPermissions(permissions);
        log.info("用户登录成功：userId={}, username={}", user.getId(), user.getUsername());
        return result;
    }

    /**
     * 校验图形验证码
     * <p>
     * 流程：captchaKey 对应 Redis 缓存内容与用户输入 captcha 比对（不区分大小写），
     * 验证通过后删除缓存防止重放。
     *
     * @param captchaKey 验证码缓存 key
     * @param captcha    用户输入的验证码
     */
    private void validateCaptcha(String captchaKey, String captcha) {
        if (!StringUtils.hasText(captchaKey) || !StringUtils.hasText(captcha)) {
            throw new AuthBizException(AuthResultCode.CAPTCHA_EXPIRED, "验证码不能为空");
        }
        String redisKey = CommonConstants.REDIS_KEY_CAPTCHA + captchaKey;
        String cached = redisTemplate.opsForValue().get(redisKey);
        if (cached == null) {
            throw new AuthBizException(AuthResultCode.CAPTCHA_EXPIRED);
        }
        if (!cached.equalsIgnoreCase(captcha)) {
            throw new AuthBizException(AuthResultCode.CAPTCHA_INVALID);
        }
        // 验证通过后删除，防止重放
        redisTemplate.delete(redisKey);
    }

    /**
     * 缓存登录态到 Redis（UserContext JSON + 角色集合 + 权限集合）
     *
     * @param user        用户 DO
     * @param token       JWT Token
     * @param roleCodes   角色编码集合
     * @param permissions 权限标识集合
     */
    private void cacheLoginContext(SysUserDO user, String token,
                                   List<String> roleCodes, List<String> permissions) {
        Long userId = user.getId();
        // UserContext JSON（供 JwtAuthenticationFilter 回显登录态）
        JSONObject ctx = new JSONObject();
        ctx.set("token", token);
        ctx.set("userId", userId);
        ctx.set("username", user.getUsername());
        ctx.set("nickname", user.getNickname());
        ctx.set("roles", roleCodes);
        ctx.set("permissions", permissions);
        ctx.set("tenantId", user.getTenantId());
        redisTemplate.opsForValue().set(
                CommonConstants.REDIS_KEY_TOKEN + userId,
                ctx.toString(), tokenExpireSeconds, TimeUnit.SECONDS);
        // 角色集合
        redisTemplate.opsForValue().set(
                CommonConstants.REDIS_KEY_ROLES + userId,
                JSONUtil.toJsonStr(roleCodes), tokenExpireSeconds, TimeUnit.SECONDS);
        // 权限集合
        redisTemplate.opsForValue().set(
                CommonConstants.REDIS_KEY_PERMISSIONS + userId,
                JSONUtil.toJsonStr(permissions), tokenExpireSeconds, TimeUnit.SECONDS);
    }

    /**
     * 根据用户 ID 查询用户基础信息（返回对外 DTO）
     * <p>
     * 返回内容包含用户基础字段以及关联的角色编码集合，便于其他业务模块做权限判断。
     *
     * @param userId 用户 ID
     * @return 用户 DTO，用户不存在返回 null
     */
    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        SysUserDO user = getById(userId);
        if (user == null) {
            return null;
        }
        // 角色编码集合
        List<SysRoleDO> roles = sysRoleMapper.selectRolesByUserId(userId);
        List<String> roleCodes = roles == null ? Collections.emptyList()
                : roles.stream().map(SysRoleDO::getRoleCode).filter(StringUtils::hasText).collect(Collectors.toList());
        UserDTO dto = converter.toDTO(converter.toBO(user));
        dto.setRoles(roleCodes);
        return dto;
    }

    /**
     * 根据用户 ID 获取菜单树（递归构建父子层级）
     * <p>
     * 用于前端渲染侧边栏菜单与按钮权限标识。仅返回该用户有权访问的菜单。
     *
     * @param userId 用户 ID
     * @return 菜单树（顶级菜单 parentId=0），按 sort 升序排列
     */
    @Override
    public List<MenuDTO> getMenuTreeByUserId(Long userId) {
        List<SysMenuDO> menus = sysMenuMapper.selectMenusByUserId(userId);
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        return buildMenuTree(menus);
    }

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
     */
    @Override
    public boolean hasPermission(Long userId, String permission) {
        if (userId == null || !StringUtils.hasText(permission)) {
            return false;
        }
        Set<String> perms = loadPermissions(userId);
        // 超管（*:*:*）拥有所有权限
        return perms.contains("*:*:*") || perms.contains(permission);
    }

    /**
     * 加载用户权限集合（优先 Redis 缓存，未命中回源并回填）
     *
     * @param userId 用户 ID
     * @return 权限标识集合
     */
    private Set<String> loadPermissions(Long userId) {
        String permsKey = CommonConstants.REDIS_KEY_PERMISSIONS + userId;
        String cached = redisTemplate.opsForValue().get(permsKey);
        if (cached != null) {
            try {
                return new HashSet<>(JSONUtil.parseArray(cached).toList(String.class));
            } catch (Exception e) {
                log.warn("解析 Redis 权限缓存失败，回源 DB：userId={}, err={}", userId, e.getMessage());
            }
        }
        // 回源 DB 查询并回填缓存
        List<String> permList = sysMenuMapper.selectPermissionsByUserId(userId);
        Set<String> perms = permList == null ? new HashSet<>() : new HashSet<>(permList);
        redisTemplate.opsForValue().set(permsKey, JSONUtil.toJsonStr(perms),
                tokenExpireSeconds, TimeUnit.SECONDS);
        return perms;
    }

    /**
     * 用户自助注册
     * <p>
     * 业务规则：
     *   1. 用户名唯一性校验（与未删除记录比对），重复抛 USERNAME_EXISTS
     *   2. 密码 BCrypt 加密入库
     *   3. 账号默认启用，默认租户 id=DEFAULT_TENANT_ID，昵称=用户名（为空时回填）
     *   4. 默认不分配任何角色（最小权限原则，管理员可在后台分配）
     *
     * @param dto 注册请求对象
     */
    @Override
    public void register(RegisterDTO dto) {
        // 1) 用户名唯一性校验
        SysUserDO exists = baseMapper.selectByUsername(dto.getUsername());
        if (exists != null) {
            throw new AuthBizException(AuthResultCode.USERNAME_EXISTS, "用户名：" + dto.getUsername());
        }
        // 2) 组装 DO（密码 BCrypt 加密）
        SysUserDO user = new SysUserDO();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(StringUtils.hasText(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        user.setStatus(CommonConstants.STATUS_ENABLE);
        user.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        // 3) 入库
        boolean ok = save(user);
        if (!ok) {
            throw new AuthBizException(AuthResultCode.USERNAME_EXISTS, "注册失败，请稍后重试");
        }
        log.info("用户注册成功：userId={}, username={}", user.getId(), user.getUsername());
    }

    /**
     * 分页查询用户（返回 BO 分页结果）
     * <p>
     * 支持用户名/昵称模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（username/nickname/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<SysUserBO> pageBO(SysUserQuery query) {
        Page<SysUserDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()),
                SysUserDO::getUsername, query.getUsername());
        wrapper.like(StringUtils.hasText(query.getNickname()),
                SysUserDO::getNickname, query.getNickname());
        wrapper.eq(query.getStatus() != null, SysUserDO::getStatus, query.getStatus());
        wrapper.orderByDesc(SysUserDO::getCreateTime);
        Page<SysUserDO> result = page(page, wrapper);
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据用户 ID 查询用户（返回 BO）
     *
     * @param id 用户 ID
     * @return 用户 BO，用户不存在返回 null
     */
    @Override
    public SysUserBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        SysUserDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增用户（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 用户业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(SysUserBO bo) {
        SysUserDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改用户（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 用户业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(SysUserBO bo) {
        SysUserDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除用户（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 用户 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }

    /**
     * 构建菜单树（按 parentId 分组递归挂 children）
     * <p>
     * 算法：
     *   1. 将所有 SysMenuDO 转换为 MenuDTO
     *   2. 按 parentId 分组
     *   3. 遍历每个 DTO，从 Map 取其子节点列表挂到 children
     *   4. 返回 parentId == 0 的顶级菜单列表
     *
     * @param menus 菜单 DO 列表
     * @return 菜单树
     */
    private List<MenuDTO> buildMenuTree(List<SysMenuDO> menus) {
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
     * SysMenuDO 转 MenuDTO（字段逐一拷贝，剔除审计字段）
     *
     * @param menu 菜单 DO
     * @return 菜单 DTO
     */
    private MenuDTO toMenuDTO(SysMenuDO menu) {
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
}
