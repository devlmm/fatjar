package com.workspace.fatjar.framework.mybatis.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MyBatis-Plus 拦截器配置
 * <p>
 * 职责：注册 MybatisPlusInterceptor，挂载分页与乐观锁两个内置拦截器。
 * <p>
 * 说明：
 *   - 多租户 TenantLineInnerInterceptor 在 fatjar-spring-boot-starter-biz-tenant 单独装配，
 *     默认关闭，避免业务表无 tenant_id 列时 SQL 报错。本类不重复处理多租户。
 *   - 分页拦截器锁定 MySQL 方言；如需多数据库可改为 DbType.OTHER。
 *   - 乐观锁拦截器依赖实体字段上 @Version 注解。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
public class MybatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 全局拦截器（分页 + 乐观锁）
     *
     * @return MybatisPlusInterceptor 实例
     */
    @Bean
    @ConditionalOnMissingBean(MybatisPlusInterceptor.class)
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件：指定 MySQL 方言，自动拼接 LIMIT
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件：更新时自动校验并自增 @Version 字段，CAS 语义防并发覆盖
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
