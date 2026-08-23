package com.workspace.fatjar.fico.api;

import com.workspace.fatjar.fico.dto.FicoVoucherDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;

/**
 * 财务会计模块门面接口（跨模块调用契约）
 * <p>
 * 设计说明：
 *   1. 本接口定义在 fatjar-fico-api 模块，对外暴露 fico 模块的核心能力
 *   2. 实现类 FicoVoucherServiceImpl 同时实现本接口与内部 FicoVoucherService 接口，一个实现满足双契约
 *   3. 其他业务模块（如 scm 采购模块）通过依赖 fatjar-fico-api 即可调用本接口进行预算校验与凭证查询，
 *      无需感知 fico 业务实现细节，从根源规避 Maven 循环依赖
 *   4. 实际 HTTP 入口由 FicoVoucherController 调用，门面接口主要服务于跨模块 RPC 风格调用
 *
 * @author fatjar
 * @since 1.0.0
 */
@Tag(name = "财务会计模块门面", description = "暴露给其他业务模块调用的财务会计能力契约")
public interface FicoVoucherApi {

    /**
     * 预算校验（校验指定部门是否具备对应金额的预算额度）
     * <p>
     * 业务流程（简化 demo）：
     *   1. 根据部门 ID 与申请金额进行预算额度校验
     *   2. 当前为简化实现，直接返回 true，表示预算充足
     *   3. 实际生产中应接入预算主数据与已占用额度，做余额扣减判断
     *
     * @param deptId 部门 ID
     * @param amount 申请金额
     * @return true 表示预算充足，false 表示预算不足
     * @author fatjar
     * @since 1.0.0
     */
    boolean checkBudget(Long deptId, BigDecimal amount);

    /**
     * 根据凭证 ID 查询会计凭证（跨模块调用入口）
     * <p>
     * 返回对外 DTO（FicoVoucherDTO），仅包含跨模块传递所需字段，不含审计字段。
     *
     * @param id 凭证 ID
     * @return 凭证 DTO，凭证不存在返回 null
     * @author fatjar
     * @since 1.0.0
     */
    FicoVoucherDTO getVoucherById(Long id);
}
