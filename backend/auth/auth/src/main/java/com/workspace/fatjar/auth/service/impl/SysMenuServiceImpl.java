package com.workspace.fatjar.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.auth.bo.SysMenuBO;
import com.workspace.fatjar.auth.convert.SysMenuConverter;
import com.workspace.fatjar.auth.domain.SysMenuDO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.mapper.SysMenuMapper;
import com.workspace.fatjar.auth.query.SysMenuQuery;
import com.workspace.fatjar.auth.service.SysMenuService;
import com.workspace.fatjar.common.result.PageResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统菜单 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;SysMenuMapper, SysMenuDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link SysMenuConverter}（MapStruct）双向转换
 *   3. treeBO 查询全量菜单，先经 converter 转 BO，再手写转 MenuDTO 并按 parentId 递归构建父子树
 *      （参考 AuthServiceImpl.buildMenuTree/toMenuDTO 逻辑，此处入参为 SysMenuBO，返回 List&lt;MenuDTO&gt;）
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * 查询方法为只读，无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuDO>
        implements SysMenuService {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final SysMenuConverter converter;

    /**
     * 分页查询菜单（返回 BO 分页结果）
     * <p>
     * 支持菜单名称模糊查询、类型/状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（name/type/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<SysMenuBO> pageBO(SysMenuQuery query) {
        Page<SysMenuDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysMenuDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getName()),
                SysMenuDO::getName, query.getName());
        wrapper.eq(query.getType() != null, SysMenuDO::getType, query.getType());
        wrapper.eq(query.getStatus() != null, SysMenuDO::getStatus, query.getStatus());
        wrapper.orderByDesc(SysMenuDO::getCreateTime);
        Page<SysMenuDO> result = page(page, wrapper);
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据菜单 ID 查询菜单（返回 BO）
     *
     * @param id 菜单 ID
     * @return 菜单 BO，菜单不存在返回 null
     */
    @Override
    public SysMenuBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        SysMenuDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增菜单（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 菜单业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(SysMenuBO bo) {
        SysMenuDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改菜单（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 菜单业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(SysMenuBO bo) {
        SysMenuDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除菜单（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 菜单 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }

    /**
     * 获取全量菜单树（不分页，用于后台管理界面）
     * <p>
     * 查询所有未删除菜单（按 sort 升序），经 converter 转 BO，再手写转 MenuDTO 并按 parentId 递归构建父子树。
     *
     * @return 菜单树（顶级菜单 parentId=0），按 sort 升序排列
     */
    @Override
    public List<MenuDTO> treeBO() {
        List<SysMenuDO> menus = list(new LambdaQueryWrapper<SysMenuDO>()
                .orderByAsc(SysMenuDO::getSort));
        if (menus == null || menus.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysMenuBO> bos = menus.stream().map(converter::toBO).collect(Collectors.toList());
        return buildMenuTree(bos);
    }

    /**
     * 构建菜单树（按 parentId 分组递归挂 children）
     * <p>
     * 算法：
     *   1. 将所有 SysMenuBO 转换为 MenuDTO
     *   2. 按 parentId 分组
     *   3. 遍历每个 DTO，从 Map 取其子节点列表挂到 children
     *   4. 返回 parentId == 0 的顶级菜单列表
     *
     * @param menus 菜单 BO 列表
     * @return 菜单树
     */
    private List<MenuDTO> buildMenuTree(List<SysMenuBO> menus) {
        List<MenuDTO> dtos = menus.stream()
                .map(this::toMenuDTO)
                .collect(Collectors.toList());
        // 按 parentId 分组
        Map<Long, List<MenuDTO>> groupedByParent = dtos.stream()
                .collect(Collectors.groupingBy(MenuDTO::getParentId));
        // 设置 children
        for (MenuDTO dto : dtos) {
            List<MenuDTO> children = groupedByParent.get(dto.getId());
            if (children != null && !children.isEmpty()) {
                dto.setChildren(children);
            } else {
                dto.setChildren(new ArrayList<>());
            }
        }
        // 返回顶级菜单（parentId = 0）
        return dtos.stream()
                .filter(dto -> dto.getParentId() != null && dto.getParentId() == 0L)
                .collect(Collectors.toList());
    }

    /**
     * SysMenuBO 转 MenuDTO（字段逐一拷贝，剔除审计字段）
     *
     * @param menu 菜单 BO
     * @return 菜单 DTO
     */
    private MenuDTO toMenuDTO(SysMenuBO menu) {
        MenuDTO dto = new MenuDTO();
        dto.setId(menu.getId());
        dto.setParentId(menu.getParentId());
        dto.setName(menu.getName());
        dto.setPath(menu.getPath());
        dto.setComponent(menu.getComponent());
        dto.setIcon(menu.getIcon());
        dto.setType(menu.getType());
        dto.setPermission(menu.getPermission());
        dto.setSort(menu.getSort());
        return dto;
    }
}
