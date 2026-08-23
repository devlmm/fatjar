package com.workspace.fatjar.common.constant;

/**
 * 通用常量定义
 * <p>
 * 集中管理 HTTP Header、TraceId、Redis Key 前缀、缓存过期时间等常量，
 * 避免魔法字符串散落各处。
 *
 * @author fatjar
 * @since 1.0.0
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /* ============ HTTP Header ============ */
    /** 鉴权 Token Header 名 */
    public static final String HEADER_AUTHORIZATION = "Authorization";
    /** Token 前缀（Bearer 模式） */
    public static final String TOKEN_PREFIX = "Bearer ";
    /** 链路追踪 ID Header（跨服务传播） */
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    /** 租户 ID Header */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    /* ============ MDC ============ */
    /** MDC 中 TraceId 的 key */
    public static final String MDC_TRACE_ID = "traceId";
    /** MDC 中用户 ID 的 key */
    public static final String MDC_USER_ID = "userId";

    /* ============ Redis Key 前缀 ============ */
    /** 登录 Token 缓存前缀 */
    public static final String REDIS_KEY_TOKEN = "fatjar:auth:token:";
    /** 用户权限缓存前缀 */
    public static final String REDIS_KEY_PERMISSIONS = "fatjar:auth:perms:";
    /** 用户角色缓存前缀 */
    public static final String REDIS_KEY_ROLES = "fatjar:auth:roles:";
    /** 验证码缓存前缀 */
    public static final String REDIS_KEY_CAPTCHA = "fatjar:auth:captcha:";
    /** 分布式锁前缀 */
    public static final String REDIS_KEY_LOCK = "fatjar:lock:";

    /* ============ 逻辑删除 ============ */
    /** 未删除 */
    public static final int NOT_DELETED = 0;
    /** 已删除 */
    public static final int DELETED = 1;

    /* ============ 通用状态 ============ */
    /** 启用 */
    public static final int STATUS_ENABLE = 0;
    /** 禁用 */
    public static final int STATUS_DISABLE = 1;

    /* ============ 默认值 ============ */
    /** 默认租户 ID（单租户场景） */
    public static final Long DEFAULT_TENANT_ID = 1L;
    /** 默认分页页码 */
    public static final long DEFAULT_PAGE_CURRENT = 1L;
    /** 默认分页大小 */
    public static final long DEFAULT_PAGE_SIZE = 10L;
}
