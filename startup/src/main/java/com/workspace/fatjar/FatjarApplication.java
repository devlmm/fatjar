package com.workspace.fatjar;

import com.workspace.fatjar.common.snowflake.IdGeneratorHolder;
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

    /**
     * 程序入口
     *
     * @param args 启动参数，支持 --spring.profiles.active=dev|sit|prd
     */
    public static void main(String[] args) {
        // 初始化雪花 ID 生成器（多 Pod 部署通过 FATJAR_WORKER_ID 环境变量区分）
        IdGeneratorHolder.autoInit();
        // 启动 Spring 容器
        SpringApplication.run(FatjarApplication.class, args);
    }
}
