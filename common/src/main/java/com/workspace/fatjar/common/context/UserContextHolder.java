package com.workspace.fatjar.common.context;

import java.util.Objects;

/**
 * 用户上下文持有者（基于 ThreadLocal）
 * <p>
 * 设计说明：
 *   1. 使用 InheritableThreadLocal，子线程可继承父线程上下文（如异步任务）
 *   2. 请求结束时必须 {@link #clear()}，避免内存泄漏（由 Filter 兜底清理）
 *   3. 业务层通过 {@link #get()} 获取当前登录用户，未登录返回 null
 * <p>
 * 配套：JwtAuthenticationFilter 在请求进入时 set，请求结束时 clear。
 *
 * @author fatjar
 * @since 1.0.0
 */
public final class UserContextHolder {

    /** ThreadLocal 实例（可继承到子线程） */
    private static final InheritableThreadLocal<UserContext> HOLDER = new InheritableThreadLocal<>();

    /** 私有构造 */
    private UserContextHolder() {
    }

    /**
     * 设置当前线程的用户上下文
     *
     * @param context 用户上下文
     */
    public static void set(UserContext context) {
        HOLDER.set(context);
    }

    /**
     * 获取当前线程的用户上下文
     *
     * @return 用户上下文，未登录返回 null
     */
    public static UserContext get() {
        return HOLDER.get();
    }

    /**
     * 获取当前登录用户 ID
     *
     * @return 用户 ID，未登录抛 NPE 防御
     */
    public static Long currentUserId() {
        UserContext ctx = HOLDER.get();
        return Objects.requireNonNull(ctx, "当前无登录用户上下文").getUserId();
    }

    /**
     * 获取当前登录用户名
     *
     * @return 用户名，未登录返回 null
     */
    public static String currentUsername() {
        UserContext ctx = HOLDER.get();
        return ctx == null ? null : ctx.getUsername();
    }

    /**
     * 清除当前线程的用户上下文（必须在请求结束时调用）
     */
    public static void clear() {
        HOLDER.remove();
    }
}
