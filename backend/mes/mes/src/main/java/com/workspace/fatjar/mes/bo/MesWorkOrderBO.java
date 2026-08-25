package com.workspace.fatjar.mes.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单业务对象（BO，Service 层输入输出）
 * <p>
 * 位于 Service 层，承载业务模型。公共字段（id/createTime/updateTime）继承自 {@link BaseBO}，
 * 本类仅声明业务字段。BO 与 DO 通过 MapStruct 双向转换。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MesWorkOrderBO extends BaseBO {

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
