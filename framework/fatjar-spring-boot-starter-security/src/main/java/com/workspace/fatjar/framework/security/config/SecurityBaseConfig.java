package com.workspace.fatjar.framework.security.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 基础配置
 * <p>
 * 职责：仅注册 PasswordEncoder（BCrypt 算法），供 auth 模块与业务层加解密口令使用。
 * <p>
 * 设计说明：
 *   1. 本类不配置 SecurityFilterChain，由业务 auth 模块（fatjar-auth）按需自定义，
 *      避免多个 SecurityFilterChain Bean 冲突。
 *   2. 使用 @ConditionalOnMissingBean，业务方可自定义 PasswordEncoder 覆盖默认 BCrypt。
 *   3. BCrypt 自带盐值（每次加密结果不同），抗彩虹表，业界推荐。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
public class SecurityBaseConfig {

    /**
     * 注册密码编码器
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
