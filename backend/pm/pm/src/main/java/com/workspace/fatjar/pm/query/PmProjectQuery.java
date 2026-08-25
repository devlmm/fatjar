package com.workspace.fatjar.pm.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 项目分页查询入参
 * <p>
 * 设计说明：
 *   1. 继承 {@link PageQuery}，复用 current/size 分页参数（含 Hibernate-Validator 校验）
 *   2. 由 PmProjectController.page 接收，透传至 PmProjectService.pageBO
 *   3. projectName 支持模糊查询，status 支持精确查询，projectNo 预留查询条件
 * <p>
 * 字段含义：
 *   - status：项目状态（0=规划中，1=进行中，2=已完成，3=已取消）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PmProjectQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 项目名称（模糊查询） */
    private String projectName;

    /** 项目编号 */
    private String projectNo;

    /** 项目状态：0=规划中 1=进行中 2=已完成 3=已取消 */
    private Integer status;
}
