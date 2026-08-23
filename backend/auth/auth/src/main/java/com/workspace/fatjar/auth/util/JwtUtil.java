package com.workspace.fatjar.auth.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT 工具类（生成与解析 Token）
 * <p>
 * 设计说明：
 *   1. 使用 jjwt 0.12.5 实现，HMAC-SHA 算法签名（密钥从配置读取）
 *   2. Token 中携带 userId（subject）与 username（自定义 claim）
 *   3. 校验失败统一返回 null/false，由调用方处理异常分支
 *   4. 密钥长度需满足 32 字节（HMAC-SHA256 最低要求）
 * <p>
 * 配置项：
 *   - fatjar.jwt.secret  ：签名密钥（默认 fatjar-secret-key-2024-must-be-at-least-32-bytes-long）
 *   - fatjar.jwt.expire  ：过期时间（秒，默认 86400 = 1 天）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Component
public class JwtUtil {

    /** JWT 签名密钥（从配置读取，必须满足 32 字节长度） */
    @Value("${fatjar.jwt.secret:fatjar-secret-key-2024-must-be-at-least-32-bytes-long}")
    private String secret;

    /** JWT 过期时间（秒，默认 86400 = 1 天） */
    @Value("${fatjar.jwt.expire:86400}")
    private long expire;

    /** 签名密钥对象（@PostConstruct 初始化） */
    private SecretKey key;

    /**
     * 初始化签名密钥（Spring 容器启动后自动调用）
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        log.info("JwtUtil 初始化完成，过期时间={}秒", expire);
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户 ID（作为 subject）
     * @param username 用户名（作为自定义 claim）
     * @return JWS Token 字符串
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire * 1000L);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }

    /**
     * 从 Token 中解析用户 ID
     *
     * @param token JWT Token
     * @return 用户 ID，解析失败返回 null
     */
    public Long parseUserId(String token) {
        try {
            String subject = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return subject == null || subject.isEmpty() ? null : Long.valueOf(subject);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token 解析 userId 失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 Token 中解析用户名
     *
     * @param token JWT Token
     * @return 用户名，解析失败返回 null
     */
    public String parseUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .get("username", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token 解析 username 失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 校验 Token 是否有效（签名正确 + 未过期）
     *
     * @param token JWT Token
     * @return true 表示有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token 校验失败：{}", e.getMessage());
            return false;
        }
    }
}
