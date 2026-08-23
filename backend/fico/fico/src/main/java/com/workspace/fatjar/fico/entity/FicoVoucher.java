package com.workspace.fatjar.fico.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.workspace.fatjar.common.entity.BaseEntity;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计凭证实体（对应 fico.voucher 表）
 * <p>
 * 字段说明：
 *   - voucherNo：凭证编号，业务唯一
 *   - title：凭证标题（摘要）
 *   - amount：金额
 *   - direction：借贷方向（0=借方，1=贷方）
 *   - period：会计期间（如 202608）
 *   - status：状态（0=草稿，1=已审核）
 * <p>
 * 公共字段（id/createTime/updateTime/createBy/updateBy/deleted）继承自 BaseEntity，
 * 故本类不重复声明这些字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("fico.voucher")
public class FicoVoucher extends BaseEntity implements Serializable {

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

    /** 会计期间（如 202608） */
    private String period;

    /** 状态：0=草稿 1=已审核 */
    private Integer status;
}
