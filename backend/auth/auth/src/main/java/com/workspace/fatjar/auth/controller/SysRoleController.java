package com.workspace.fatjar.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.auth.entity.SysRole;
import com.workspace.fatjar.auth.mapper.SysRoleMapper;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageQuery;
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
import org.springframework.web.bind.annotation.RequestParam;
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

    /** 角色 Mapper（直接使用 BaseMapper 提供的 CRUD） */
    private final SysRoleMapper sysRoleMapper;

    /**
     * 分页查询角色
     *
     * @param pageQuery 分页参数
     * @param roleName  角色名称（模糊）
     * @param roleCode  角色编码（模糊）
     * @param status    状态（精确）
     * @return 分页结果
     */
    @Operation(summary = "分页查询角色", description = "支持名称/编码模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<SysRole>> page(@Valid PageQuery pageQuery,
                                       @Parameter(description = "角色名称（模糊）") @RequestParam(required = false) String roleName,
                                       @Parameter(description = "角色编码（模糊）") @RequestParam(required = false) String roleCode,
                                       @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<SysRole> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(roleName != null && !roleName.isEmpty(), SysRole::getRoleName, roleName);
        wrapper.like(roleCode != null && !roleCode.isEmpty(), SysRole::getRoleCode, roleCode);
        wrapper.eq(status != null, SysRole::getStatus, status);
        wrapper.orderByDesc(SysRole::getCreateTime);
        Page<SysRole> result = sysRoleMapper.selectPage(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询角色
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    @Operation(summary = "根据 ID 查询角色")
    @GetMapping("/{id}")
    public R<SysRole> get(@Parameter(description = "角色 ID") @PathVariable Long id) {
        SysRole role = sysRoleMapper.selectById(id);
        if (role == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(role);
    }

    /**
     * 新增角色
     *
     * @param role 角色实体
     * @return 操作结果
     */
    @Operation(summary = "新增角色")
    @PostMapping("/save")
    public R<Void> save(@Parameter(description = "角色信息") @Valid @RequestBody SysRole role) {
        int rows = sysRoleMapper.insert(role);
        if (rows <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改角色
     *
     * @param role 角色实体
     * @return 操作结果
     */
    @Operation(summary = "修改角色")
    @PutMapping("/update")
    public R<Void> update(@Parameter(description = "角色信息") @Valid @RequestBody SysRole role) {
        if (role.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "角色 ID 不能为空");
        }
        int rows = sysRoleMapper.updateById(role);
        if (rows <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
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
        int rows = sysRoleMapper.deleteById(id);
        if (rows <= 0) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
