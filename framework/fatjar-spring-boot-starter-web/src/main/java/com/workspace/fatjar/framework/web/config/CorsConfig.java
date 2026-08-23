package com.workspace.fatjar.framework.web.config;

import com.workspace.fatjar.common.constant.CommonConstants;
import com.workspace.fatjar.framework.web.properties.WebProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS 跨域配置
 * <p>
 * 职责：实现 WebMvcConfigurer.addCorsMappings，为所有接口统一开启跨域支持。
 * <p>
 * 策略：
 *   - 路径：所有路径（默认）
 *   - 来源：若配置 fatjar.web.cors-allowed-origins 则使用配置值，否则允许所有来源
 *   - 方法：所有 HTTP 方法
 *   - 头：允许所有请求头
 *   - 暴露响应头：X-Trace-Id（前端可读取链路追踪 ID）
 *   - 凭证：允许携带 Cookie
 *   - 预检缓存：1 小时
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
public class CorsConfig implements WebMvcConfigurer {

    private final WebProperties webProperties;

    /**
     * 构造注入 WebProperties
     *
     * @param webProperties Web 层配置属性
     */
    public CorsConfig(WebProperties webProperties) {
        this.webProperties = webProperties;
    }

    /**
     * 注册 CORS 映射规则
     *
     * @param registry CORS 注册器
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的来源：配置非空则用配置，否则允许所有
                // 注意：Spring 6 起 allowCredentials(true) 与 allowedOrigins("*") 不可共存，
                // 必须改用 allowedOriginPatterns（支持通配且允许携带凭证）
                .allowedOriginPatterns(resolveAllowedOrigins())
                .allowedMethods("*")
                .allowedHeaders("*")
                .exposedHeaders(CommonConstants.HEADER_TRACE_ID)
                .allowCredentials(true)
                .maxAge(3600L);
    }

    /**
     * 解析允许的来源列表
     *
     * @return 来源数组，未配置时返回通配 "*"
     */
    private String[] resolveAllowedOrigins() {
        if (webProperties.getCorsAllowedOrigins() == null
                || webProperties.getCorsAllowedOrigins().isEmpty()) {
            return new String[]{"*"};
        }
        return webProperties.getCorsAllowedOrigins().toArray(new String[0]);
    }
}
