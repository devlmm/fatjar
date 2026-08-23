package com.workspace.fatjar.mes.api;

import com.workspace.fatjar.mes.dto.MesWorkOrderDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 制造执行系统模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-mes-api 模块，对外暴露 mes 模块的核心能力
 *   2. 实现类 MesWorkOrderServiceImpl 同时实现本接口与内部 MesWorkOrderService 接口，
 *      一个实现满足双契约
 *   3. 其他业务模块通过依赖 fatjar-mes-api 即可调用本接口查询工单，
 *      无需感知 mes 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 实际 HTTP 入口由 MesWorkOrderController 调用，门面接口主要服务于跨模块 RPC 风格调用
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "制造执行系统模块门面", description = "暴露给其他业务模块调用的制造能力契约")
public interface MesWorkOrderApi {

    /**
     * 根据工单 ID 查询工单（跨模块调用入口）
     * <p>
     * 返回对外 DTO（MesWorkOrderDTO），仅包含跨模块传递所需字段，不含审计字段与计划时间字段。
     *
     * @param id 工单 ID
     * @return 工单 DTO，工单不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    MesWorkOrderDTO getWorkOrderById(Long id);
}
