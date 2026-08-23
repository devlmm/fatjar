package com.workspace.fatjar.common.snowflake;

/**
 * 雪花 ID 生成器（Twitter Snowflake 算法实现）
 * <p>
 * 位分配（共 64 位，Java long）：
 *   - 1 位符号位（恒 0）
 *   - 41 位时间戳（毫秒级，约 69 年）
 *   - 10 位机器 ID（可拆 5 位数据中心 + 5 位工作节点，本实现统一为 workerId）
 *   - 12 位序列号（同毫秒内自增，最多 4096 个/毫秒）
 * <p>
 * 多 Pod 部署时，通过环境变量 FATJAR_WORKER_ID 注入不同 workerId，避免冲突。
 * <p>
 * 线程安全：通过 synchronized 保证单机内并发唯一。
 *
 * @author fatjar
 * @since 1.0.0
 */
public class SnowflakeIdGenerator {

    /** 起始时间戳（2024-01-01 00:00:00 UTC），可按需调整 */
    private static final long TWEPOCH = 1704067200000L;

    /** 机器 ID 占用位数 */
    private static final long WORKER_ID_BITS = 10L;
    /** 序列号占用位数 */
    private static final long SEQUENCE_BITS = 12L;

    /** 机器 ID 最大值（1023） */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    /** 序列号掩码（4095） */
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /** 机器 ID 左移位数（12） */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    /** 时间戳左移位数（22） */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /** 工作节点 ID（0~1023） */
    private final long workerId;
    /** 当前序列号 */
    private long sequence = 0L;
    /** 上次生成时间戳 */
    private long lastTimestamp = -1L;

    /**
     * 构造雪花 ID 生成器
     *
     * @param workerId 工作节点 ID（0~1023），多 Pod 部署需各不相同
     * @throws IllegalArgumentException workerId 越界时抛出
     */
    public SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                    "workerId 越界，合法范围 [0," + MAX_WORKER_ID + "]，实际：" + workerId);
        }
        this.workerId = workerId;
    }

    /**
     * 生成下一个全局唯一 ID（线程安全）
     *
     * @return 64 位雪花 ID
     */
    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        // 时钟回拨检测：若系统时钟回退，拒绝生成，避免重复
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(
                    "时钟回拨 " + (lastTimestamp - timestamp) + "ms，拒绝生成 ID");
        }

        if (timestamp == lastTimestamp) {
            // 同毫秒内序列号自增
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0L) {
                // 序列号耗尽，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 新毫秒，序列号归零
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 位运算组装：时间戳 | 机器ID | 序列号
        return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 自旋等待到下一毫秒
     *
     * @param lastTimestamp 上次时间戳
     * @return 下一毫秒时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
