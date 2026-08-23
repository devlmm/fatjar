package com.workspace.fatjar.pm.api;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 项目管理模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-pm-api 模块，对外暴露 pm 模块的项目核心能力
 *   2. 实现类 PmProjectServiceImpl 同时实现本接口与内部 PmProjectService 接口，
 *      一个实现满足「门面」与「内部」双契约
 *   3. 其他业务模块通过依赖 fatjar-pm-api 即可调用本接口，
 *      无需感知 pm 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 门面接口仅包含「跨模块」必要方法，面向内部 CRUD 的 HTTP 入口由 PmProjectController 承载
 *   5. 返回值统一使用 DTO 或基础类型，不直接暴露 Entity，避免实现细节外泄
 * <p>
 * 跨模块依赖：PmProjectServiceImpl 反向依赖 fatjar-hrm-api（HrmEmployeeApi），
 *   用于项目经理存在性校验，体现「impl 仅依赖对方 api 契约」的跨模块协作模式。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "项目管理模块门面", description = "暴露给其他业务模块调用的项目能力契约")
public interface PmProjectApi {

    /**
     * 项目经理姓名查询（跨模块）
     * <p>
     * 内部通过 HrmEmployeeApi.getEmployeeName 反查项目经理姓名，便于其他模块展示。
     * 项目不存在、项目经理未指定（managerId 为空）或员工已被逻辑删除时返回 null。
     *
     * @param projectId 项目 ID
     * @return 项目经理姓名，不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    String getProjectManagerName(Long projectId);
}
