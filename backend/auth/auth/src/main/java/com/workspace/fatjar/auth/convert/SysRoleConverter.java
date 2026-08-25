package com.workspace.fatjar.auth.convert;

import com.workspace.fatjar.auth.bo.SysRoleBO;
import com.workspace.fatjar.auth.domain.SysRoleDO;
import com.workspace.fatjar.auth.vo.SysRoleVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 系统角色对象转换器（MapStruct，Spring Bean）
 * <p>
 * 提供 BO/DO/VO 之间的转换：
 *   - toBO(DO)：DO 转 BO（Service 查询返回）
 *   - toDO(BO)：BO 转 DO（持久化；忽略 createBy/updateBy/deleted 审计字段，由 MetaObjectHandler 填充）
 *   - toVO(BO)：BO 转 VO（Controller 返回前端）
 *   - toVOList(BO 列表)：批量 BO 转 VO
 * <p>
 * 说明：SysRoleBO 无 roles 字段，本转换器不涉及 UserDTO 转换（UserDTO 的 roles 由 AuthService 填充）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {})
public interface SysRoleConverter {

    /** DO 转 BO */
    SysRoleBO toBO(SysRoleDO sysRoleDO);

    /**
     * BO 转 DO（忽略 createBy/updateBy/deleted 审计字段，由 MetaObjectHandler 自动填充）
     *
     * @param sysRoleBO 角色业务对象
     * @return 角色数据对象
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysRoleDO toDO(SysRoleBO sysRoleBO);

    /** BO 转 VO */
    SysRoleVO toVO(SysRoleBO sysRoleBO);

    /** BO 列表转 VO 列表 */
    List<SysRoleVO> toVOList(List<SysRoleBO> sysRoleBOList);
}
