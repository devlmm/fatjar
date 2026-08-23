package com.workspace.fatjar.oa.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 审批 DTO（跨模块传递的审批基础信息）
 * <p>
 * 跨模块调用门面方法时返回，仅包含对外必要字段（不含 content/comment 等大文本/内部字段），
 * 避免数据库结构变更影响调用方。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "审批信息")
public class OaApprovalDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 审批 ID */
    @Schema(description = "审批 ID", example = "1234567890")
    private Long id;

    /** 审批标题 */
    @Schema(description = "审批标题", example = "差旅报销申请")
    private String title;

    /** 申请人 ID（关联 sys_user.id） */
    @Schema(description = "申请人 ID", example = "1001")
    private Long applicantId;

    /** 审批类型（leave/expense/purchase 等） */
    @Schema(description = "审批类型", example = "expense")
    private String type;

    /** 状态：0=待审批 1=已通过 2=已驳回 */
    @Schema(description = "状态：0=待审批 1=已通过 2=已驳回", example = "0")
    private Integer status;
}
