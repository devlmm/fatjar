package com.workspace.fatjar.oa.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批 VO（Controller 层返回前端）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BaseVO}，公共字段（id/createTime/updateTime）由父类提供
 *   2. 由 Controller 通过 {@link com.workspace.fatjar.oa.convert.OaApprovalConverter} 从 BO 转换
 *   3. 不含敏感字段与审计人字段
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OaApprovalVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 审批标题 */
    private String title;

    /** 申请人 ID */
    private Long applicantId;

    /** 审批类型 */
    private String type;

    /** 状态：0=待审批 1=已通过 2=已驳回 */
    private Integer status;

    /** 审批内容 */
    private String content;

    /** 审批意见 */
    private String comment;
}
