package com.workspace.fatjar.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实体基类（所有数据库实体的公共字段）
 * <p>
 * 字段说明：
 *   - id：主键，雪花 ID（由 IdGeneratorHolder 生成，MetaObjectHandler 自动填充）
 *   - createTime：创建时间，insert 时填充
 *   - updateTime：更新时间，insert+update 时填充
 *   - createBy：创建人 ID，insert 时填充（来自 UserContextHolder）
 *   - updateBy：更新人 ID，insert+update 时填充
 *   - deleted：逻辑删除标识（0=未删除，1=已删除），MyBatis-Plus 自动过滤
 * <p>
 * 配置：id-type=INPUT（不在数据库自增，由应用层雪花算法填充），与 Nacos 配置一致。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID（雪花算法生成） */
    @TableId(type = IdType.INPUT)
    private Long id;

    /** 创建时间（insert 时由 MetaObjectHandler 填充） */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间（insert+update 时填充） */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人标识（insert 时填充）
     * <p>
     * 类型统一为 String：既支持登录态下的用户 ID 字符串化（如 "1"），
     * 也支持无登录态的审计标识（如 "system"、"register"、"job"），与 MySQL VARCHAR(64) 列对齐。
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人标识（insert+update 时填充）
     *
     * @see #createBy 类型说明
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 逻辑删除标识（0=正常，1=已删除） */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
