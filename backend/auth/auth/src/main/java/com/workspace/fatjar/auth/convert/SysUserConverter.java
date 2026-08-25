package com.workspace.fatjar.auth.convert;

import com.workspace.fatjar.auth.bo.SysUserBO;
import com.workspace.fatjar.auth.domain.SysUserDO;
import com.workspace.fatjar.auth.dto.UserDTO;
import com.workspace.fatjar.auth.vo.SysUserVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 系统用户对象转换器（MapStruct，Spring Bean）
 * <p>
 * 提供 BO/DO/VO/DTO 之间的转换：
 *   - toBO(DO)：DO 转 BO（Service 查询返回）
 *   - toDO(BO)：BO 转 DO（持久化；忽略 createBy/updateBy/deleted 审计字段，由 MetaObjectHandler 填充）
 *   - toVO(BO)：BO 转 VO（Controller 返回前端，自动剔除 password）
 *   - toVOList(BO 列表)：批量 BO 转 VO
 *   - toDTO(BO)：BO 转 UserDTO（跨模块门面，仅 id/username/nickname/status 等公开字段）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {})
public interface SysUserConverter {

    /** DO 转 BO */
    SysUserBO toBO(SysUserDO sysUserDO);

    /**
     * BO 转 DO（忽略 createBy/updateBy/deleted 审计字段，由 MetaObjectHandler 自动填充）
     *
     * @param sysUserBO 用户业务对象
     * @return 用户数据对象
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysUserDO toDO(SysUserBO sysUserBO);

    /** BO 转 VO（SysUserVO 无 password 字段，从结构上避免响应泄露密文） */
    SysUserVO toVO(SysUserBO sysUserBO);

    /** BO 列表转 VO 列表 */
    List<SysUserVO> toVOList(List<SysUserBO> sysUserBOList);

    /**
     * BO 转 UserDTO（跨模块门面对象）
     * <p>
     * UserDTO 继承 BaseDTO 仅含 id，无 createTime/updateTime/createBy/updateBy/deleted 字段，
     * MapStruct 自动忽略源端审计字段，无需显式 @Mapping。
     * roles 字段不在 BO 中，由 AuthService.getUserById 单独填充。
     *
     * @param sysUserBO 用户业务对象
     * @return 用户 DTO
     */
    UserDTO toDTO(SysUserBO sysUserBO);
}
