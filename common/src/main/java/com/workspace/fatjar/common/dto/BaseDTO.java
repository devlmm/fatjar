package com.workspace.fatjar.common.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据传输对象基类（DTO，跨模块门面契约）
 * <p>
 * 设计说明（分层领域模型 - 阿里黄山版）：
 *   1. DTO（Data Transfer Object）位于各业务模块的 api 子模块 {@code dto/} 包，用于跨模块门面调用
 *   2. 各业务模块的 XxxDTO 继承本类，仅声明跨模块必要字段（剔除审计字段、敏感字段）
 *   3. DTO 由 Converter 从 BO 转换（BO -> DTO），剔除 createTime/updateTime（见 Converter）
 *   4. api 模块仅依赖 fatjar-common，不依赖任何业务 impl，从根源规避 Maven 循环依赖
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
public abstract class BaseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long id;
}
