package com.workspace.fatjar.bi.controller;

import com.workspace.fatjar.bi.entity.BiReport;
import com.workspace.fatjar.bi.ro.BiReportPageRO;
import com.workspace.fatjar.bi.service.BiReportService;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
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
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/bi/report")
@Tag(name = "商业智能-报表管理", description = "BI 报表 CRUD")
public class BiReportController {

    /** 报表 Service（内部视角，同时承担 BiReportApi 门面实现） */
    private final BiReportService biReportService;

    /**
     * 构造器注入（@Autowired 显式声明）
     *
     * @param biReportService 报表 Service
     */
    @Autowired
    public BiReportController(BiReportService biReportService) {
        this.biReportService = biReportService;
    }

    /**
     * 分页查询报表
     *
     * @param ro 分页查询请求（含 current/size + reportName/status 过滤条件）
     * @return 分页结果
     */
    @Operation(summary = "分页查询报表", description = "支持报表名称模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<BiReport>> page(@Valid BiReportPageRO ro) {
        PageResult<BiReport> result = biReportService.page(ro);
        return R.ok(result);
    }

    /**
     * 根据 ID 查询报表
     *
     * @param id 报表 ID
     * @return 报表信息
     */
    @Operation(summary = "根据 ID 查询报表")
    @GetMapping("/{id}")
    public R<BiReport> get(@Parameter(description = "报表 ID") @PathVariable Long id) {
        BiReport report = biReportService.getById(id);
        if (report == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(report);
    }

    /**
     * 新增报表
     *
     * @param report 报表实体
     * @return 操作结果
     */
    @Operation(summary = "新增报表")
    @PostMapping
    public R<Void> save(@Parameter(description = "报表信息") @Valid @RequestBody BiReport report) {
        boolean ok = biReportService.save(report);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改报表
     *
     * @param report 报表实体
     * @return 操作结果
     */
    @Operation(summary = "修改报表")
    @PutMapping
    public R<Void> update(@Parameter(description = "报表信息") @Valid @RequestBody BiReport report) {
        if (report.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "报表 ID 不能为空");
        }
        boolean ok = biReportService.update(report);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
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
        boolean ok = biReportService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
