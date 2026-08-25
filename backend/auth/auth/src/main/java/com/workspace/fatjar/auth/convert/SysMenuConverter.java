package com.workspace.fatjar.auth.convert;

import com.workspace.fatjar.auth.bo.SysMenuBO;
import com.workspace.fatjar.auth.domain.SysMenuDO;
import com.workspace.fatjar.auth.vo.SysMenuVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 系统菜单对象转换器（MapStruct，Spring Bean）
 * <p>
 * 提供 BO/DO/VO 之间的转换：
 *   - toBO(DO)：DO 转 BO（Service 查询返回）
 *   - toDO(BO)：BO 转 DO（持久化；忽略 createBy/updateBy/deleted 审计字段，由 MetaObjectHandler 填充）
 *   - toVO(BO)：BO 转 VO（Controller 返回前端）
 *   - toVOList(BO 列表)：批量 BO 转 VO
 * <p>
 * 说明：MenuDTO 的树形构建在 SysMenuServiceImpl.treeBO 中手写实现（按 parentId 递归挂 children）。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring", uses = {})
public interface SysMenuConverter {

    /** DO 转 BO */
    SysMenuBO toBO(SysMenuDO sysMenuDO);

    /**
     * BO 转 DO（忽略 createBy/updateBy/deleted 审计字段，由 MetaObjectHandler 自动填充）
     *
     * @param sysMenuBO 菜单业务对象
     * @return 菜单数据对象
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    SysMenuDO toDO(SysMenuBO sysMenuBO);

    /** BO 转 VO */
    SysMenuVO toVO(SysMenuBO sysMenuBO);

    /** BO 列表转 VO 列表 */
    List<SysMenuVO> toVOList(List<SysMenuBO> sysMenuBOList);
}
