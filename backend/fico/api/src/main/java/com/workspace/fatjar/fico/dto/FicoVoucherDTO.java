package com.workspace.fatjar.fico.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 会计凭证 DTO（跨模块传递的凭证基础信息）
 * <p>
 * 跨模块调用 FicoVoucherApi.getVoucherById 时返回，仅包含对外必要字段
 * （不含审计字段 createTime/updateTime/createBy/updateBy/deleted）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "会计凭证信息")
public class FicoVoucherDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 凭证 ID */
    @Schema(description = "凭证 ID", example = "1234567890")
    private Long id;

    /** 凭证编号（业务唯一） */
    @Schema(description = "凭证编号", example = "FV-202608-0001")
    private String voucherNo;

    /** 凭证标题（摘要） */
    @Schema(description = "凭证标题", example = "8 月办公采购")
    private String title;

    /** 金额 */
    @Schema(description = "金额", example = "12800.00")
    private BigDecimal amount;

    /** 借贷方向：0=借方 1=贷方 */
    @Schema(description = "借贷方向：0=借方 1=贷方", example = "0")
    private Integer direction;

    /** 会计期间（如 202608） */
    @Schema(description = "会计期间", example = "202608")
    private String period;

    /** 状态：0=草稿 1=已审核 */
    @Schema(description = "状态：0=草稿 1=已审核", example = "0")
    private Integer status;
}
