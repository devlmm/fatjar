package com.workspace.fatjar.hrm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.hrm.entity.HrmEmployee;
import com.workspace.fatjar.hrm.ro.HrmEmployeePageRO;
import com.workspace.fatjar.hrm.service.HrmEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工控制器（CRUD）
 * <p>
 * 路径前缀：/hrm/employee
 * 接口列表：
 *   - GET    /hrm/employee/page        ：分页查询员工（支持姓名模糊、状态精确）
 *   - GET    /hrm/employee/{id}        ：根据 ID 查询员工
 *   - POST   /hrm/employee             ：新增员工
 *   - PUT    /hrm/employee             ：修改员工
 *   - DELETE /hrm/employee/{id}        ：根据 ID 删除员工（逻辑删除）
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，门面方法（getEmployeeName / getEmployeeById）
 * 由 HrmEmployeeApi 承载，仅供跨模块调用，不在此暴露 HTTP 入口。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/hrm/employee")
@Tag(name = "人力资源-员工管理", description = "员工 CRUD")
public class HrmEmployeeController {

    /** 员工 Service（内部契约，承载 IService CRUD 能力） */
    private final HrmEmployeeService hrmEmployeeService;

    /**
     * 构造器注入（推荐方式，便于单元测试与最终字段保证）
     *
     * @param hrmEmployeeService 员工 Service
     */
    @Autowired
    public HrmEmployeeController(HrmEmployeeService hrmEmployeeService) {
        this.hrmEmployeeService = hrmEmployeeService;
    }

    /**
     * 分页查询员工
     *
     * @param ro 分页 + 过滤参数（current/size/name/status）
     * @return 分页结果
     */
    @Operation(summary = "分页查询员工", description = "支持姓名模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<HrmEmployee>> page(@Valid HrmEmployeePageRO ro) {
        Page<HrmEmployee> page = new Page<>(ro.getCurrent(), ro.getSize());
        LambdaQueryWrapper<HrmEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ro.getName() != null && !ro.getName().isEmpty(), HrmEmployee::getName, ro.getName());
        wrapper.eq(ro.getStatus() != null, HrmEmployee::getStatus, ro.getStatus());
        wrapper.orderByDesc(HrmEmployee::getCreateTime);
        Page<HrmEmployee> result = hrmEmployeeService.page(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询员工
     *
     * @param id 员工 ID
     * @return 员工信息
     */
    @Operation(summary = "根据 ID 查询员工")
    @GetMapping("/{id}")
    public R<HrmEmployee> get(@Parameter(description = "员工 ID") @PathVariable Long id) {
        HrmEmployee employee = hrmEmployeeService.getById(id);
        if (employee == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(employee);
    }

    /**
     * 新增员工
     *
     * @param entity 员工实体
     * @return 操作结果
     */
    @Operation(summary = "新增员工")
    @PostMapping
    public R<Void> save(@Parameter(description = "员工信息") @Valid @RequestBody HrmEmployee entity) {
        boolean ok = hrmEmployeeService.save(entity);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改员工
     *
     * @param entity 员工实体（id 不能为空）
     * @return 操作结果
     */
    @Operation(summary = "修改员工")
    @PutMapping
    public R<Void> update(@Parameter(description = "员工信息") @Valid @RequestBody HrmEmployee entity) {
        if (entity.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "员工 ID 不能为空");
        }
        boolean ok = hrmEmployeeService.updateById(entity);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除员工（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 员工 ID
     * @return 操作结果
     */
    @Operation(summary = "删除员工", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "员工 ID") @PathVariable Long id) {
        boolean ok = hrmEmployeeService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
