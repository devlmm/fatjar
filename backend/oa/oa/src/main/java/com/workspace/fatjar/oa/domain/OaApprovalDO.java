package com.workspace.fatjar.oa.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.domain.BaseDO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批 DO（与数据库表 oa.approval 一一对应）
 * <p>
 * 设计说明：
 *   1. 继承 {@link BaseDO}，公共审计字段（id/createTime/updateTime/createBy/updateBy/deleted）由父类提供
 *   2. 本类仅声明审批业务字段，@TableName 指定库表名 oa.approval
 *   3. applicantId 跨库关联 auth.sys_user.id，content 为 TEXT 大字段，统一用 String 承载
 *   4. 仅在 Mapper/Service 内部使用，不跨模块传递
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("oa.approval")
public class OaApprovalDO extends BaseDO {

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
