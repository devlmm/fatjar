package com.workspace.fatjar.framework.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.workspace.fatjar.common.constant.CommonConstants;
import com.workspace.fatjar.common.context.UserContextHolder;
import com.workspace.fatjar.common.snowflake.IdGeneratorHolder;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充处理器
 * <p>
 * 职责：在 insert/update 时自动填充 BaseEntity 的公共字段，避免业务层重复手写。
 * <p>
 * 填充规则：
 *   - insert：id（雪花算法，仅当为 null）/ createTime / updateTime / createBy / updateBy / deleted=0
 *   - update：updateTime / updateBy
 * <p>
 * 注意：
 *   1. createBy / updateBy 取自 UserContextHolder.currentUserId()，定时任务 / MQ 消费等无登录场景
 *      会抛 NPE，故用 try-catch 兜底为 null（不填充），不影响数据落库。
 *   2. id 字段 @TableId(type=INPUT) 无 fill 注解，不能用 strictInsertFill，需手动 setValue。
 *   3. strictInsertFill / strictUpdateFill 仅在字段当前值为 null 时填充，不会覆盖业务层显式赋值。
 *
 * @author fatjar
 * @since 1.0.0
 */
@AutoConfiguration
public class MetaObjectHandlerImpl implements MetaObjectHandler {

    /**
     * 新增时填充
     *
     * @param metaObject MyBatis 元对象（包装当前实体）
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 1. 主键 id：雪花算法生成全局唯一 ID（仅当业务层未显式赋值时）
        if (metaObject.hasGetter("id") && metaObject.getValue("id") == null) {
            metaObject.setValue("id", IdGeneratorHolder.nextId());
        }
        // 2. 时间戳
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        // 3. 操作人（优先取登录上下文 userId→String；无登录态兜底 "system"，与 data.sql 初始化审计一致）
        Long userId = currentUserIdSafely();
        String operator = userId != null ? String.valueOf(userId) : "system";
        this.strictInsertFill(metaObject, "createBy", String.class, operator);
        this.strictUpdateFill(metaObject, "updateBy", String.class, operator);
        // 4. 逻辑删除标识：默认未删除
        this.strictInsertFill(metaObject, "deleted", Integer.class, CommonConstants.NOT_DELETED);
    }

    /**
     * 更新时填充
     *
     * @param metaObject MyBatis 元对象（包装当前实体）
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long userId = currentUserIdSafely();
        String operator = userId != null ? String.valueOf(userId) : "system";
        this.strictUpdateFill(metaObject, "updateBy", String.class, operator);
    }

    /**
     * 安全获取当前登录用户 ID
     * <p>
     * UserContextHolder.currentUserId() 在无上下文时抛 NPE，
     * 此处兜底返回 null（适用于定时任务、MQ 消费等无登录场景）。
     *
     * @return 用户 ID，无上下文时返回 null
     */
    private Long currentUserIdSafely() {
        try {
            return UserContextHolder.currentUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
