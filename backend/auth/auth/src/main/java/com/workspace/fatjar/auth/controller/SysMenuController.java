package com.workspace.fatjar.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.entity.SysMenu;
import com.workspace.fatjar.auth.mapper.SysMenuMapper;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageQuery;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统菜单控制器（CRUD + 菜单树）
 * <p>
 * 路径前缀：/sys/menu
 * 接口列表：
 *   - GET    /sys/menu/page        ：分页查询菜单
 *   - GET    /sys/menu/{id}        ：根据 ID 查询菜单
 *   - GET    /sys/menu/tree        ：获取全量菜单树（用于后台管理界面渲染）
 *   - POST   /sys/menu/save        ：新增菜单
 *   - PUT    /sys/menu/update       ：修改菜单
 *   - DELETE /sys/menu/{id}        ：根据 ID 删除菜单（逻辑删除）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
@Tag(name = "权限模块-菜单管理", description = "系统菜单 CRUD 与菜单树")
public class SysMenuController {

    /** 菜单 Mapper（直接使用 BaseMapper + 自定义查询） */
    private final SysMenuMapper sysMenuMapper;

    /**
     * 分页查询菜单
     *
     * @param pageQuery 分页参数
     * @param name      菜单名称（模糊）
     * @param type      类型（0=目录 1=菜单 2=按钮）
     * @param status    状态（精确）
     * @return 分页结果
     */
    @Operation(summary = "分页查询菜单", description = "支持名称模糊、类型/状态精确查询")
    @GetMapping("/page")
    public R<PageResult<SysMenu>> page(@Valid PageQuery pageQuery,
                                       @Parameter(description = "菜单名称（模糊）") @RequestParam(required = false) String name,
                                       @Parameter(description = "类型：0=目录 1=菜单 2=按钮") @RequestParam(required = false) Integer type,
                                       @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<SysMenu> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), SysMenu::getName, name);
        wrapper.eq(type != null, SysMenu::getType, type);
        wrapper.eq(status != null, SysMenu::getStatus, status);
        wrapper.orderByAsc(SysMenu::getSort);
        Page<SysMenu> result = sysMenuMapper.selectPage(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询菜单
     *
     * @param id 菜单 ID
     * @return 菜单信息
     */
    @Operation(summary = "根据 ID 查询菜单")
    @GetMapping("/{id}")
    public R<SysMenu> get(@Parameter(description = "菜单 ID") @PathVariable Long id) {
        SysMenu menu = sysMenuMapper.selectById(id);
        if (menu == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(menu);
    }

    /**
     * 获取全量菜单树（不分页，用于后台管理界面）
     * <p>
     * 查询所有启用且未删除的菜单，按 parentId 递归构建父子树。
     *
     * @return 菜单树
     */
    @Operation(summary = "获取全量菜单树", description = "用于后台菜单管理界面渲染")
    @GetMapping("/tree")
    public R<List<MenuDTO>> tree() {
        // 查询所有未删除的菜单，按 sort 升序排列
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = sysMenuMapper.selectList(wrapper);
        if (menus == null || menus.isEmpty()) {
            return R.ok(Collections.emptyList());
        }
        return R.ok(buildTree(menus));
    }

    /**
     * 新增菜单
     *
     * @param menu 菜单实体
     * @return 操作结果
     */
    @Operation(summary = "新增菜单")
    @PostMapping("/save")
    public R<Void> save(@Parameter(description = "菜单信息") @Valid @RequestBody SysMenu menu) {
        int rows = sysMenuMapper.insert(menu);
        if (rows <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改菜单
     *
     * @param menu 菜单实体
     * @return 操作结果
     */
    @Operation(summary = "修改菜单")
    @PutMapping("/update")
    public R<Void> update(@Parameter(description = "菜单信息") @Valid @RequestBody SysMenu menu) {
        if (menu.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "菜单 ID 不能为空");
        }
        int rows = sysMenuMapper.updateById(menu);
        if (rows <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除菜单（逻辑删除）
     *
     * @param id 菜单 ID
     * @return 操作结果
     */
    @Operation(summary = "删除菜单", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "菜单 ID") @PathVariable Long id) {
        int rows = sysMenuMapper.deleteById(id);
        if (rows <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 构建菜单树（按 parentId 分组递归挂 children）
     * <p>
     * 算法：
     *   1. 将所有 SysMenu 转换为 MenuDTO
     *   2. 按 parentId 分组
     *   3. 遍历每个 DTO，从 Map 取其子节点列表挂到 children
     *   4. 返回 parentId == 0 的顶级菜单列表
     *
     * @param menus 菜单列表
     * @return 菜单树
     */
    private List<MenuDTO> buildTree(List<SysMenu> menus) {
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
     * SysMenu 转 MenuDTO（字段逐一拷贝）
     *
     * @param menu 菜单实体
     * @return 菜单 DTO
     */
    private MenuDTO toMenuDTO(SysMenu menu) {
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
