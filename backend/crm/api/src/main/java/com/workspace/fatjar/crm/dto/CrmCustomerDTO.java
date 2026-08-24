package com.workspace.fatjar.crm.dto;

import com.workspace.fatjar.common.dto.BaseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户 DTO（跨模块传递的客户基础信息）
 * <p>
 * 仅包含对外必要字段（不含 createTime/updateTime/createBy/updateBy/deleted 等审计字段，
 * 亦不含 email 等非跨模块必需字段）。主键 ID 继承自 {@link BaseDTO}。
 * <p>
 * 字段含义：
 *   - level：客户等级（0=普通，1=VIP，2=战略）
 *   - status：客户状态（0=潜在，1=正式，2=流失）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户信息")
public class CrmCustomerDTO extends BaseDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 客户名称 */
    @Schema(description = "客户名称", example = "Workspace 科技")
    private String customerName;

    /** 联系人 */
    @Schema(description = "联系人", example = "李四")
    private String contact;

    /** 联系电话 */
    @Schema(description = "联系电话", example = "13900139000")
    private String phone;

    /** 客户等级：0=普通 1=VIP 2=战略 */
    @Schema(description = "客户等级：0=普通 1=VIP 2=战略", example = "0")
    private Integer level;

    /** 客户状态：0=潜在 1=正式 2=流失 */
    @Schema(description = "客户状态：0=潜在 1=正式 2=流失", example = "1")
    private Integer status;
}
