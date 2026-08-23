package com.workspace.fatjar.scm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.scm.entity.ScmPurchaseOrder;
import com.workspace.fatjar.scm.ro.ScmPurchaseOrderPageRO;
import com.workspace.fatjar.scm.service.ScmPurchaseOrderService;
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
 * 采购订单控制器（CRUD）
 * <p>
 * 路径前缀：/scm/purchase-order
 * 接口列表：
 *   - GET    /scm/purchase-order/page        ：分页查询采购订单
 *   - GET    /scm/purchase-order/{id}        ：根据 ID 查询采购订单
 *   - POST   /scm/purchase-order             ：新增采购订单（保存前自动调用 FICO 预算校验）
 *   - PUT    /scm/purchase-order             ：修改采购订单
 *   - DELETE /scm/purchase-order/{id}        ：根据 ID 删除采购订单（逻辑删除）
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，门面方法（getPurchaseAmount）由 ScmPurchaseOrderApi
 * 跨模块调用，不在 Controller 重复暴露。新增接口的预算校验逻辑在 Service.save 内部完成，
 * Controller 无需感知跨模块调用细节。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/scm/purchase-order")
@Tag(name = "供应链管理模块-采购订单", description = "采购订单 CRUD")
public class ScmPurchaseOrderController {

    /** 采购订单 Service（同时承担 ScmPurchaseOrder 的 IService 能力） */
    private final ScmPurchaseOrderService scmPurchaseOrderService;

    /**
     * 构造器注入（@Autowired 标注构造器，显式声明依赖）
     *
     * @param scmPurchaseOrderService 采购订单 Service
     */
    @Autowired
    public ScmPurchaseOrderController(ScmPurchaseOrderService scmPurchaseOrderService) {
        this.scmPurchaseOrderService = scmPurchaseOrderService;
    }

    /**
     * 分页查询采购订单
     * <p>
     * 支持订单编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param ro 分页查询参数（current/size/orderNo/status）
     * @return 分页结果
     */
    @Operation(summary = "分页查询采购订单", description = "支持订单编号模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<ScmPurchaseOrder>> page(@Valid ScmPurchaseOrderPageRO ro) {
        Page<ScmPurchaseOrder> page = new Page<>(ro.getCurrent(), ro.getSize());
        LambdaQueryWrapper<ScmPurchaseOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ro.getOrderNo() != null && !ro.getOrderNo().isEmpty(),
                ScmPurchaseOrder::getOrderNo, ro.getOrderNo());
        wrapper.eq(ro.getStatus() != null, ScmPurchaseOrder::getStatus, ro.getStatus());
        wrapper.orderByDesc(ScmPurchaseOrder::getCreateTime);
        Page<ScmPurchaseOrder> result = scmPurchaseOrderService.page(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询采购订单
     *
     * @param id 订单 ID
     * @return 订单信息
     */
    @Operation(summary = "根据 ID 查询采购订单")
    @GetMapping("/{id}")
    public R<ScmPurchaseOrder> get(@Parameter(description = "订单 ID") @PathVariable Long id) {
        ScmPurchaseOrder order = scmPurchaseOrderService.getById(id);
        if (order == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(order);
    }

    /**
     * 新增采购订单
     * <p>
     * 保存前由 Service 内部调用 FicoVoucherApi.checkBudget 做跨模块预算校验，
     * 预算不足时抛 BizException 由全局异常处理器转换为失败响应。
     *
     * @param order 采购订单实体
     * @return 操作结果
     */
    @Operation(summary = "新增采购订单", description = "保存前自动调用 FICO 预算校验")
    @PostMapping
    public R<Void> save(@Parameter(description = "订单信息") @Valid @RequestBody ScmPurchaseOrder order) {
        boolean ok = scmPurchaseOrderService.save(order);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改采购订单
     *
     * @param order 采购订单实体
     * @return 操作结果
     */
    @Operation(summary = "修改采购订单")
    @PutMapping
    public R<Void> update(@Parameter(description = "订单信息") @Valid @RequestBody ScmPurchaseOrder order) {
        if (order.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "订单 ID 不能为空");
        }
        boolean ok = scmPurchaseOrderService.updateById(order);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除采购订单（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 订单 ID
     * @return 操作结果
     */
    @Operation(summary = "删除采购订单", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "订单 ID") @PathVariable Long id) {
        boolean ok = scmPurchaseOrderService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
