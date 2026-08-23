package com.workspace.fatjar.pm.ro;

import com.workspace.fatjar.common.result.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目分页查询请求对象 RO（Request Object）
 * <p>
 * 继承 PageQuery 获取分页参数（current/size），并扩展业务过滤字段：
 *   - projectName：项目名称（模糊查询，可空）
 *   - status：项目状态（精确匹配，可空，0=规划中 1=进行中 2=已完成 3=已取消）
 * <p>
 * 由 starter-web 全局拦截 Hibernate-Validator 校验失败并转换为 R&lt;Void&gt; 错误响应。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "项目分页查询请求")
public class PmProjectPageRO extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 项目名称（模糊查询） */
    @Schema(description = "项目名称（模糊）", example = "fatjar")
    private String projectName;

    /** 项目状态：0=规划中 1=进行中 2=已完成 3=已取消（精确查询） */
    @Schema(description = "项目状态：0=规划中 1=进行中 2=已完成 3=已取消", example = "1")
    private Integer status;
}
