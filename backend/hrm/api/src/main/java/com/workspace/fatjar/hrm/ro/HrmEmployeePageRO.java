package com.workspace.fatjar.hrm.ro;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工分页查询请求对象 RO（Request Object）
 * <p>
 * 继承 PageQuery 获取分页参数（current/size），并扩展业务过滤字段：
 *   - name：员工姓名（模糊查询，可空）
 *   - status：员工状态（精确匹配，可空，0=在职 1=离职）
 * <p>
 * 由 starter-web 全局拦截 Hibernate-Validator 校验失败并转换为 R&lt;Void&gt; 错误响应。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "员工分页查询请求")
public class HrmEmployeePageRO extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 员工姓名（模糊查询） */
    @Schema(description = "员工姓名（模糊）", example = "张")
    private String name;

    /** 状态：0=在职 1=离职（精确查询） */
    @Schema(description = "状态：0=在职 1=离职", example = "0")
    private Integer status;
}
