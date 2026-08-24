package com.workspace.fatjar.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.fico.api.FicoVoucherApi;
import com.workspace.fatjar.scm.api.ScmPurchaseOrderApi;
import com.workspace.fatjar.scm.bo.ScmPurchaseOrderBO;
import com.workspace.fatjar.scm.convert.ScmPurchaseOrderConverter;
import com.workspace.fatjar.scm.domain.ScmPurchaseOrderDO;
import com.workspace.fatjar.scm.mapper.ScmPurchaseOrderMapper;
import com.workspace.fatjar.scm.query.ScmPurchaseOrderQuery;
import com.workspace.fatjar.scm.resultcode.ScmResultCode;
import com.workspace.fatjar.scm.service.ScmPurchaseOrderService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 采购订单 Service 实现（跨模块调用 demo）
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;ScmPurchaseOrderMapper, ScmPurchaseOrderDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements ScmPurchaseOrderService + ScmPurchaseOrderApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link ScmPurchaseOrderConverter}（MapStruct）双向转换
 *   4. 跨模块编排：saveBO 方法在持久化前调用 FicoVoucherApi.checkBudget 做预算校验，
 *      预算不足抛 ScmBizException(OPERATION_FAILED, "预算不足")
 *   5. @Transactional 同时覆盖「预算校验 + 订单保存」，保证二者原子性
 * <p>
 * 依赖注入：
 *   - ScmPurchaseOrderConverter converter：MapStruct 转换器（Spring Bean，构造器注入）
 *   - FicoVoucherApi ficoVoucherApi：跨模块门面契约（依赖 fatjar-fico-api 契约，由 Spring 注入 FicoVoucherServiceImpl Bean）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScmPurchaseOrderServiceImpl extends ServiceImpl<ScmPurchaseOrderMapper, ScmPurchaseOrderDO>
        implements ScmPurchaseOrderService, ScmPurchaseOrderApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final ScmPurchaseOrderConverter converter;

    /** 跨模块门面：FICO 预算校验能力（依赖 fatjar-fico-api 契约，由 Spring 注入 FicoVoucherServiceImpl Bean） */
    private final FicoVoucherApi ficoVoucherApi;

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
        ScmPurchaseOrderDO doEntity = getById(orderId);
        if (doEntity == null) {
            return null;
        }
        return doEntity.getTotalAmount();
    }

    /**
     * 分页查询采购订单（返回 BO 分页结果）
     * <p>
     * 支持订单编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（orderNo/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<ScmPurchaseOrderBO> pageBO(ScmPurchaseOrderQuery query) {
        Page<ScmPurchaseOrderDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<ScmPurchaseOrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getOrderNo()),
                ScmPurchaseOrderDO::getOrderNo, query.getOrderNo());
        wrapper.eq(query.getStatus() != null, ScmPurchaseOrderDO::getStatus, query.getStatus());
        wrapper.orderByDesc(ScmPurchaseOrderDO::getCreateTime);
        Page<ScmPurchaseOrderDO> result = page(page, wrapper);
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据订单 ID 查询采购订单（返回 BO）
     *
     * @param id 订单 ID
     * @return 订单 BO，订单不存在返回 null
     */
    @Override
    public ScmPurchaseOrderBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        ScmPurchaseOrderDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增采购订单（保存前加入跨模块预算校验）
     * <p>
     * 业务流程：
     *   1. 调用 FicoVoucherApi.checkBudget，传入部门 ID 与订单总金额做预算校验
     *   2. 返回 false 表示预算不足，抛 ScmBizException(OPERATION_FAILED, "预算不足")
     *   3. 预算充足则经 MapStruct 将 BO 转 DO 后持久化订单
     * <p>
     * 事务说明：@Transactional(rollbackFor = Exception.class) 同时覆盖预算校验与订单保存，
     * 任一步骤异常均回滚，保证原子性。
     *
     * @param bo 采购订单业务对象
     * @return true 表示保存成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBO(ScmPurchaseOrderBO bo) {
        // 1. 跨模块预算校验（调用 FICO 门面契约，不感知 fico 实现）
        boolean budgetOk = ficoVoucherApi.checkBudget(bo.getDeptId(), bo.getTotalAmount());
        if (!budgetOk) {
            log.warn("采购订单保存失败：预算不足，deptId={}, totalAmount={}",
                    bo.getDeptId(), bo.getTotalAmount());
            throw new com.workspace.fatjar.scm.exception.ScmBizException(
                    ScmResultCode.OPERATION_FAILED, "预算不足");
        }
        // 2. 预算充足，BO 转 DO 后持久化订单
        ScmPurchaseOrderDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改采购订单（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 采购订单业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(ScmPurchaseOrderBO bo) {
        ScmPurchaseOrderDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除采购订单（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 订单 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
