package com.workspace.fatjar.auth.controller;

import com.workspace.fatjar.auth.bo.SysMenuBO;
import com.workspace.fatjar.auth.convert.SysMenuConverter;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.exception.AuthBizException;
import com.workspace.fatjar.auth.query.SysMenuQuery;
import com.workspace.fatjar.auth.service.SysMenuService;
import com.workspace.fatjar.auth.vo.SysMenuVO;
import com.workspace.fatjar.common.result.CommonResultCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /** 菜单 Service（提供 BO 系列 CRUD + treeBO 方法） */
    private final SysMenuService sysMenuService;
    /** MapStruct 转换器（BO -> VO） */
    private final SysMenuConverter converter;

    /**
     * 分页查询菜单
     *
     * @param query 分页查询参数（current/size + name/type/status）
     * @return 分页结果
     */
    @Operation(summary = "分页查询菜单", description = "支持名称模糊、类型/状态精确查询")
    @GetMapping("/page")
    public R<PageResult<SysMenuVO>> page(@Valid SysMenuQuery query) {
        PageResult<SysMenuBO> boPage = sysMenuService.pageBO(query);
        PageResult<SysMenuVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询菜单
     *
     * @param id 菜单 ID
     * @return 菜单信息
     */
    @Operation(summary = "根据 ID 查询菜单")
    @GetMapping("/{id}")
    public R<SysMenuVO> get(@Parameter(description = "菜单 ID") @PathVariable Long id) {
        SysMenuBO bo = sysMenuService.getBOById(id);
        if (bo == null) {
            throw new AuthBizException(CommonResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 获取全量菜单树（不分页，用于后台管理界面）
     * <p>
     * 查询所有未删除的菜单，按 parentId 递归构建父子树。
     *
     * @return 菜单树
     */
    @Operation(summary = "获取全量菜单树", description = "用于后台菜单管理界面渲染")
    @GetMapping("/tree")
    public R<List<MenuDTO>> tree() {
        List<MenuDTO> tree = sysMenuService.treeBO();
        return R.ok(tree);
    }

    /**
     * 新增菜单
     *
     * @param bo 菜单业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增菜单")
    @PostMapping("/save")
    public R<Void> save(@Parameter(description = "菜单信息") @Valid @RequestBody SysMenuBO bo) {
        boolean ok = sysMenuService.saveBO(bo);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改菜单
     *
     * @param bo 菜单业务对象
     * @return 操作结果
     */
    @Operation(summary = "修改菜单")
    @PutMapping("/update")
    public R<Void> update(@Parameter(description = "菜单信息") @Valid @RequestBody SysMenuBO bo) {
        if (bo.getId() == null) {
            throw new AuthBizException(CommonResultCode.PARAM_INVALID, "菜单 ID 不能为空");
        }
        boolean ok = sysMenuService.updateBO(bo);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
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
        boolean ok = sysMenuService.removeBOById(id);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
