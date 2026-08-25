package com.workspace.fatjar.hrm.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工 VO（Controller 层返回前端）
 * <p>
 * 设计说明：
 *   1. 位于 Controller 层，由 HrmEmployeeController 通过 Converter 从 BO 转换，面向前端展示
 *   2. 公共字段（id/createTime/updateTime）继承自 {@link BaseVO}，不含敏感字段与审计人字段
 *   3. 与 BO 通过 {@link com.workspace.fatjar.hrm.convert.HrmEmployeeConverter}（MapStruct）单向转换（BO -> VO）
 * <p>
 * 字段含义：
 *   - status：员工状态（0=在职，1=离职）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrmEmployeeVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工号 */
    private String empNo;

    /** 姓名 */
    private String name;

    /** 部门 ID */
    private Long deptId;

    /** 职位 */
    private String position;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态：0=在职 1=离职 */
    private Integer status;
}
