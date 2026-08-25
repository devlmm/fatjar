package com.workspace.fatjar.mes.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单视图对象（VO，Controller 层返回前端）
 * <p>
 * 位于 Controller 层，面向前端展示。公共字段（id/createTime/updateTime）继承自 {@link BaseVO}，
 * 本类仅声明展示字段。VO 由 Converter 从 BO 单向转换。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "工单信息")
public class MesWorkOrderVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工单编号（业务唯一） */
    @Schema(description = "工单编号", example = "WO-202608-0001")
    private String workOrderNo;

    /** 产品名称 */
    @Schema(description = "产品名称", example = "智能传感器 X1")
    private String productName;

    /** 生产数量 */
    @Schema(description = "生产数量", example = "1000")
    private Integer quantity;

    /** 状态：0=新建 1=生产中 2=已完成 */
    @Schema(description = "状态：0=新建 1=生产中 2=已完成", example = "0")
    private Integer status;

    /** 计划开始时间 */
    @Schema(description = "计划开始时间", example = "2026-08-25T08:00:00")
    private LocalDateTime plannedStart;

    /** 计划结束时间 */
    @Schema(description = "计划结束时间", example = "2026-08-30T18:00:00")
    private LocalDateTime plannedEnd;
}
