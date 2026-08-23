package com.workspace.fatjar.hrm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 员工 DTO（跨模块传递的员工基础信息）
 * <p>
 * 跨模块调用 HrmEmployeeApi.getEmployeeById 时返回，仅包含对外必要字段
 * （不含 createTime/updateTime/createBy/updateBy/deleted 等审计字段）。
 * <p>
 * 字段含义：
 *   - status：员工状态（0=在职，1=离职）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@Schema(description = "员工信息")
public class HrmEmployeeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 员工 ID */
    @Schema(description = "员工 ID", example = "1234567890")
    private Long id;

    /** 工号 */
    @Schema(description = "工号", example = "EMP0001")
    private String empNo;

    /** 姓名 */
    @Schema(description = "姓名", example = "张三")
    private String name;

    /** 部门 ID */
    @Schema(description = "部门 ID", example = "1001")
    private Long deptId;

    /** 职位 */
    @Schema(description = "职位", example = "研发工程师")
    private String position;

    /** 手机号 */
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /** 邮箱 */
    @Schema(description = "邮箱", example = "zhangsan@workspace.com")
    private String email;

    /** 状态：0=在职 1=离职 */
    @Schema(description = "状态：0=在职 1=离职", example = "0")
    private Integer status;
}
