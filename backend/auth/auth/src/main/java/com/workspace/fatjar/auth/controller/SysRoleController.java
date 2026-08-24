package com.workspace.fatjar.auth.controller;

import com.workspace.fatjar.auth.bo.SysRoleBO;
import com.workspace.fatjar.auth.convert.SysRoleConverter;
import com.workspace.fatjar.auth.query.SysRoleQuery;
import com.workspace.fatjar.auth.service.SysRoleService;
import com.workspace.fatjar.auth.vo.SysRoleVO;
import com.workspace.fatjar.auth.exception.AuthBizException;
import com.workspace.fatjar.common.result.CommonResultCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * 系统角色控制器（CRUD）
 * <p>
 * 路径前缀：/sys/role
 * 接口列表：
 *   - GET    /sys/role/page        ：分页查询角色
 *   - GET    /sys/role/{id}        ：根据 ID 查询角色
 *   - POST   /sys/role/save        ：新增角色
 *   - PUT    /sys/role/update       ：修改角色
 *   - DELETE /sys/role/{id}        ：根据 ID 删除角色（逻辑删除）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
@Tag(name = "权限模块-角色管理", description = "系统角色 CRUD")
public class SysRoleController {

    /** 角色 Service（提供 BO 系列 CRUD 方法） */
    private final SysRoleService sysRoleService;
    /** MapStruct 转换器（BO -> VO） */
    private final SysRoleConverter converter;

    /**
     * 分页查询角色
     *
     * @param query 分页查询参数（current/size + roleName/roleCode/status）
     * @return 分页结果
     */
    @Operation(summary = "分页查询角色", description = "支持名称/编码模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<SysRoleVO>> page(@Valid SysRoleQuery query) {
        PageResult<SysRoleBO> boPage = sysRoleService.pageBO(query);
        PageResult<SysRoleVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    @Operation(summary = "根据 ID 查询角色")
    @GetMapping("/{id}")
    public R<SysRoleVO> get(@Parameter(description = "角色 ID") @PathVariable Long id) {
        SysRoleBO bo = sysRoleService.getBOById(id);
        if (bo == null) {
            throw new AuthBizException(CommonResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增角色
     *
     * @param bo 角色业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增角色")
    @PostMapping("/save")
    public R<Void> save(@Parameter(description = "角色信息") @Valid @RequestBody SysRoleBO bo) {
        boolean ok = sysRoleService.saveBO(bo);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改角色
     *
     * @param bo 角色业务对象
     * @return 操作结果
     */
    @Operation(summary = "修改角色")
    @PutMapping("/update")
    public R<Void> update(@Parameter(description = "角色信息") @Valid @RequestBody SysRoleBO bo) {
        if (bo.getId() == null) {
            throw new AuthBizException(CommonResultCode.PARAM_INVALID, "角色 ID 不能为空");
        }
        boolean ok = sysRoleService.updateBO(bo);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除角色（逻辑删除）
     *
     * @param id 角色 ID
     * @return 操作结果
     */
    @Operation(summary = "删除角色", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "角色 ID") @PathVariable Long id) {
        boolean ok = sysRoleService.removeBOById(id);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
