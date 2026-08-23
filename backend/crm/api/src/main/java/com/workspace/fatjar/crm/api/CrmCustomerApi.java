package com.workspace.fatjar.crm.api;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 客户关系管理模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-crm-api 模块，对外暴露 crm 模块的客户核心能力
 *   2. 实现类 CrmCustomerServiceImpl 同时实现本接口与内部 CrmCustomerService 接口，
 *      一个实现满足「门面」与「内部」双契约
 *   3. 其他业务模块通过依赖 fatjar-crm-api 即可调用本接口，
 *      无需感知 crm 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 门面接口仅包含「跨模块」必要方法，面向内部 CRUD 的 HTTP 入口由 CrmCustomerController 承载
 *   5. 返回值统一使用 DTO 或基础类型，不直接暴露 Entity，避免实现细节外泄
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "客户关系管理模块门面", description = "暴露给其他业务模块调用的客户能力契约")
public interface CrmCustomerApi {

    /**
     * 客户名称查询（跨模块）
     * <p>
     * 供其他模块（如销售订单、合同等）校验客户存在性或获取展示名称使用。
     * 客户不存在或被逻辑删除时返回 null，调用方需自行处理。
     *
     * @param customerId 客户 ID
     * @return 客户名称，客户不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    String getCustomerName(Long customerId);
}
