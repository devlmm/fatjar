package com.workspace.fatjar.common.snowflake;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 雪花 ID 生成器并发唯一性测试
 * <p>
 * 场景：10 线程并发各生成 1 万个 ID，验证全局唯一性。
 *
 * @author fatjar
 * @since 1.0.0
 */
class SnowflakeIdGeneratorTest {

    /** 测试总 ID 数（10 万） */
    private static final int TOTAL_IDS = 100_000;
    /** 并发线程数 */
    private static final int THREAD_COUNT = 10;
    /** 每线程生成数 */
    private static final int PER_THREAD = TOTAL_IDS / THREAD_COUNT;

    @Test
    void testConcurrentUnique() throws InterruptedException {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1L);
        Set<Long> ids = Collections.synchronizedSet(new HashSet<>(TOTAL_IDS));
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            pool.submit(() -> {
                try {
                    for (int j = 0; j < PER_THREAD; j++) {
                        ids.add(generator.nextId());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        pool.shutdown();

        // 校验总数一致（无重复）
        assertEquals(TOTAL_IDS, ids.size(), "雪花 ID 存在重复");
        assertTrue(ids.stream().allMatch(id -> id > 0), "ID 应为正数");
    }
}
