package com.workspace.fatjar.mes.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单数据对象（DO，对应 mes.work_order 表）
 * <p>
 * 位于 Mapper 层，与数据库表一一对应。公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）
 * 继承自 {@link BaseDO}，本类仅声明业务字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mes.work_order")
public class MesWorkOrderDO extends BaseDO {

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
