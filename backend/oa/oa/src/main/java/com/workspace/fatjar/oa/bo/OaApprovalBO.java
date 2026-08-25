package com.workspace.fatjar.oa.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批 BO（Service 层业务对象）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BaseBO}，公共字段（id/createTime/updateTime）由父类提供
 *   2. 与 {@link com.workspace.fatjar.oa.domain.OaApprovalDO} 通过 MapStruct 双向转换
 *   3. 不含 createBy/updateBy/deleted 等审计字段（由 DO 承载）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OaApprovalBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 审批标题 */
    private String title;

    /** 申请人 ID（跨库关联 auth.sys_user.id） */
    private Long applicantId;

    /** 审批类型（leave/expense/purchase 等） */
    private String type;

    /** 状态：0=待审批 1=已通过 2=已驳回 */
    private Integer status;

    /** 审批内容（TEXT 大字段） */
    private String content;

    /** 审批意见 */
    private String comment;
}
