package com.workspace.fatjar.auth;

import com.workspace.fatjar.auth.convert.SysUserConverter;
import com.workspace.fatjar.auth.domain.SysRoleDO;
import com.workspace.fatjar.auth.domain.SysUserDO;
import com.workspace.fatjar.auth.dto.LoginDTO;
import com.workspace.fatjar.auth.dto.LoginResultDTO;
import com.workspace.fatjar.auth.mapper.SysMenuMapper;
import com.workspace.fatjar.auth.mapper.SysRoleMapper;
import com.workspace.fatjar.auth.mapper.SysUserMapper;
import com.workspace.fatjar.auth.resultcode.AuthResultCode;
import com.workspace.fatjar.auth.service.impl.AuthServiceImpl;
import com.workspace.fatjar.auth.util.JwtUtil;
import com.workspace.fatjar.common.exception.BizException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * 登录鉴权测试（Auth 关键场景）
 * <p>
 * 测试场景（4 个）：
 *   1. 登录成功（验证码正确 + 用户存在 + 状态正常 + 密码匹配）
 *   2. 验证码已过期（Redis 取不到验证码）
 *   3. 用户不存在（selectByUsername 返回 null）
 *   4. 账号已禁用（status = DISABLE）
 * <p>
 * 说明：使用 Mockito 模拟 Mapper/Redis/PasswordEncoder/JwtUtil，
 * 验证 AuthServiceImpl 登录业务逻辑，不依赖 MySQL/Redis 真实环境。
 *
 * @author fatjar
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private SysUserMapper userMapper;
    @Mock private SysRoleMapper roleMapper;
    @Mock private SysMenuMapper menuMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;

    /** MapStruct 转换器（使用 Mappers.getMapper 兜底实例，无需 Spring 容器） */
    private final SysUserConverter converter = SysUserConverter.INSTANCE;

    private AuthServiceImpl authService;

    /**
     * 测试前置：构造 AuthServiceImpl（构造器注入 6 个依赖），
     * 反射注入继承的 baseMapper 与 @Value 字段 tokenExpireSeconds、captchaEnabled
     */
    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                converter, roleMapper, menuMapper, jwtUtil, redisTemplate, passwordEncoder);
        ReflectionTestUtils.setField(authService, "baseMapper", userMapper);
        ReflectionTestUtils.setField(authService, "tokenExpireSeconds", 86400L);
        ReflectionTestUtils.setField(authService, "captchaEnabled", false);
    }

    /**
     * 构造登录请求
     */
    private LoginDTO buildLoginDTO(String username, String password) {
        LoginDTO dto = new LoginDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setCaptcha("ABCD");
        dto.setCaptchaKey("test-key");
        return dto;
    }

    /**
     * 构造测试用户
     */
    private SysUserDO buildUser(Long id, String username, String hashedPwd, Integer status) {
        SysUserDO u = new SysUserDO();
        u.setId(id);
        u.setUsername(username);
        u.setNickname("测试用户");
        u.setPassword(hashedPwd);
        u.setStatus(status);
        return u;
    }

    /**
     * 场景 1：登录成功（captchaEnabled=false 跳过验证码校验）
     */
    @Test
    void login_success() {
        SysUserDO user = buildUser(1L, "admin", "$2a$hashedPwd", 0);
        when(userMapper.selectByUsername("admin")).thenReturn(user);
        when(passwordEncoder.matches("admin123", "$2a$hashedPwd")).thenReturn(true);
        when(roleMapper.selectRolesByUserId(1L)).thenReturn(Collections.singletonList(buildRole()));
        when(menuMapper.selectPermissionsByUserId(1L)).thenReturn(List.of("*:*:*"));
        when(jwtUtil.generateToken(1L, "admin")).thenReturn("token-xxx");
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        LoginResultDTO result = authService.login(buildLoginDTO("admin", "admin123"));

        assertNotNull(result, "登录成功应返回非空结果");
        assertEquals("token-xxx", result.getToken(), "Token 应与 JwtUtil 生成一致");
        assertEquals(1L, result.getUserId(), "用户 ID 应为 1");
        assertEquals("admin", result.getUsername(), "用户名应为 admin");
    }

    /**
     * 场景 2：验证码已过期（开启验证码校验后 Redis 取不到）
     */
    @Test
    void login_captchaExpired() {
        ReflectionTestUtils.setField(authService, "captchaEnabled", true);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.get(anyString())).thenReturn(null);

        BizException ex = assertThrows(BizException.class,
                () -> authService.login(buildLoginDTO("admin", "admin123")));
        assert ex.getCode() == AuthResultCode.CAPTCHA_EXPIRED.getCode()
                : "验证码过期应抛 CAPTCHA_EXPIRED";
    }

    /**
     * 场景 3：用户不存在（selectByUsername 返回 null）
     */
    @Test
    void login_userNotFound() {
        when(userMapper.selectByUsername("nouser")).thenReturn(null);

        BizException ex = assertThrows(BizException.class,
                () -> authService.login(buildLoginDTO("nouser", "any")));
        assert ex.getCode() == AuthResultCode.BAD_CREDENTIALS.getCode()
                : "用户不存在应抛 BAD_CREDENTIALS";
    }

    /**
     * 场景 4：账号已禁用
     */
    @Test
    void login_accountDisabled() {
        SysUserDO user = buildUser(1L, "admin", "$2a$hashedPwd", 1);
        when(userMapper.selectByUsername("admin")).thenReturn(user);

        BizException ex = assertThrows(BizException.class,
                () -> authService.login(buildLoginDTO("admin", "admin123")));
        assert ex.getCode() == AuthResultCode.ACCOUNT_DISABLED.getCode()
                : "账号禁用应抛 ACCOUNT_DISABLED";
    }

    /**
     * 构造测试角色
     */
    private SysRoleDO buildRole() {
        SysRoleDO r = new SysRoleDO();
        r.setId(1L);
        r.setRoleCode("admin");
        r.setRoleName("超管");
        return r;
    }
}
