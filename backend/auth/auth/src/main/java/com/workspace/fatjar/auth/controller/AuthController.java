package com.workspace.fatjar.auth.controller;

import com.workspace.fatjar.auth.dto.LoginResultDTO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.dto.UserDTO;
import com.workspace.fatjar.auth.ro.LoginRO;
import com.workspace.fatjar.auth.ro.RegisterRO;
import com.workspace.fatjar.auth.service.AuthService;
import com.workspace.fatjar.common.constant.CommonConstants;
import com.workspace.fatjar.common.context.UserContext;
import com.workspace.fatjar.common.context.UserContextHolder;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限模块入口控制器（登录/登出/用户信息/菜单/验证码）
 * <p>
 * 路径前缀：/auth
 * 接口列表：
 *   - GET  /auth/captcha  ：获取图形验证码（返回 JSON: { captchaKey, imageBase64 }；captcha-enabled=false 时仍可调用，但登录不强制校验）
 *   - POST /auth/login   ：账号密码登录（permitAll，SecurityConfig 放行）
 *   - POST /auth/logout  ：登出，清除 Redis 中 Token/权限/角色缓存
 *   - GET  /auth/userInfo：获取当前登录用户信息（含角色）
 *   - GET  /auth/info    ：/auth/userInfo 的兼容别名（兼容旧前端与 SDK）
 *   - GET  /auth/menus   ：获取当前登录用户的菜单树
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "权限模块", description = "登录/登出/用户信息/菜单/验证码")
public class AuthController {

    /** 权限 Service（含登录、用户查询、菜单树、权限校验） */
    private final AuthService authService;
    /** Redis 操作模板（登出时清除缓存 + 验证码存储） */
    private final StringRedisTemplate redisTemplate;

    /** 图形验证码过期时间（秒，默认 120s = 2 分钟） */
    @Value("${fatjar.auth.captcha-expire-seconds:120}")
    private long captchaExpireSeconds;

    /** 图形验证码尺寸（宽） */
    private static final int CAPTCHA_WIDTH = 140;
    /** 图形验证码尺寸（高） */
    private static final int CAPTCHA_HEIGHT = 42;
    /** 图形验证码字符集（去掉易混淆字符 0/O/1/I/l） */
    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    /** 图形验证码字符长度 */
    private static final int CAPTCHA_LEN = 4;
    /** 图形验证码干扰线数量 */
    private static final int CAPTCHA_LINES = 6;
    /** 图形验证码字体 */
    private static final Font CAPTCHA_FONT = new Font("SansSerif", Font.BOLD, 28);

    /**
     * 用户登录
     *
     * @param ro 登录请求（用户名/密码/验证码/验证码 key）
     * @return 登录结果（Token + 用户信息 + 角色 + 权限）
     */
    @Operation(summary = "用户登录", description = "账号 + 密码 + 验证码登录，返回 Token")
    @PostMapping("/login")
    public R<LoginResultDTO> login(@Parameter(description = "登录请求") @Valid @RequestBody LoginRO ro) {
        LoginResultDTO result = authService.login(ro);
        return R.ok(result);
    }

    /**
     * 用户登出
     * <p>
     * 业务流程：
     *   1. 从 UserContextHolder 取当前登录用户 ID
     *   2. 删除 Redis 中 Token / 角色 / 权限 三类缓存 key
     *
     * @return 操作结果
     */
    @Operation(summary = "用户登出", description = "清除 Redis 中的 Token/角色/权限缓存")
    @PostMapping("/logout")
    public R<Void> logout() {
        UserContext ctx = UserContextHolder.get();
        if (ctx == null || ctx.getUserId() == null) {
            // 未登录直接返回成功（幂等）
            return R.ok();
        }
        Long userId = ctx.getUserId();
        // 删除 Token 缓存
        redisTemplate.delete(CommonConstants.REDIS_KEY_TOKEN + userId);
        // 删除角色缓存
        redisTemplate.delete(CommonConstants.REDIS_KEY_ROLES + userId);
        // 删除权限缓存
        redisTemplate.delete(CommonConstants.REDIS_KEY_PERMISSIONS + userId);
        log.info("用户登出成功：userId={}", userId);
        return R.ok();
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息 DTO（含角色编码集合）
     */
    @Operation(summary = "获取当前登录用户信息", description = "返回用户基础信息与角色编码集合")
    @GetMapping("/userInfo")
    public R<UserDTO> userInfo() {
        Long userId = UserContextHolder.currentUserId();
        UserDTO dto = authService.getUserById(userId);
        if (dto == null) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "用户上下文已失效，请重新登录");
        }
        return R.ok(dto);
    }

    /**
     * 获取当前用户的菜单树
     *
     * @return 菜单树
     */
    @Operation(summary = "获取当前用户菜单树", description = "用于前端侧边栏渲染与按钮权限控制")
    @GetMapping("/menus")
    public R<List<MenuDTO>> menus() {
        Long userId = UserContextHolder.currentUserId();
        List<MenuDTO> tree = authService.getMenuTreeByUserId(userId);
        return R.ok(tree);
    }

    /**
     * /auth/userInfo 兼容别名（旧前端 / 自定义 SDK 可能写成 /auth/info，避免 404）
     *
     * @return 同 /auth/userInfo
     */
    @Operation(summary = "获取当前登录用户信息（兼容别名 /auth/info）")
    @GetMapping({"/info"})
    public R<UserDTO> userInfoAlias() {
        return userInfo();
    }

    /**
     * 获取图形验证码（SIT/PRD 开启 captcha-enabled 时使用；DEV 关闭时不强制校验但仍可调用）
     * <p>
     * 流程：
     *   1. 生成 UUID captchaKey 与 4 位随机字符答案
     *   2. 答案写入 Redis（key=fatjar:auth:captcha:captchaKey，TTL=2 分钟）
     *   3. 绘制 PNG 图形（42x140）+ 干扰线
     *   4. Base64 编码后与 captchaKey 一起 JSON 返回
     *   5. 前端：先 GET /auth/captcha 拿 captchaKey + 图片显示，登录时提交 captchaKey + 用户输入的 captcha
     *
     * @return { captchaKey, imageBase64 }
     * @throws IOException PNG 编码异常
     */
    @Operation(summary = "获取图形验证码", description = "返回 captchaKey 与 PNG Base64，SIT/PRD 登录时需一并提交")
    @GetMapping("/captcha")
    public R<Map<String, String>> captcha(HttpServletResponse response) throws IOException {
        // 1) 生成 4 位随机字符答案
        Random rnd = new Random();
        StringBuilder answer = new StringBuilder(CAPTCHA_LEN);
        for (int i = 0; i < CAPTCHA_LEN; i++) {
            answer.append(CAPTCHA_CHARS.charAt(rnd.nextInt(CAPTCHA_CHARS.length())));
        }
        // 2) 生成 captchaKey 并写 Redis
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        String redisKey = CommonConstants.REDIS_KEY_CAPTCHA + captchaKey;
        redisTemplate.opsForValue().set(redisKey, answer.toString(), captchaExpireSeconds, TimeUnit.SECONDS);

        // 3) 绘制 PNG 图形
        BufferedImage image = new BufferedImage(CAPTCHA_WIDTH, CAPTCHA_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            // 背景
            g.setColor(new Color(245, 248, 252));
            g.fillRect(0, 0, CAPTCHA_WIDTH, CAPTCHA_HEIGHT);
            // 边框
            g.setColor(new Color(200, 210, 225));
            g.drawRect(0, 0, CAPTCHA_WIDTH - 1, CAPTCHA_HEIGHT - 1);
            // 字体
            g.setFont(CAPTCHA_FONT);
            for (int i = 0; i < CAPTCHA_LEN; i++) {
                char ch = answer.charAt(i);
                // 每个字符颜色略有差异
                g.setColor(new Color(30 + rnd.nextInt(120), 50 + rnd.nextInt(140), 80 + rnd.nextInt(150)));
                // 轻微随机旋转与偏移
                int x = 12 + i * 30;
                int y = CAPTCHA_HEIGHT - 10 + rnd.nextInt(6);
                g.drawString(String.valueOf(ch), x, y);
            }
            // 干扰线
            for (int i = 0; i < CAPTCHA_LINES; i++) {
                g.setColor(new Color(180 + rnd.nextInt(50), 180 + rnd.nextInt(50), 190 + rnd.nextInt(50)));
                int x1 = rnd.nextInt(CAPTCHA_WIDTH), y1 = rnd.nextInt(CAPTCHA_HEIGHT);
                int x2 = rnd.nextInt(CAPTCHA_WIDTH), y2 = rnd.nextInt(CAPTCHA_HEIGHT);
                g.drawLine(x1, y1, x2, y2);
            }
        } finally {
            g.dispose();
        }

        // 4) Base64 编码 PNG（DataURL 可直接 <img src="data:image/png;base64,...">）
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try (OutputStream os = baos) {
            ImageIO.write(image, "png", os);
        }
        String base64 = java.util.Base64.getEncoder().encodeToString(baos.toByteArray());

        Map<String, String> data = new HashMap<>(4);
        data.put("captchaKey", captchaKey);
        data.put("imageBase64", base64);
        data.put("expireSeconds", String.valueOf(captchaExpireSeconds));
        return R.ok(data);
    }

    /**
     * 用户自助注册（前端 /register 页调用）
     * <p>
     * 业务规则见 AuthService.register，这里做一层 JSR303 校验（@Valid RegisterRO）。
     * 注册成功返回 code=200，前端跳转至 /login。
     *
     * @param ro 注册请求（username + password）
     * @return 空 R（仅 code / msg 指示成功与否）
     */
    @Operation(summary = "用户自助注册", description = "创建新账号；用户名 3-32、密码 6-64；默认启用、默认租户、无角色（管理员后续分配）")
    @PostMapping("/register")
    public R<Void> register(@Parameter(description = "注册请求") @Valid @RequestBody RegisterRO ro) {
        authService.register(ro);
        return R.ok();
    }
}
