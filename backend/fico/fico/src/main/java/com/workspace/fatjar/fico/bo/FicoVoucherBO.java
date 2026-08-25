package com.workspace.fatjar.fico.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计凭证业务对象（BO，Service 层输入输出）
 * <p>
 * 位于 Service 层，承载业务模型。公共字段（id/createTime/updateTime）继承自 {@link BaseBO}，
 * 本类仅声明业务字段。BO 与 DO 通过 MapStruct 双向转换。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FicoVoucherBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 凭证编号（业务唯一） */
    private String voucherNo;

    /** 凭证标题（摘要） */
    private String title;

    /** 金额 */
    private BigDecimal amount;

    /** 借贷方向：0=借方 1=贷方 */
    private Integer direction;

    /** 会计期间（如 2026-08） */
    private String period;

    /** 状态：0=草稿 1=已审核 */
    private Integer status;
}
