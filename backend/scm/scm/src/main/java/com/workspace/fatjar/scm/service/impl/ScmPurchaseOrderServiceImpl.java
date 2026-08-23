package com.workspace.fatjar.scm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.fico.api.FicoVoucherApi;
import com.workspace.fatjar.scm.api.ScmPurchaseOrderApi;
import com.workspace.fatjar.scm.entity.ScmPurchaseOrder;
import com.workspace.fatjar.scm.mapper.ScmPurchaseOrderMapper;
import com.workspace.fatjar.scm.service.ScmPurchaseOrderService;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 采购订单 Service 实现（跨模块调用 demo）
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;ScmPurchaseOrderMapper, ScmPurchaseOrder&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements ScmPurchaseOrderService + ScmPurchaseOrderApi，一个实现满足「内部」与「门面」双契约
 *   3. 跨模块编排：save 方法在持久化前调用 FicoVoucherApi.checkBudget 做预算校验，
 *      预算不足抛 BizException(OPERATION_FAILED, "预算不足")
 *   4. @Transactional 同时覆盖「预算校验 + 订单保存」，保证二者原子性
 * <p>
 * 依赖注入：
 *   - FicoVoucherApi ficoVoucherApi：跨模块门面契约（仅依赖 fatjar-fico-api，不依赖 fatjar-fico 实现）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class ScmPurchaseOrderServiceImpl extends ServiceImpl<ScmPurchaseOrderMapper, ScmPurchaseOrder>
        implements ScmPurchaseOrderService, ScmPurchaseOrderApi {

    /** 跨模块门面：FICO 预算校验能力（依赖 fatjar-fico-api 契约，由 Spring 注入 FicoVoucherServiceImpl Bean） */
    @Autowired
    private FicoVoucherApi ficoVoucherApi;

    /**
     * 查询采购订单金额（跨模块门面方法实现）
     *
     * @param orderId 采购订单 ID
     * @return 采购订单总金额，订单不存在返回 null
     */
    @Override
    public BigDecimal getPurchaseAmount(Long orderId) {
        if (orderId == null) {
            return null;
        }
        ScmPurchaseOrder order = getById(orderId);
        if (order == null) {
            return null;
        }
        return order.getTotalAmount();
    }

    /**
     * 新增采购订单（覆盖默认 save，加入跨模块预算校验）
     * <p>
     * 业务流程：
     *   1. 调用 FicoVoucherApi.checkBudget，传入部门 ID 与订单总金额做预算校验
     *   2. 返回 false 表示预算不足，抛 BizException(OPERATION_FAILED, "预算不足")
     *   3. 预算充足则调用 super.save 持久化订单
     * <p>
     * 事务说明：@Transactional(rollbackFor = Exception.class) 同时覆盖预算校验与订单保存，
     * 任一步骤异常均回滚，保证原子性。
     *
     * @param entity 采购订单实体
     * @return true 表示保存成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(ScmPurchaseOrder entity) {
        // 1. 跨模块预算校验（调用 FICO 门面契约，不感知 fico 实现）
        boolean budgetOk = ficoVoucherApi.checkBudget(entity.getDeptId(), entity.getTotalAmount());
        if (!budgetOk) {
            log.warn("采购订单保存失败：预算不足，deptId={}, totalAmount={}",
                    entity.getDeptId(), entity.getTotalAmount());
            throw new BizException(ErrorCode.OPERATION_FAILED, "预算不足");
        }
        // 2. 预算充足，持久化订单
        return super.save(entity);
    }
}
