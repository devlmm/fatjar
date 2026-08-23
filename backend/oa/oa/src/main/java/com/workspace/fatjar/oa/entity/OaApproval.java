package com.workspace.fatjar.oa.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * OA 审批实体（对应 oa.approval 表，独立数据库 schema=oa）
 * <p>
 * 字段说明：
 *   - title：审批标题，前端列表展示用
 *   - applicantId：申请人 ID（关联 sys_user.id，跨模块查询用户信息走 AuthApi）
 *   - type：审批类型（leave/expense/purchase/seal 等）
 *   - status：状态（0=待审批，1=已通过，2=已驳回）
 *   - content：审批内容（JSON 或富文本，存储表单明细）
 *   - comment：审批意见（审批人填写，驳回时必填）
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa.approval")
public class OaApproval extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 审批标题 */
    private String title;

    /** 申请人 ID（关联 sys_user.id） */
    private Long applicantId;

    /** 审批类型（leave/expense/purchase 等） */
    private String type;

    /** 状态：0=待审批 1=已通过 2=已驳回 */
    private Integer status;

    /** 审批内容（表单明细 JSON / 富文本） */
    private String content;

    /** 审批意见（审批人填写） */
    private String comment;
}
