package com.workspace.fatjar.common.result;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页查询基类（所有分页查询入参继承）
 * <p>
 * 字段：current=页码（默认 1），size=每页大小（默认 10，最大 100）。
 * 使用 Hibernate-Validator 校验，由 starter-web 自动拦截校验。
 * <p>
 * 使用示例：
 *   public class ProductPageQuery extends PageQuery { private String name; ... }
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 当前页码（从 1 开始） */
    @Min(value = 1, message = "页码不能小于 1")
    private Long current = 1L;

    /** 每页大小（1~100） */
    @Min(value = 1, message = "每页大小不能小于 1")
    @Max(value = 100, message = "每页大小不能超过 100")
    private Long size = 10L;
}
