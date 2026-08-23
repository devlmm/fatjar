package com.workspace.fatjar.scm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.scm.entity.ScmPurchaseOrder;
import java.math.BigDecimal;

/**
 * 采购订单内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;ScmPurchaseOrder&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 scm 模块对外/对内的业务方法，方法签名与 ScmPurchaseOrderApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 ScmPurchaseOrderServiceImpl 同时 implements ScmPurchaseOrderService + ScmPurchaseOrderApi
 *   4. 额外声明 save 方法：因实现类覆盖了默认 save 以加入跨模块预算校验逻辑，故在接口显式声明契约
 * <p>
 * 与 ScmPurchaseOrderApi 的关系：ScmPurchaseOrderService 是「内部视角」（面向 service 层与 Controller），
 * ScmPurchaseOrderApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface ScmPurchaseOrderService extends IService<ScmPurchaseOrder> {

    /**
     * 查询采购订单金额
     *
     * @param orderId 采购订单 ID
     * @return 采购订单总金额，订单不存在返回 null
     */
    BigDecimal getPurchaseAmount(Long orderId);

    /**
     * 新增采购订单（覆盖默认 save，加入跨模块预算校验）
     * <p>
     * 业务流程：
     *   1. 调用 FicoVoucherApi.checkBudget 校验部门预算
     *   2. 预算不足抛 BizException，预算充足则调用父类 save 持久化
     *
     * @param entity 采购订单实体
     * @return true 表示保存成功
     */
    @Override
    boolean save(ScmPurchaseOrder entity);
}
