package com.workspace.fatjar.mes.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.mes.entity.MesWorkOrder;
import com.workspace.fatjar.mes.ro.MesWorkOrderPageRO;
import com.workspace.fatjar.mes.service.MesWorkOrderService;
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
 * 跨模块调用，不在 Controller 重复暴露。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/mes/work-order")
@Tag(name = "制造执行系统模块-工单", description = "工单 CRUD")
public class MesWorkOrderController {

    /** 工单 Service（同时承担 MesWorkOrder 的 IService 能力） */
    private final MesWorkOrderService mesWorkOrderService;

    /**
     * 构造器注入（@Autowired 标注构造器，显式声明依赖）
     *
     * @param mesWorkOrderService 工单 Service
     */
    @Autowired
    public MesWorkOrderController(MesWorkOrderService mesWorkOrderService) {
        this.mesWorkOrderService = mesWorkOrderService;
    }

    /**
     * 分页查询工单
     * <p>
     * 支持工单编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param ro 分页查询参数（current/size/workOrderNo/status）
     * @return 分页结果
     */
    @Operation(summary = "分页查询工单", description = "支持工单编号模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<MesWorkOrder>> page(@Valid MesWorkOrderPageRO ro) {
        Page<MesWorkOrder> page = new Page<>(ro.getCurrent(), ro.getSize());
        LambdaQueryWrapper<MesWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ro.getWorkOrderNo() != null && !ro.getWorkOrderNo().isEmpty(),
                MesWorkOrder::getWorkOrderNo, ro.getWorkOrderNo());
        wrapper.eq(ro.getStatus() != null, MesWorkOrder::getStatus, ro.getStatus());
        wrapper.orderByDesc(MesWorkOrder::getCreateTime);
        Page<MesWorkOrder> result = mesWorkOrderService.page(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询工单
     *
     * @param id 工单 ID
     * @return 工单信息
     */
    @Operation(summary = "根据 ID 查询工单")
    @GetMapping("/{id}")
    public R<MesWorkOrder> get(@Parameter(description = "工单 ID") @PathVariable Long id) {
        MesWorkOrder workOrder = mesWorkOrderService.getById(id);
        if (workOrder == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(workOrder);
    }

    /**
     * 新增工单
     *
     * @param workOrder 工单实体
     * @return 操作结果
     */
    @Operation(summary = "新增工单")
    @PostMapping
    public R<Void> save(@Parameter(description = "工单信息") @Valid @RequestBody MesWorkOrder workOrder) {
        boolean ok = mesWorkOrderService.save(workOrder);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改工单
     *
     * @param workOrder 工单实体
     * @return 操作结果
     */
    @Operation(summary = "修改工单")
    @PutMapping
    public R<Void> update(@Parameter(description = "工单信息") @Valid @RequestBody MesWorkOrder workOrder) {
        if (workOrder.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "工单 ID 不能为空");
        }
        boolean ok = mesWorkOrderService.updateById(workOrder);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
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
        boolean ok = mesWorkOrderService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
