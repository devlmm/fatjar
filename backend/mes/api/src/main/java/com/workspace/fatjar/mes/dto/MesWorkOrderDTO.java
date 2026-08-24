package com.workspace.fatjar.mes.dto;

import com.workspace.fatjar.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工单 DTO（跨模块传递的工单基础信息）
 * <p>
 * 跨模块调用 MesWorkOrderApi.getWorkOrderById 时返回，仅包含对外必要字段
 * （不含计划时间 plannedStart/plannedEnd 与审计字段 createTime/updateTime/createBy/updateBy/deleted）。
 * 主键 ID 继承自 {@link BaseDTO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "工单信息")
public class MesWorkOrderDTO extends BaseDTO {

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
}
