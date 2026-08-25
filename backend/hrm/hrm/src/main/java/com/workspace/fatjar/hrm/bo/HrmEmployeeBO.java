package com.workspace.fatjar.hrm.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 员工 BO（Service 层业务对象）
 * <p>
 * 设计说明：
 *   1. 位于 Service 层，由 HrmEmployeeService 产出/接收，Controller 经 @RequestBody 接收
 *   2. 公共字段（id/createTime/updateTime）继承自 {@link BaseBO}，不含 createBy/updateBy/deleted 等审计字段
 *   3. 与 DO 通过 {@link com.workspace.fatjar.hrm.convert.HrmEmployeeConverter}（MapStruct）双向转换
 * <p>
 * 字段含义：
 *   - status：员工状态（0=在职，1=离职）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class HrmEmployeeBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工号（唯一） */
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
