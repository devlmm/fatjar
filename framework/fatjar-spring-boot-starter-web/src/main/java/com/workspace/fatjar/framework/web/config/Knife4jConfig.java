package com.workspace.fatjar.framework.web.config;

import com.workspace.fatjar.framework.web.properties.WebProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Knife4j + SpringDoc OpenAPI 文档配置
 * <p>
 * 职责：自动装配 OpenAPI 文档 Bean，并按业务域（auth/erp/oa/crm/ems）划分接口分组，
 *       便于在 Knife4j 文档页面按业务域筛选接口。
 * <p>
 * 开关：fatjar.web.enable-knife4j=false 可关闭（生产环境推荐关闭以减少信息泄露）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
@EnableConfigurationProperties(WebProperties.class)
@ConditionalOnProperty(prefix = "fatjar.web", name = "enable-knife4j", havingValue = "true", matchIfMissing = true)
public class Knife4jConfig {

    /**
     * 注册 OpenAPI 主文档信息
     *
     * @return OpenAPI 实例（含标题、版本、描述、联系人）
     */
    @Bean
    public OpenAPI fatjarOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("fatjar 大单体 API 文档")
                        .description("企业云原生大单体业务管理系统 - 统一接口文档")
                        .version("1.0.0")
                        .contact(new Contact().name("fatjar").email("dev@fatjar.com")));
    }

    /**
     * 认证授权分组（auth 业务域）
     *
     * @return GroupedOpenApi 实例
     */
    @Bean
    public GroupedOpenApi authGroup() {
        return GroupedOpenApi.builder()
                .group("auth")
                .pathsToMatch("/auth/**")
                .build();
    }

    /**
     * ERP 业务分组
     *
     * @return GroupedOpenApi 实例
     */
    @Bean
    public GroupedOpenApi erpGroup() {
        return GroupedOpenApi.builder()
                .group("erp")
                .pathsToMatch("/erp/**")
                .build();
    }

    /**
     * OA 业务分组
     *
     * @return GroupedOpenApi 实例
     */
    @Bean
    public GroupedOpenApi oaGroup() {
        return GroupedOpenApi.builder()
                .group("oa")
                .pathsToMatch("/oa/**")
                .build();
    }

    /**
     * CRM 业务分组
     *
     * @return GroupedOpenApi 实例
     */
    @Bean
    public GroupedOpenApi crmGroup() {
        return GroupedOpenApi.builder()
                .group("crm")
                .pathsToMatch("/crm/**")
                .build();
    }

    /**
     * EMS 资金业务分组
     *
     * @return GroupedOpenApi 实例
     */
    @Bean
    public GroupedOpenApi emsGroup() {
        return GroupedOpenApi.builder()
                .group("ems")
                .pathsToMatch("/ems/**")
                .build();
    }
}
