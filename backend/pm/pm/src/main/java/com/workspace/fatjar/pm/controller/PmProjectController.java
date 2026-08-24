package com.workspace.fatjar.pm.controller;

import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.pm.bo.PmProjectBO;
import com.workspace.fatjar.pm.convert.PmProjectConverter;
import com.workspace.fatjar.pm.exception.PmBizException;
import com.workspace.fatjar.pm.query.PmProjectQuery;
import com.workspace.fatjar.pm.resultcode.PmResultCode;
import com.workspace.fatjar.pm.service.PmProjectService;
import com.workspace.fatjar.pm.vo.PmProjectVO;
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
 * 项目控制器（CRUD）
 * <p>
 * 路径前缀：/pm/project
 * 接口列表：
 *   - GET    /pm/project/page        ：分页查询项目（支持名称模糊、状态精确）
 *   - GET    /pm/project/{id}        ：根据 ID 查询项目
 *   - POST   /pm/project             ：新增项目（内部触发项目经理存在性跨模块校验）
 *   - PUT    /pm/project             ：修改项目
 *   - DELETE /pm/project/{id}        ：根据 ID 删除项目（逻辑删除）
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，门面方法（getProjectManagerName）
 * 由 PmProjectApi 承载，仅供跨模块调用，不在此暴露 HTTP 入口。
 * 新增项目时由 ServiceImpl 在事务内调用 HrmEmployeeApi 校验项目经理存在性。
 * Controller 通过 {@link PmProjectConverter} 将 Service 返回的 BO 转换为 VO 返回前端，
 * 分页查询使用 {@link PmProjectQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/pm/project")
@RequiredArgsConstructor
@Tag(name = "项目管理-项目管理", description = "项目 CRUD")
public class PmProjectController {

    /** 项目 Service（内部契约，承载 IService CRUD 能力，含跨模块项目经理校验） */
    private final PmProjectService pmProjectService;

    /** MapStruct 转换器（BO -> VO） */
    private final PmProjectConverter converter;

    /**
     * 分页查询项目
     *
     * @param query 分页 + 过滤参数（current/size/projectName/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询项目", description = "支持名称模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<PmProjectVO>> page(@Valid PmProjectQuery query) {
        PageResult<PmProjectBO> boPage = pmProjectService.pageBO(query);
        PageResult<PmProjectVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询项目
     *
     * @param id 项目 ID
     * @return 项目信息
     */
    @Operation(summary = "根据 ID 查询项目")
    @GetMapping("/{id}")
    public R<PmProjectVO> get(@Parameter(description = "项目 ID") @PathVariable Long id) {
        PmProjectBO bo = pmProjectService.getBOById(id);
        if (bo == null) {
            throw new PmBizException(PmResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增项目
     * <p>
     * 内部由 ServiceImpl 在事务内调用 HrmEmployeeApi 校验项目经理存在性，
     * 若项目经理不存在则抛 PmBizException(DATA_NOT_FOUND, "项目经理不存在")。
     *
     * @param bo 项目业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增项目", description = "内部校验项目经理存在性（跨模块调用 HRM）")
    @PostMapping
    public R<Void> save(@Parameter(description = "项目信息") @Valid @RequestBody PmProjectBO bo) {
        boolean ok = pmProjectService.saveBO(bo);
        if (!ok) {
            throw new PmBizException(PmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改项目
     *
     * @param bo 项目业务对象（id 不能为空）
     * @return 操作结果
     */
    @Operation(summary = "修改项目")
    @PutMapping
    public R<Void> update(@Parameter(description = "项目信息") @Valid @RequestBody PmProjectBO bo) {
        if (bo.getId() == null) {
            throw new PmBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "项目 ID 不能为空");
        }
        boolean ok = pmProjectService.updateBO(bo);
        if (!ok) {
            throw new PmBizException(PmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除项目（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 项目 ID
     * @return 操作结果
     */
    @Operation(summary = "删除项目", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "项目 ID") @PathVariable Long id) {
        boolean ok = pmProjectService.removeBOById(id);
        if (!ok) {
            throw new PmBizException(PmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
