package com.workspace.fatjar.crm.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户分页查询入参
 * <p>
 * 设计说明：
 *   1. 继承 {@link PageQuery}，复用 current/size 分页参数（含 Hibernate-Validator 校验）
 *   2. 由 CrmCustomerController.page 接收，透传至 CrmCustomerService.pageBO
 *   3. customerName 支持模糊查询，status 支持精确查询
 * <p>
 * 字段含义：
 *   - status：客户状态（0=潜在，1=正式，2=流失）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CrmCustomerQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 客户名称（模糊查询） */
    private String customerName;

    /** 客户状态：0=潜在 1=正式 2=流失 */
    private Integer status;
}
