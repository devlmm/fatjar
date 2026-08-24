package com.workspace.fatjar.hrm.controller;

import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.hrm.bo.HrmEmployeeBO;
import com.workspace.fatjar.hrm.convert.HrmEmployeeConverter;
import com.workspace.fatjar.hrm.query.HrmEmployeeQuery;
import com.workspace.fatjar.hrm.resultcode.HrmResultCode;
import com.workspace.fatjar.hrm.service.HrmEmployeeService;
import com.workspace.fatjar.hrm.vo.HrmEmployeeVO;
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
 * 由 HrmEmployeeApi 承载，仅供跨模块调用，不在此暴露 HTTP 入口。Controller 通过
 * {@link HrmEmployeeConverter} 将 Service 返回的 BO 转换为 VO 返回前端，
 * 分页查询使用 {@link HrmEmployeeQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/hrm/employee")
@RequiredArgsConstructor
@Tag(name = "人力资源-员工管理", description = "员工 CRUD")
public class HrmEmployeeController {

    /** 员工 Service（同时承担 HrmEmployeeDO 的 IService 能力） */
    private final HrmEmployeeService hrmEmployeeService;

    /** MapStruct 转换器（BO -> VO） */
    private final HrmEmployeeConverter converter;

    /**
     * 分页查询员工
     * <p>
     * 支持员工姓名模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询参数（current/size/name/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询员工", description = "支持姓名模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<HrmEmployeeVO>> page(@Valid HrmEmployeeQuery query) {
        PageResult<HrmEmployeeBO> boPage = hrmEmployeeService.pageBO(query);
        PageResult<HrmEmployeeVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询员工
     *
     * @param id 员工 ID
     * @return 员工信息
     */
    @Operation(summary = "根据 ID 查询员工")
    @GetMapping("/{id}")
    public R<HrmEmployeeVO> get(@Parameter(description = "员工 ID") @PathVariable Long id) {
        HrmEmployeeBO bo = hrmEmployeeService.getBOById(id);
        if (bo == null) {
            throw new com.workspace.fatjar.hrm.exception.HrmBizException(HrmResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增员工
     *
     * @param bo 员工业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增员工")
    @PostMapping
    public R<Void> save(@Parameter(description = "员工信息") @Valid @RequestBody HrmEmployeeBO bo) {
        boolean ok = hrmEmployeeService.saveBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.hrm.exception.HrmBizException(HrmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改员工
     *
     * @param bo 员工业务对象（id 不能为空）
     * @return 操作结果
     */
    @Operation(summary = "修改员工")
    @PutMapping
    public R<Void> update(@Parameter(description = "员工信息") @Valid @RequestBody HrmEmployeeBO bo) {
        if (bo.getId() == null) {
            throw new com.workspace.fatjar.hrm.exception.HrmBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "员工 ID 不能为空");
        }
        boolean ok = hrmEmployeeService.updateBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.hrm.exception.HrmBizException(HrmResultCode.OPERATION_FAILED);
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
        boolean ok = hrmEmployeeService.removeBOById(id);
        if (!ok) {
            throw new com.workspace.fatjar.hrm.exception.HrmBizException(HrmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
