package com.workspace.fatjar.auth.bo;

import com.workspace.fatjar.common.bo.BaseBO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色业务对象（Service 层输入输出）
 * <p>
 * 字段同 SysRoleDO 业务字段；与 DO 通过 MapStruct 双向转换。
 * 公共字段继承自 {@link BaseBO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统角色业务对象")
public class SysRoleBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 角色编码（业务唯一） */
    @Schema(description = "角色编码", example = "admin")
    private String roleCode;

    /** 角色名称（展示用） */
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;

    /** 状态：0=正常 1=禁用 */
    @Schema(description = "状态：0=正常 1=禁用", example = "0")
    private Integer status;

    /** 备注 */
    @Schema(description = "备注")
    private String remark;
}
