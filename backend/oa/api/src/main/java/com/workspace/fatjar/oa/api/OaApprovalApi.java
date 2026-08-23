package com.workspace.fatjar.oa.api;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 办公自动化模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-oa-api 模块，对外暴露 oa 模块的核心能力（仅跨模块必需方法）
 *   2. 实现类 OaApprovalServiceImpl 同时实现本接口与内部 OaApprovalService 接口，一个实现满足双契约
 *   3. 其他业务模块（auth/bi/crm 等）通过依赖 fatjar-oa-api 即可调用本接口，
 *      无需感知 oa 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 门面接口仅暴露跨模块方法，内部 CRUD 由 OaApprovalController 直接暴露，
 *      门面方法服务于跨模块 RPC 风格调用
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "办公自动化模块门面", description = "暴露给其他业务模块调用的审批能力契约")
public interface OaApprovalApi {

    /**
     * 审批标题查询（跨模块）
     * <p>
     * 供其他业务模块在无需感知 oa 实现的前提下，根据审批 ID 获取审批标题，
     * 常用于消息推送、待办关联、流程追溯等场景。
     *
     * @param approvalId 审批 ID
     * @return 审批标题；审批不存在时返回 null
     * @author fatjar
     * @since 1.0.0
     */
    String getApprovalTitle(Long approvalId);
}
