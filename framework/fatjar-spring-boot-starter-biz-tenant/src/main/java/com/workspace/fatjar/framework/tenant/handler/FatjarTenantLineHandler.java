package com.workspace.fatjar.framework.tenant.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.workspace.fatjar.common.constant.CommonConstants;
import com.workspace.fatjar.common.context.UserContext;
import com.workspace.fatjar.common.context.UserContextHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;

/**
 * 多租户租户 ID 解析器
 * <p>
 * 职责：实现 TenantLineHandler，向 MyBatis-Plus 提供：
 *   1. getTenantId()：当前请求所属租户 ID（从 UserContextHolder 获取，无上下文兜底默认租户）
 *   2. ignoreTable()：是否忽略某表的租户过滤（这里返回 false，所有表都拼接 tenant_id）
 * <p>
 * 注意：
 *   - MyBatis-Plus 3.5.5 的 TenantLineHandler.getTenantId() 返回 net.sf.jsqlparser.expression.Expression，
 *     需用 StringValue 包装字符串形式的租户 ID。
 *   - 所有业务表必须含 tenant_id 列，否则 SQL 执行报错；故本 Starter 默认关闭。
 *
 * @author fatjar
 * @since 1.0.0
 */
public class FatjarTenantLineHandler implements TenantLineHandler {

    /**
     * 获取当前租户 ID
     * <p>
     * 优先从 UserContextHolder 取登录上下文中的 tenantId；
     * 无登录上下文（如系统初始化、内部定时任务）时兜底为默认租户 ID，避免 NPE。
     *
     * @return 包装为 StringValue 的租户 ID
     */
    @Override
    public Expression getTenantId() {
        UserContext ctx = UserContextHolder.get();
        Long tenantId = ctx == null ? null : ctx.getTenantId();
        if (tenantId == null) {
            tenantId = CommonConstants.DEFAULT_TENANT_ID;
        }
        return new StringValue(String.valueOf(tenantId));
    }

    /**
     * 是否忽略指定表的租户过滤
     * <p>
     * 返回 false 表示所有表都拼接 tenant_id 条件。
     * 如需对某些系统表（如字典表）放行，可在此处按表名判断返回 true。
     *
     * @param tableName 表名
     * @return false 表示不忽略（即参与租户过滤）
     */
    @Override
    public boolean ignoreTable(String tableName) {
        return false;
    }
}
