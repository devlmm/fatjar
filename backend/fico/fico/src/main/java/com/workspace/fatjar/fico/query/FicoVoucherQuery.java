package com.workspace.fatjar.fico.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计凭证分页查询条件
 * <p>
 * 继承 {@link PageQuery}（current/size），附加凭证编号模糊查询与状态精确查询。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FicoVoucherQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 凭证编号（模糊查询） */
    private String voucherNo;

    /** 状态：0=草稿 1=已审核（精确查询，null 表示不限） */
    private Integer status;
}
