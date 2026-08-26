package com.workspace.fatjar;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * DEV 环境启动成功后打印访问地址与关键配置信息
 * <p>
 * 职责：
 *   1. 仅在 dev profile 激活时装配（SIT/PRD 不装配、不打印，避免生产环境泄漏地址/中间件信息）
 *   2. 实现 {@link ApplicationRunner}，在 Spring 容器启动成功后执行（启动失败不会触发本类）
 *   3. 汇总输出：运行环境、HTTP 访问地址、接口文档地址、健康检查端口、
 *      MySQL/Redis/Nacos/RocketMQ/XXL-JOB 等中间件连接信息、雪花 workerId
 * <p>
 * 设计说明：
 *   - 用 @Profile("dev") 而非配置开关，是因为需求明确为「DEV 打印、SIT/PRD 不打印」，
 *     @Profile 在 Bean 级别直接阻断装配，比运行期判断更彻底（连 Bean 都不创建）。
 *   - 走 SLF4J 日志而非 System.out，可复用 logback-spring.xml 中 dev profile 的 CONSOLE+FILE 输出。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Component
@Profile("dev")
public class StartupInfoPrinter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupInfoPrinter.class);

    /** 控制台边框，与 FatjarApplication 启动失败提示保持一致风格 */
    private static final String HLINE =
            "═══════════════════════════════════════════════════════════════════";

    private final Environment env;

    /**
     * 构造注入 Spring Environment，读取各配置项
     *
     * @param env Spring 环境抽象
     */
    public StartupInfoPrinter(Environment env) {
        this.env = env;
    }

    /**
     * 容器启动成功后执行，打印访问地址与配置信息
     *
     * @param args 启动参数
     */
    @Override
    public void run(ApplicationArguments args) {
        String port = orDefault(env.getProperty("server.port"), "8080");
        String contextPath = orDefault(env.getProperty("server.servlet.context-path"), "");
        String host = orDefault(env.getProperty("server.address"), "localhost");
        String accessUrl = "http://" + host + ":" + port + contextPath;

        boolean knife4j = Boolean.parseBoolean(orDefault(env.getProperty("fatjar.web.enable-knife4j"), "false"));
        String actuatorPort = env.getProperty("management.server.port");

        StringBuilder sb = new StringBuilder();
        sb.append('\n').append(HLINE);
        sb.append("\n  ✅ fatjar 启动成功（DEV 环境）");
        sb.append('\n').append(HLINE);
        sb.append("\n  运行环境    : ").append(profiles());
        sb.append("\n  访问地址    : ").append(accessUrl);
        if (knife4j) {
            sb.append("\n  接口文档    : ").append(accessUrl).append("/doc.html");
        }
        if (actuatorPort != null && !actuatorPort.isBlank()) {
            sb.append("\n  健康检查    : http://").append(host).append(":").append(actuatorPort)
                    .append("/actuator/health");
        }
        sb.append('\n').append(HLINE);
        sb.append("\n  MySQL       : ").append(jdbcShort(env.getProperty("spring.datasource.url")));
        sb.append("\n  Redis       : ").append(hostPort("spring.data.redis.host", "spring.data.redis.port", "6379"));
        sb.append("\n  Nacos       : ").append(nacosInfo());
        sb.append("\n  RocketMQ    : ").append(rocketmqInfo());
        sb.append("\n  XXL-JOB     : ").append(xxlInfo());
        sb.append("\n  雪花workerId: ").append(orDefault(env.getProperty("FATJAR_WORKER_ID"), "(未设置，默认 1)"));
        sb.append('\n').append(HLINE);

        log.info(sb.toString());
    }

    /**
     * 读取当前激活的 profile 列表，空则返回 default
     *
     * @return profile 拼接串
     */
    private String profiles() {
        String[] active = env.getActiveProfiles();
        return active.length == 0 ? "default" : Arrays.toString(active);
    }

    /**
     * 从 JDBC URL 截取 host:port/db 部分，去掉查询参数
     *
     * @param url JDBC 连接串
     * @return 简化的 host:port/db，为空时返回 未配置
     */
    private String jdbcShort(String url) {
        if (url == null || url.isBlank()) {
            return "未配置";
        }
        String s = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
        int idx = s.indexOf("//");
        if (idx >= 0) {
            s = s.substring(idx + 2);
        }
        return s;
    }

    /**
     * 拼接 host:port 形式的中间件地址
     *
     * @param hostKey    host 配置键
     * @param portKey    port 配置键
     * @param defaultPort 默认端口（port 未配置时使用）
     * @return host:port 或 未配置
     */
    private String hostPort(String hostKey, String portKey, String defaultPort) {
        String h = env.getProperty(hostKey);
        if (h == null || h.isBlank()) {
            return "未配置";
        }
        return h + ":" + orDefault(env.getProperty(portKey), defaultPort);
    }

    /**
     * 汇总 Nacos 地址与命名空间
     *
     * @return server-addr (namespace: xxx) 形式串
     */
    private String nacosInfo() {
        String addr = env.getProperty("spring.cloud.nacos.server-addr");
        if (addr == null || addr.isBlank()) {
            return "未配置";
        }
        String ns = env.getProperty("spring.cloud.nacos.config.namespace");
        return addr + (ns == null || ns.isBlank() ? "" : " (namespace: " + ns + ")");
    }

    /**
     * 汇总 RocketMQ name-server 与 producer.group 配置状态
     *
     * @return name-server + producer 状态
     */
    private String rocketmqInfo() {
        String ns = env.getProperty("rocketmq.name-server");
        if (ns == null || ns.isBlank()) {
            return "未配置（MQ 生产者/模板不装配）";
        }
        String group = env.getProperty("rocketmq.producer.group");
        return ns + (group == null || group.isBlank() ? " (producer.group 未配置，DemoMqProducer 不装配)"
                : " (producer.group: " + group + ")");
    }

    /**
     * 汇总 XXL-JOB 启用状态与调度中心地址
     *
     * @return 启用状态 + admin 地址
     */
    private String xxlInfo() {
        String enabled = orDefault(env.getProperty("xxl.job.enabled"), "false");
        if (!"true".equalsIgnoreCase(enabled)) {
            return "未启用";
        }
        String admin = env.getProperty("xxl.job.admin.addresses");
        return "enabled" + (admin == null || admin.isBlank() ? "" : ", admin: " + admin);
    }

    /**
     * 空值兜底：value 为空则返回 def
     *
     * @param value 原值
     * @param def   兜底值
     * @return 非空结果
     */
    private String orDefault(String value, String def) {
        return (value == null || value.isBlank()) ? def : value;
    }
}
