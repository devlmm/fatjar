package com.workspace.fatjar.fico.ro;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计凭证分页查询请求对象 RO（Request Object）
 * <p>
 * 继承 PageQuery 获取分页参数（current/size），并附加凭证专有查询条件。
 * 由 Controller 绑定查询参数，配合 Hibernate-Validator 校验。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会计凭证分页查询")
public class FicoVoucherPageRO extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 凭证编号（模糊查询，可空） */
    @Schema(description = "凭证编号（模糊）", example = "FV-202608")
    private String voucherNo;

    /** 状态（精确匹配，可空：0=草稿 1=已审核） */
    @Schema(description = "状态：0=草稿 1=已审核", example = "0")
    private Integer status;
}
