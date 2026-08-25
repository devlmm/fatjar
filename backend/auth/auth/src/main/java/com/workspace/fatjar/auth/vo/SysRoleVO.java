package com.workspace.fatjar.auth.vo;

import com.workspace.fatjar.common.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统角色视图对象（Controller 层返回前端）
 * <p>
 * 公共字段继承自 {@link BaseVO}。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "系统角色信息")
public class SysRoleVO extends BaseVO {

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
