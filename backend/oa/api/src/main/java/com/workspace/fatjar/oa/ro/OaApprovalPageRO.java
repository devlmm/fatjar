package com.workspace.fatjar.oa.ro;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审批分页查询请求对象 RO（Request Object）
 * <p>
 * 继承通用 PageQuery（提供 current/size 分页参数），扩展审批专属查询条件。
 * 配合 Hibernate-Validator 校验分页范围，由 starter-web 全局拦截校验失败。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "审批分页查询请求")
public class OaApprovalPageRO extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 审批标题（模糊查询） */
    @Schema(description = "审批标题（模糊）", example = "报销")
    private String title;

    /** 状态：0=待审批 1=已通过 2=已驳回（精确匹配，可空） */
    @Schema(description = "状态：0=待审批 1=已通过 2=已驳回", example = "0")
    private Integer status;
}
