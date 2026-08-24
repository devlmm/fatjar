package com.workspace.fatjar.bi.controller;

import com.workspace.fatjar.bi.bo.BiReportBO;
import com.workspace.fatjar.bi.convert.BiReportConverter;
import com.workspace.fatjar.bi.exception.BiBizException;
import com.workspace.fatjar.bi.query.BiReportQuery;
import com.workspace.fatjar.bi.resultcode.BiResultCode;
import com.workspace.fatjar.bi.service.BiReportService;
import com.workspace.fatjar.bi.vo.BiReportVO;
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
 * BI 报表控制器（CRUD REST）
 * <p>
 * 路径前缀：/bi/report
 * 接口列表：
 *   - GET    /bi/report/page   ：分页查询报表
 *   - GET    /bi/report/{id}   ：根据 ID 查询报表
 *   - POST   /bi/report        ：新增报表
 *   - PUT    /bi/report        ：修改报表
 *   - DELETE /bi/report/{id}   ：根据 ID 删除报表
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，不暴露门面方法（门面方法供其他模块跨模块调用）。
 * Controller 通过 {@link BiReportConverter} 将 Service 返回的 BO 转换为 VO 返回前端，
 * 分页查询使用 {@link BiReportQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/bi/report")
@RequiredArgsConstructor
@Tag(name = "商业智能-报表管理", description = "BI 报表 CRUD")
public class BiReportController {

    /** 报表 Service（内部视角，同时承担 BiReportApi 门面实现） */
    private final BiReportService biReportService;

    /** MapStruct 转换器（BO -> VO） */
    private final BiReportConverter converter;

    /**
     * 分页查询报表
     *
     * @param query 分页查询参数（current/size/reportName/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询报表", description = "支持报表名称模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<BiReportVO>> page(@Valid BiReportQuery query) {
        PageResult<BiReportBO> boPage = biReportService.pageBO(query);
        PageResult<BiReportVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询报表
     *
     * @param id 报表 ID
     * @return 报表信息
     */
    @Operation(summary = "根据 ID 查询报表")
    @GetMapping("/{id}")
    public R<BiReportVO> get(@Parameter(description = "报表 ID") @PathVariable Long id) {
        BiReportBO bo = biReportService.getBOById(id);
        if (bo == null) {
            throw new BiBizException(BiResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增报表
     *
     * @param bo 报表业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增报表")
    @PostMapping
    public R<Void> save(@Parameter(description = "报表信息") @Valid @RequestBody BiReportBO bo) {
        boolean ok = biReportService.saveBO(bo);
        if (!ok) {
            throw new BiBizException(BiResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改报表
     *
     * @param bo 报表业务对象（id 不能为空）
     * @return 操作结果
     */
    @Operation(summary = "修改报表")
    @PutMapping
    public R<Void> update(@Parameter(description = "报表信息") @Valid @RequestBody BiReportBO bo) {
        if (bo.getId() == null) {
            throw new BiBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "报表 ID 不能为空");
        }
        boolean ok = biReportService.updateBO(bo);
        if (!ok) {
            throw new BiBizException(BiResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除报表（逻辑删除）
     *
     * @param id 报表 ID
     * @return 操作结果
     */
    @Operation(summary = "删除报表", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "报表 ID") @PathVariable Long id) {
        boolean ok = biReportService.removeBOById(id);
        if (!ok) {
            throw new BiBizException(BiResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
