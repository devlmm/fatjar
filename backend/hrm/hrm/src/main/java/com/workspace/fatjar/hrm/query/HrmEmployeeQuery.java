package com.workspace.fatjar.hrm.query;

import com.workspace.fatjar.common.result.PageQuery;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工分页查询入参
 * <p>
 * 设计说明：
 *   1. 继承 {@link PageQuery}，复用 current/size 分页参数（含 Hibernate-Validator 校验）
 *   2. 由 HrmEmployeeController.page 接收，透传至 HrmEmployeeService.pageBO
 *   3. name 支持模糊查询，status 支持精确查询，empNo 预留查询条件
 * <p>
 * 字段含义：
 *   - status：员工状态（0=在职，1=离职）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrmEmployeeQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 姓名（模糊查询） */
    private String name;

    /** 工号 */
    private String empNo;

    /** 状态：0=在职 1=离职 */
    private Integer status;
}
