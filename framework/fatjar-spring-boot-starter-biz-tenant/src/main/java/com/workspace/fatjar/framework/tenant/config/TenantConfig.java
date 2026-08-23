package com.workspace.fatjar.framework.tenant.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.workspace.fatjar.framework.tenant.handler.FatjarTenantLineHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * 多租户自动装配配置
 * <p>
 * 职责：fatjar.tenant.enabled=true 时，注册带 TenantLineInnerInterceptor 的 MybatisPlusInterceptor，
 *       自动为所有业务 SQL 拼接 tenant_id 过滤条件，实现行级租户隔离。
 * <p>
 * 与 mybatis starter 的关系：
 *   - MybatisPlusInterceptor 全局只能有一个 Bean。本配置通过 @AutoConfigureBefore 指定在
 *     mybatis starter 的 MybatisPlusConfig 之前装配，并通过 @ConditionalOnMissingBean 让
 *     mybatis starter 检测到已存在拦截器后跳过自身注册，避免 Bean 冲突。
 *   - 启用多租户后，本拦截器替代 mybatis starter 的分页+乐观锁拦截器。
 *     如需在多租户模式下同时使用分页/乐观锁，请在此处 addInnerInterceptor 追加
 *     PaginationInnerInterceptor 与 OptimisticLockerInnerInterceptor（注意：租户拦截器须在分页之前）。
 *   - 默认关闭（fatjar.tenant.enabled 未配置或 false 时不装配），避免业务表无 tenant_id 列报错。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
@AutoConfigureBefore(name = "com.workspace.fatjar.framework.mybatis.config.MybatisPlusConfig")
@ConditionalOnProperty(prefix = "fatjar.tenant", name = "enabled", havingValue = "true")
public class TenantConfig {

    /**
     * 注册多租户拦截器
     *
     * @return MybatisPlusInterceptor 实例（含 TenantLineInnerInterceptor）
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor tenantMybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 多租户拦截器：解析 SQL 时自动拼接 tenant_id 条件
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new FatjarTenantLineHandler()));
        return interceptor;
    }
}
