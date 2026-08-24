package com.workspace.fatjar.scm.controller;

import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.scm.bo.ScmPurchaseOrderBO;
import com.workspace.fatjar.scm.convert.ScmPurchaseOrderConverter;
import com.workspace.fatjar.scm.query.ScmPurchaseOrderQuery;
import com.workspace.fatjar.scm.resultcode.ScmResultCode;
import com.workspace.fatjar.scm.service.ScmPurchaseOrderService;
import com.workspace.fatjar.scm.vo.ScmPurchaseOrderVO;
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
 * 跨模块调用，不在 Controller 重复暴露。新增接口的预算校验逻辑在 Service.saveBO 内部完成，
 * Controller 无需感知跨模块调用细节。Controller 通过 {@link ScmPurchaseOrderConverter} 将
 * Service 返回的 BO 转换为 VO 返回前端，分页查询使用 {@link ScmPurchaseOrderQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/scm/purchase-order")
@RequiredArgsConstructor
@Tag(name = "供应链管理模块-采购订单", description = "采购订单 CRUD")
public class ScmPurchaseOrderController {

    /** 采购订单 Service（同时承担 ScmPurchaseOrderDO 的 IService 能力） */
    private final ScmPurchaseOrderService scmPurchaseOrderService;

    /** MapStruct 转换器（BO -> VO） */
    private final ScmPurchaseOrderConverter converter;

    /**
     * 分页查询采购订单
     * <p>
     * 支持订单编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询参数（current/size/orderNo/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询采购订单", description = "支持订单编号模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<ScmPurchaseOrderVO>> page(@Valid ScmPurchaseOrderQuery query) {
        PageResult<ScmPurchaseOrderBO> boPage = scmPurchaseOrderService.pageBO(query);
        PageResult<ScmPurchaseOrderVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询采购订单
     *
     * @param id 订单 ID
     * @return 订单信息
     */
    @Operation(summary = "根据 ID 查询采购订单")
    @GetMapping("/{id}")
    public R<ScmPurchaseOrderVO> get(@Parameter(description = "订单 ID") @PathVariable Long id) {
        ScmPurchaseOrderBO bo = scmPurchaseOrderService.getBOById(id);
        if (bo == null) {
            throw new com.workspace.fatjar.scm.exception.ScmBizException(ScmResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增采购订单
     * <p>
     * 保存前由 Service 内部调用 FicoVoucherApi.checkBudget 做跨模块预算校验，
     * 预算不足时抛 ScmBizException 由全局异常处理器转换为失败响应。
     *
     * @param bo 采购订单业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增采购订单", description = "保存前自动调用 FICO 预算校验")
    @PostMapping
    public R<Void> save(@Parameter(description = "订单信息") @Valid @RequestBody ScmPurchaseOrderBO bo) {
        boolean ok = scmPurchaseOrderService.saveBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.scm.exception.ScmBizException(ScmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改采购订单
     *
     * @param bo 采购订单业务对象
     * @return 操作结果
     */
    @Operation(summary = "修改采购订单")
    @PutMapping
    public R<Void> update(@Parameter(description = "订单信息") @Valid @RequestBody ScmPurchaseOrderBO bo) {
        if (bo.getId() == null) {
            throw new com.workspace.fatjar.scm.exception.ScmBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "订单 ID 不能为空");
        }
        boolean ok = scmPurchaseOrderService.updateBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.scm.exception.ScmBizException(ScmResultCode.OPERATION_FAILED);
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
        boolean ok = scmPurchaseOrderService.removeBOById(id);
        if (!ok) {
            throw new com.workspace.fatjar.scm.exception.ScmBizException(ScmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
