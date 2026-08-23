package com.workspace.fatjar.crm.ro;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户分页查询请求对象 RO（Request Object）
 * <p>
 * 继承 PageQuery 获取分页参数（current/size），并扩展业务过滤字段：
 *   - customerName：客户名称（模糊查询，可空）
 *   - status：客户状态（精确匹配，可空，0=潜在 1=正式 2=流失）
 * <p>
 * 由 starter-web 全局拦截 Hibernate-Validator 校验失败并转换为 R&lt;Void&gt; 错误响应。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "客户分页查询请求")
public class CrmCustomerPageRO extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 客户名称（模糊查询） */
    @Schema(description = "客户名称（模糊）", example = "科技")
    private String customerName;

    /** 客户状态：0=潜在 1=正式 2=流失（精确查询） */
    @Schema(description = "客户状态：0=潜在 1=正式 2=流失", example = "1")
    private Integer status;
}
