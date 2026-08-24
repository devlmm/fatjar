package com.workspace.fatjar.mes.controller;

import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.mes.bo.MesWorkOrderBO;
import com.workspace.fatjar.mes.convert.MesWorkOrderConverter;
import com.workspace.fatjar.mes.query.MesWorkOrderQuery;
import com.workspace.fatjar.mes.resultcode.MesResultCode;
import com.workspace.fatjar.mes.service.MesWorkOrderService;
import com.workspace.fatjar.mes.vo.MesWorkOrderVO;
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
 * 工单控制器（CRUD）
 * <p>
 * 路径前缀：/mes/work-order
 * 接口列表：
 *   - GET    /mes/work-order/page        ：分页查询工单
 *   - GET    /mes/work-order/{id}        ：根据 ID 查询工单
 *   - POST   /mes/work-order             ：新增工单
 *   - PUT    /mes/work-order             ：修改工单
 *   - DELETE /mes/work-order/{id}        ：根据 ID 删除工单（逻辑删除）
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，门面方法（getWorkOrderById）由 MesWorkOrderApi
 * 跨模块调用，不在 Controller 重复暴露。Controller 通过 {@link MesWorkOrderConverter} 将 Service
 * 返回的 BO 转换为 VO 返回前端，分页查询使用 {@link MesWorkOrderQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/mes/work-order")
@RequiredArgsConstructor
@Tag(name = "制造执行系统模块-工单", description = "工单 CRUD")
public class MesWorkOrderController {

    /** 工单 Service（同时承担 MesWorkOrderDO 的 IService 能力） */
    private final MesWorkOrderService mesWorkOrderService;

    /** MapStruct 转换器（BO -> VO） */
    private final MesWorkOrderConverter converter;

    /**
     * 分页查询工单
     * <p>
     * 支持工单编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询参数（current/size/workOrderNo/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询工单", description = "支持工单编号模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<MesWorkOrderVO>> page(@Valid MesWorkOrderQuery query) {
        PageResult<MesWorkOrderBO> boPage = mesWorkOrderService.pageBO(query);
        PageResult<MesWorkOrderVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询工单
     *
     * @param id 工单 ID
     * @return 工单信息
     */
    @Operation(summary = "根据 ID 查询工单")
    @GetMapping("/{id}")
    public R<MesWorkOrderVO> get(@Parameter(description = "工单 ID") @PathVariable Long id) {
        MesWorkOrderBO bo = mesWorkOrderService.getBOById(id);
        if (bo == null) {
            throw new com.workspace.fatjar.mes.exception.MesBizException(MesResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增工单
     *
     * @param bo 工单业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增工单")
    @PostMapping
    public R<Void> save(@Parameter(description = "工单信息") @Valid @RequestBody MesWorkOrderBO bo) {
        boolean ok = mesWorkOrderService.saveBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.mes.exception.MesBizException(MesResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改工单
     *
     * @param bo 工单业务对象
     * @return 操作结果
     */
    @Operation(summary = "修改工单")
    @PutMapping
    public R<Void> update(@Parameter(description = "工单信息") @Valid @RequestBody MesWorkOrderBO bo) {
        if (bo.getId() == null) {
            throw new com.workspace.fatjar.mes.exception.MesBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "工单 ID 不能为空");
        }
        boolean ok = mesWorkOrderService.updateBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.mes.exception.MesBizException(MesResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除工单（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 工单 ID
     * @return 操作结果
     */
    @Operation(summary = "删除工单", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "工单 ID") @PathVariable Long id) {
        boolean ok = mesWorkOrderService.removeBOById(id);
        if (!ok) {
            throw new com.workspace.fatjar.mes.exception.MesBizException(MesResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
