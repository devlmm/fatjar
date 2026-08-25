package com.workspace.fatjar.common.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 视图对象基类（VO，Controller 层返回前端）
 * <p>
 * 设计说明（分层领域模型 - 阿里黄山版）：
 *   1. VO（View Object）位于 Controller 层，面向前端展示，由 Controller 通过 Converter 从 BO 转换
 *   2. 各业务模块的 XxxVO 继承本类，仅声明展示字段，公共字段由本类统一提供
 *   3. VO 不含敏感字段（如密码、租户、审计人），从结构上避免响应泄露
 *   4. VO 与 BO 通过 MapStruct 转换（BO -> VO），单向不可逆
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
public abstract class BaseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long id;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
