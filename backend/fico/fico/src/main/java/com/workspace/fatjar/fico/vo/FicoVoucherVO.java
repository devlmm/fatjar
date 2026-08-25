package com.workspace.fatjar.fico.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 会计凭证视图对象（VO，Controller 层返回前端）
 * <p>
 * 位于 Controller 层，面向前端展示。公共字段（id/createTime/updateTime）继承自 {@link BaseVO}，
 * 本类仅声明展示字段。VO 由 Converter 从 BO 单向转换。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "会计凭证信息")
public class FicoVoucherVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

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

    /** 会计期间（如 2026-08） */
    @Schema(description = "会计期间", example = "2026-08")
    private String period;

    /** 状态：0=草稿 1=已审核 */
    @Schema(description = "状态：0=草稿 1=已审核", example = "0")
    private Integer status;
}
