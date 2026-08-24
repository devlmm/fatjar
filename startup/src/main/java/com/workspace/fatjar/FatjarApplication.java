package com.workspace.fatjar;

import com.workspace.fatjar.common.snowflake.IdGeneratorHolder;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * fatjar 大单体业务管理系统 - 启动入口
 * <p>
 * 职责：
 *   1. Spring Boot 启动主类，扫描所有业务模块的 Bean
 *   2. @MapperScan 统一扫描所有业务实现模块的 Mapper（位于 com.workspace.fatjar.**.mapper 包）
 *   3. 启动时初始化雪花 ID 生成器（workerId 从环境变量 FATJAR_WORKER_ID 读取）
 *   4. 启动失败时，根据异常 cause 链输出中文友好提示（Redis/MySQL/Nacos/RocketMQ 等中间件连接不上）
 * <p>
 * 启动方式：
 *   java -jar fatjar-startup-1.0.0.jar --spring.profiles.active=dev
 * <p>
 * 多 Pod 部署：每个 Pod 设置不同的 FATJAR_WORKER_ID 环境变量，避免雪花 ID 冲突。
 *
 * @author fatjar
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.workspace.fatjar.**.mapper")
public class FatjarApplication {

    /** 边框分隔线，用于在控制台突出中文提示 */
    private static final String HLINE = "═══════════════════════════════════════════════════════════════════";

    /**
     * 程序入口
     *
     * @param args 启动参数，支持 --spring.profiles.active=dev|sit|prd
     */
    public static void main(String[] args) {
        // 初始化雪花 ID 生成器（多 Pod 部署通过 FATJAR_WORKER_ID 环境变量区分）
        IdGeneratorHolder.autoInit();
        try {
            // 启动 Spring 容器
            SpringApplication.run(FatjarApplication.class, args);
        } catch (Throwable t) {
            // 任何启动失败都先打印原始堆栈（便于深入排查）
            t.printStackTrace();
            // 然后输出中文友好定位提示
            printStartupFailureHint(t);
            // 以非零状态退出，保证 CI/CD 能识别启动失败
            System.exit(1);
        }
    }

    /**
     * 遍历异常 cause 链，识别常见的中间件连接失败，并输出中文提示 + 处理建议。
     * <p>
     * 识别策略（按 cause 链从上到下逐层检查异常类名、消息、嵌套包装）：
     *   1. Redis（Redisson / Lettuce）：{@code RedisConnectionException}、{@code Unable to connect to Redis}
     *   2. MySQL：{@code CommunicationsException}、{@code Communications link failure}、{@code Connection refused} + port 3306
     *   3. Nacos：{@code NacosConnectionFailureException}、{@code failed to connect to server} + port 8848
     *   4. RocketMQ：{@code MQClientException}、{@code No route info}、{@code NameServer is null}
     * <p>
     * 如果匹配不到已知模式，输出通用兜底提示，引导用户查看堆栈详情。
     *
     * @param rootCause SpringApplication.run 抛出的原始异常
     */
    private static void printStartupFailureHint(Throwable rootCause) {
        Throwable deepest = unwrap(rootCause);
        String className = deepest.getClass().getName();
        String message = deepest.getMessage() == null ? "" : deepest.getMessage();
        String combined = (className + " " + message).toLowerCase();

        String title;
        String body;

        if (combined.contains("redisconnectionexception")
                || combined.contains("unable to connect to redis")
                || combined.contains("rediscommandtimeoutexception")
                || (combined.contains("connection refused") && combined.contains(":6379"))) {
            title = "启动失败：Redis 连接不上";
            body  = String.join("\n",
                    "  1) 确认 Redis 是否启动：端口 6379（默认地址 localhost/127.0.0.1:6379）",
                    "  2) 如果使用 Docker，请执行：",
                    "       docker run -d --name fatjar-redis -p 6379:6379 redis:7-alpine",
                    "  3) 如果 Redis 地址不是本机，请修改 application-dev.yml 中 spring.data.redis.host/port",
                    "     或在 Nacos 配置中心的 application-common.yml 中覆盖该配置");
        } else if (combined.contains("communicationsexception")
                || combined.contains("communications link failure")
                || combined.contains("publickeyretrieval is not allowed")
                || (combined.contains("connection refused") && combined.contains(":3306"))
                || combined.contains("unknown database")
                || combined.contains("access denied for user")) {
            title = "启动失败：MySQL 连接不上或账号/库异常";
            body  = String.join("\n",
                    "  1) 确认 MySQL 是否启动：端口 3306（默认地址 localhost:3306）",
                    "  2) 如果使用 Docker，请执行：",
                    "       docker run -d --name fatjar-mysql -p 3306:3306 \\",
                    "         -e MYSQL_ROOT_PASSWORD=root \\",
                    "         -e TZ=Asia/Shanghai mysql:8.0 \\",
                    "         --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci",
                    "  3) 确认 10 个数据库是否已创建并导入 schema.sql / data.sql：",
                    "       fatjar, auth, fico, scm, mes, hrm, crm, pm, bi, oa",
                    "  4) 确认账号密码：默认 root/root，可在 application-dev.yml 修改 spring.datasource.*",
                    "  5) 提示 \"Public Key Retrieval is not allowed\"：在 JDBC URL 末尾追加 &allowPublicKeyRetrieval=true");
        } else if (combined.contains("nacosconnectionfailureexception")
                || combined.contains("failed to connect to server")
                || (combined.contains("connection refused") && (combined.contains("8848") || combined.contains("nacos")))) {
            title = "启动失败：Nacos 配置中心 / 注册中心连接不上";
            body  = String.join("\n",
                    "  1) 确认 Nacos Server 是否启动：端口 8848（默认地址 127.0.0.1:8848）",
                    "  2) 如果使用 Docker，请执行（standalone 单机模式）：",
                    "       docker run -d --name fatjar-nacos -p 8848:8848 -p 9848:9848 \\",
                    "         -e MODE=standalone -e TZ=Asia/Shanghai nacos/nacos-server:v2.3.2",
                    "  3) 浏览器打开 http://localhost:8848/nacos（nacos/nacos）检查 Namespace 和配置是否存在",
                    "  4) 如果完全不想用 Nacos，从 startup/pom.xml 移除 nacos-config-spring-boot-starter，",
                    "     并把所有配置写到 application-dev.yml / application.yml 中即可（不推荐生产环境这样做）");
        } else if (combined.contains("mqclientexception")
                || combined.contains("no route info of this topic")
                || combined.contains("nameserver is null")
                || combined.contains("namesrvaddr is null")
                || (combined.contains("connection refused") && (combined.contains("9876") || combined.contains("rocketmq")))) {
            title = "启动失败：RocketMQ 消息队列连接不上";
            body  = String.join("\n",
                    "  1) 确认 RocketMQ NameServer 是否启动：端口 9876（默认 127.0.0.1:9876）",
                    "  2) 本地 Docker 启动 RocketMQ 相对复杂，建议按 README 第 6 节步骤：",
                    "     先启动 NameServer → 再启动 Broker → 创建 Topic 并设置 autoCreateTopicEnable=true",
                    "  3) 如果暂时不使用消息队列功能，可从 startup/pom.xml 临时移除 fatjar-spring-boot-starter-mq 依赖，",
                    "     加快本地启动速度（SCM/MES 发消息相关接口会降级报错，但其他业务功能不受影响）");
        } else if (combined.contains("port 8080 was already in use")
                || combined.contains("address already in use")
                || combined.contains("bindexception")) {
            title = "启动失败：端口被占用";
            body  = String.join("\n",
                    "  1) 端口 8080 已被其他进程占用，请先关闭占用进程：",
                    "       Windows: netstat -ano | findstr :8080   然后 taskkill /F /PID <PID>",
                    "       Linux:   lsof -i :8080                   然后 kill -9 <PID>",
                    "  2) 或者修改 application.yml 中 server.port 为其他端口（如 18080）");
        } else if (combined.contains("factorybeanobjecttype")) {
            title = "启动失败：mybatis-spring 版本不兼容（Spring 6.x 需要 mybatis-spring >= 3.0.3）";
            body  = String.join("\n",
                    "  这个异常通常意味着 classpath 中同时存在 mybatis-spring 2.x（Spring 5 时代）和 Spring 6.x，",
                    "  请在 IDEA 中执行 Maven → Reload All Maven Projects → Build → Rebuild Project，",
                    "  然后重新启动。如果仍然失败，执行 mvn clean install -DskipTests 并重启 IDEA。");
        } else {
            title = "启动失败：未匹配到已知中间件/环境异常";
            body  = String.join("\n",
                    "  请查看上方打印的完整异常堆栈（Throwable.printStackTrace）定位根因。",
                    "  如果是中间件连接问题，常见错误如下：",
                    "    · localhost:3306 → MySQL；localhost:6379 → Redis；",
                    "      127.0.0.1:8848 → Nacos；127.0.0.1:9876 → RocketMQ。",
                    "  排查顺序建议：MySQL → Redis → Nacos → RocketMQ。");
        }

        // 把 title+body 用大边框打印，保证在大量堆栈日志中一眼可见
        System.err.println();
        System.err.println(HLINE);
        System.err.println("  ❌ " + title);
        System.err.println(HLINE);
        System.err.println("  【错误根因摘要】");
        System.err.println("    最深层异常：" + className);
        if (!message.isEmpty()) {
            // 只截取第一行，避免超长堆栈重复打印
            String firstLine = message.lines().findFirst().orElse(message);
            if (firstLine.length() > 160) firstLine = firstLine.substring(0, 160) + "…";
            System.err.println("    异常消息：    " + firstLine);
        }
        System.err.println();
        System.err.println("  【处理建议】");
        System.err.println(body);
        System.err.println(HLINE);
        System.err.println();
    }

    /**
     * 解包异常嵌套，返回最深层的真实 cause。
     * <p>
     * Spring / 反射 / 并发工具常见的包装异常（如 {@link UndeclaredThrowableException}、
     * {@link InvocationTargetException}、{@link ExecutionException}）都会被层层剥开，
     * 返回最底层抛出的业务异常或连接异常，便于上面的识别逻辑精确匹配。
     *
     * @param t 起始异常
     * @return 最深层 cause（若没有 cause，则返回 t 本身）
     */
    private static Throwable unwrap(Throwable t) {
        Throwable current = t;
        int guard = 0;
        while (guard++ < 64) {
            Throwable next = null;
            if (current instanceof UndeclaredThrowableException) {
                next = ((UndeclaredThrowableException) current).getUndeclaredThrowable();
            } else if (current instanceof InvocationTargetException) {
                next = ((InvocationTargetException) current).getTargetException();
            } else if (current instanceof ExecutionException) {
                next = ((ExecutionException) current).getCause();
            }
            if (next == null) {
                next = current.getCause();
            }
            if (next == null || next == current) {
                return current;
            }
            current = next;
        }
        return current;
    }
}
