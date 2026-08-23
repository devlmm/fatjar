package com.workspace.fatjar.common.snowflake;

/**
 * 雪花 ID 生成器全局持有者（单例）
 * <p>
 * 设计说明：
 *   1. 单 JVM 内共享同一个 SnowflakeIdGenerator 实例
 *   2. workerId 优先从环境变量 FATJAR_WORKER_ID 读取，未配置默认 1
 *   3. 由 startup 模块在启动时调用 {@link #init(long)} 显式初始化（配置化）
 *   4. 业务层通过 {@link #nextId()} 获取全局唯一 ID
 * <p>
 * 与 MyBatis-Plus 集成：通过 MetaObjectHandler 在 insert 时自动填充主键字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
public final class IdGeneratorHolder {

    /** 环境变量名：FATJAR_WORKER_ID（多 Pod 部署时各 Pod 不同） */
    public static final String ENV_WORKER_ID = "FATJAR_WORKER_ID";

    /** 单例实例 */
    private static volatile SnowflakeIdGenerator instance;

    /** 私有构造，禁止实例化 */
    private IdGeneratorHolder() {
    }

    /**
     * 初始化雪花 ID 生成器（启动时调用一次）
     *
     * @param workerId 工作节点 ID（0~1023）
     */
    public static void init(long workerId) {
        if (instance == null) {
            synchronized (IdGeneratorHolder.class) {
                if (instance == null) {
                    instance = new SnowflakeIdGenerator(workerId);
                }
            }
        }
    }

    /**
     * 自动初始化：从环境变量读取 workerId，未配置默认 1
     */
    public static void autoInit() {
        long workerId = 1L;
        String env = System.getenv(ENV_WORKER_ID);
        if (env != null && !env.isEmpty()) {
            try {
                workerId = Long.parseLong(env);
            } catch (NumberFormatException ignored) {
                workerId = 1L;
            }
        }
        init(workerId);
    }

    /**
     * 生成下一个全局唯一 ID
     *
     * @return 雪花 ID
     */
    public static long nextId() {
        if (instance == null) {
            // 兜底：未显式初始化时自动初始化
            autoInit();
        }
        return instance.nextId();
    }

    /**
     * 生成下一个全局唯一 ID（字符串形式，便于前端处理大数精度丢失问题）
     *
     * @return 雪花 ID 字符串
     */
    public static String nextIdStr() {
        return String.valueOf(nextId());
    }
}
