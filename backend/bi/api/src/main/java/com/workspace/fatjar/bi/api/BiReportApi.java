package com.workspace.fatjar.bi.api;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 商业智能模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-bi-api 模块，对外暴露 bi 模块的核心能力（仅跨模块必需方法）
 *   2. 实现类 BiReportServiceImpl 同时实现本接口与内部 BiReportService 接口，一个实现满足双契约
 *   3. 其他业务模块（auth/oa/crm 等）通过依赖 fatjar-bi-api 即可调用本接口，
 *      无需感知 bi 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 门面接口仅暴露跨模块方法，内部 CRUD 由 BiReportController 直接暴露，
 *      门面方法服务于跨模块 RPC 风格调用
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "商业智能模块门面", description = "暴露给其他业务模块调用的报表能力契约")
public interface BiReportApi {

    /**
     * 报表名称查询（跨模块）
     * <p>
     * 供其他业务模块在无需感知 bi 实现的前提下，根据报表 ID 获取报表名称，
     * 常用于关联展示、审批明细、推送通知等场景。
     *
     * @param reportId 报表 ID
     * @return 报表名称；报表不存在时返回 null
     * @author fatjar
     * @since 1.0.0
     */
    String getReportName(Long reportId);
}
