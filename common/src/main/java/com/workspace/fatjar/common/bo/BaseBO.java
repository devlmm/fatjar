package com.workspace.fatjar.common.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 业务对象基类（BO，Service 层输入输出）
 * <p>
 * 设计说明（分层领域模型 - 阿里黄山版）：
 *   1. BO（Business Object）位于 Service 层，承载业务模型，由 Service 产出/接收
 *   2. 各业务模块的 XxxBO 继承本类，仅声明业务字段，公共字段由本类统一提供
 *   3. BO 与 DO 通过 MapStruct 双向转换；对外 DTO 时剔除 createTime/updateTime（见 Converter）
 *   4. BO 不含 deleted/createBy/updateBy 等审计字段（由 DO 承载，BO 层无需感知）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
public abstract class BaseBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 ID */
    private Long id;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
