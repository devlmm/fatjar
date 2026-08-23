package com.workspace.fatjar.framework.web.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Web 层配置属性
 * <p>
 * 对应 application.yml 中的 fatjar.web 前缀配置项，控制 CORS 允许来源与 Knife4j 开关。
 * <p>
 * 配置示例：
 *   fatjar:
 *     web:
 *       cors-allowed-origins:
 *         - https://admin.fatjar.com
 *         - https://www.fatjar.com
 *       enable-knife4j: true
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "fatjar.web")
public class WebProperties {

    /** CORS 允许的来源列表，为空则允许所有来源（通配 *） */
    private List<String> corsAllowedOrigins;

    /** 是否启用 Knife4j 接口文档（生产环境可关闭），默认 true */
    private boolean enableKnife4j = true;
}
