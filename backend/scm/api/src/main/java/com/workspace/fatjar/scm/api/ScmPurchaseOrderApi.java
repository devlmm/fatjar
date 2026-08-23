package com.workspace.fatjar.scm.api;

import com.workspace.fatjar.scm.dto.ScmPurchaseOrderDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;

/**
 * 供应链管理模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-scm-api 模块，对外暴露 scm 模块的核心能力
 *   2. 实现类 ScmPurchaseOrderServiceImpl 同时实现本接口与内部 ScmPurchaseOrderService 接口，
 *      一个实现满足双契约
 *   3. 其他业务模块通过依赖 fatjar-scm-api 即可调用本接口查询采购金额，
 *      无需感知 scm 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 实际 HTTP 入口由 ScmPurchaseOrderController 调用，门面接口主要服务于跨模块 RPC 风格调用
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "供应链管理模块门面", description = "暴露给其他业务模块调用的供应链能力契约")
public interface ScmPurchaseOrderApi {

    /**
     * 查询采购订单金额（跨模块调用入口）
     * <p>
     * 根据订单 ID 查询采购订单总金额，供其他模块（如财务对账）使用。
     *
     * @param orderId 采购订单 ID
     * @return 采购订单总金额，订单不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    BigDecimal getPurchaseAmount(Long orderId);
}
