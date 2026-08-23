package com.workspace.fatjar.common.result;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页结果封装 PageResult
 * <p>
 * 与 MyBatis-Plus 的 IPage 解耦：Service 层返回 IPage，Controller 层转换为 PageResult，
 * 避免把 MyBatis-Plus 类型暴露给前端。
 * <p>
 * 使用示例：
 *   IPage&lt;ErpProduct&gt; page = productService.page(...);
 *   PageResult&lt;ProductVO&gt; result = PageResult.of(page, ProductVO::from);
 *
 * @param <T> 列表元素类型
 * @author fatjar
 * @since 1.0.0
 */
@Data
public class PageResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 当前页码（从 1 开始） */
    private long current;
    /** 每页大小 */
    private long size;
    /** 总记录数 */
    private long total;
    /** 总页数 */
    private long pages;
    /** 当前页数据列表 */
    private List<T> records;

    /** 默认构造 */
    public PageResult() {
        this.records = Collections.emptyList();
    }

    /**
     * 全参构造
     */
    public PageResult(long current, long size, long total, long pages, List<T> records) {
        this.current = current;
        this.size = size;
        this.total = total;
        this.pages = pages;
        this.records = records == null ? Collections.emptyList() : records;
    }

    /**
     * 空结果
     *
     * @param <T> 元素类型
     * @return 空分页
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(1L, 10L, 0L, 0L, Collections.emptyList());
    }

    /**
     * 从 MyBatis-Plus IPage 构造（不转换元素类型）
     *
     * @param page MyBatis-Plus 分页对象
     * @param <T>  元素类型
     * @return PageResult
     */
    public static <T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<T> page) {
        return new PageResult<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal(),
                page.getPages(),
                page.getRecords()
        );
    }

    /**
     * 从 MyBatis-Plus IPage 构造（带元素类型转换，常用于 Entity -> VO）
     *
     * @param page     MyBatis-Plus 分页对象
     * @param mapper   元素转换函数（Entity -> VO）
     * @param <S>      源类型
     * @param <T>      目标类型
     * @return PageResult
     */
    public static <S, T> PageResult<T> of(com.baomidou.mybatisplus.core.metadata.IPage<S> page, Function<S, T> mapper) {
        List<T> list = page.getRecords() == null
                ? Collections.emptyList()
                : page.getRecords().stream().map(mapper).collect(Collectors.toList());
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), list);
    }
}
