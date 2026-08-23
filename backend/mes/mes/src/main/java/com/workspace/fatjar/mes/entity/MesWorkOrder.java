package com.workspace.fatjar.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单实体（对应 mes.work_order 表）
 * <p>
 * 字段说明：
 *   - workOrderNo：工单编号，业务唯一
 *   - productName：产品名称
 *   - quantity：生产数量
 *   - status：状态（0=新建，1=生产中，2=已完成）
 *   - plannedStart：计划开始时间
 *   - plannedEnd：计划结束时间
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mes.work_order")
public class MesWorkOrder extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工单编号（业务唯一） */
    private String workOrderNo;

    /** 产品名称 */
    private String productName;

    /** 生产数量 */
    private Integer quantity;

    /** 状态：0=新建 1=生产中 2=已完成 */
    private Integer status;

    /** 计划开始时间 */
    private LocalDateTime plannedStart;

    /** 计划结束时间 */
    private LocalDateTime plannedEnd;
}
