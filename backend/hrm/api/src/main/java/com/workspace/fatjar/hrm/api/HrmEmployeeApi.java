package com.workspace.fatjar.hrm.api;

import com.workspace.fatjar.hrm.dto.HrmEmployeeDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 人力资源模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-hrm-api 模块，对外暴露 hrm 模块的员工核心能力
 *   2. 实现类 HrmEmployeeServiceImpl 同时实现本接口与内部 HrmEmployeeService 接口，
 *      一个实现满足「门面」与「内部」双契约
 *   3. 其他业务模块（如 pm 项目管理）通过依赖 fatjar-hrm-api 即可调用本接口，
 *      无需感知 hrm 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 门面接口仅包含「跨模块」必要方法，面向内部 CRUD 的 HTTP 入口由 HrmEmployeeController 承载
 *   5. 返回值统一使用 DTO（HrmEmployeeDTO），不直接暴露 Entity，避免实现细节外泄
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "人力资源模块门面", description = "暴露给其他业务模块调用的人力资源能力契约")
public interface HrmEmployeeApi {

    /**
     * 员工姓名查询（跨模块）
     * <p>
     * 供其他模块（如 pm 项目管理）校验员工存在性或获取展示姓名使用。
     * 员工不存在或被逻辑删除时返回 null，调用方需自行处理。
     *
     * @param empId 员工 ID
     * @return 员工姓名，员工不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    String getEmployeeName(Long empId);

    /**
     * 员工详情查询（跨模块）
     * <p>
     * 返回对外必要的员工基础信息（不含创建/更新审计字段），用于跨模块展示。
     * 员工不存在或被逻辑删除时返回 null。
     *
     * @param empId 员工 ID
     * @return 员工 DTO，员工不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    HrmEmployeeDTO getEmployeeById(Long empId);
}
