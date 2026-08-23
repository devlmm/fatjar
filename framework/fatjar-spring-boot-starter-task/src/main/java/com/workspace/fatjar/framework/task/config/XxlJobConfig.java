package com.workspace.fatjar.framework.task.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * XXL-JOB 执行器自动装配
 * <p>
 * 职责：从 xxl.job.* 配置读取参数，注册 XxlJobSpringExecutor Bean。
 * <p>
 * 生命周期说明：
 *   - XxlJobSpringExecutor 实现 SmartInitializingSingleton（afterSingletonsInstantiated 调用 start）
 *     与 DisposableBean（destroy 调用 stop），Spring 容器自动管理，故 @Bean 不指定 initMethod/destroyMethod，
 *     否则会与 SmartInitializingSingleton 重复触发 start，导致端口二次绑定报错。
 * <p>
 * 装配开关：xxl.job.enabled（默认 matchIfMissing=true，即引入本 Starter 即默认开启）。
 *   不需要 XXL-JOB 的服务设置 xxl.job.enabled=false 即可关闭。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "xxl.job", name = "enabled", havingValue = "true", matchIfMissing = true)
public class XxlJobConfig {

    /* ============ 调度中心配置 ============ */
    /** 调度中心地址（多个逗号分隔） */
    @Value("${xxl.job.admin.addresses:}")
    private String adminAddresses;
    /** 调度中心通信 Token，需与调度中心配置一致 */
    @Value("${xxl.job.accessToken:}")
    private String accessToken;

    /* ============ 执行器配置 ============ */
    /** 执行器AppName，调度中心按此名称匹配执行器 */
    @Value("${xxl.job.executor.appname:fatjar-job-executor}")
    private String appname;
    /** 执行器地址（为空时由调度中心根据注册项拼接） */
    @Value("${xxl.job.executor.address:}")
    private String address;
    /** 执行器IP（为空时自动获取本机IP） */
    @Value("${xxl.job.executor.ip:}")
    private String ip;
    /** 执行器端口（接收调度中心触发任务请求） */
    @Value("${xxl.job.executor.port:9999}")
    private int port;
    /** 执行器日志路径 */
    @Value("${xxl.job.executor.logpath:./logs/xxl-job/jobhandler}")
    private String logPath;
    /** 执行器日志保留天数 */
    @Value("${xxl.job.executor.logretentiondays:30}")
    private int logRetentionDays;

    /**
     * 注册 XXL-JOB 执行器
     * <p>
     * Spring 在所有单例 Bean 初始化完成后回调 afterSingletonsInstantiated 触发 start()，
     * 应用关闭时调用 destroy() 触发 stop()，故此处无需 initMethod/destroyMethod。
     *
     * @return XxlJobSpringExecutor 实例
     */
    @Bean
    @ConditionalOnMissingBean(XxlJobSpringExecutor.class)
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor executor = new XxlJobSpringExecutor();
        executor.setAdminAddresses(adminAddresses);
        executor.setAppname(appname);
        executor.setAddress(address);
        executor.setIp(ip);
        executor.setPort(port);
        executor.setAccessToken(accessToken);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
        return executor;
    }
}
