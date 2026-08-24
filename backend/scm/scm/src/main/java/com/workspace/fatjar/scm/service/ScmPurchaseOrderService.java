package com.workspace.fatjar.scm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.scm.bo.ScmPurchaseOrderBO;
import com.workspace.fatjar.scm.domain.ScmPurchaseOrderDO;
import com.workspace.fatjar.scm.query.ScmPurchaseOrderQuery;
import java.math.BigDecimal;

/**
 * 采购订单内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;ScmPurchaseOrderDO&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 scm 模块对外/对内的业务方法，方法签名与 ScmPurchaseOrderApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 ScmPurchaseOrderServiceImpl 同时 implements ScmPurchaseOrderService + ScmPurchaseOrderApi
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 *   5. saveBO 内部保留跨模块预算校验（调用 FicoVoucherApi.checkBudget）
 * <p>
 * 与 ScmPurchaseOrderApi 的关系：ScmPurchaseOrderService 是「内部视角」（面向 service 层与 Controller），
 * ScmPurchaseOrderApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface ScmPurchaseOrderService extends IService<ScmPurchaseOrderDO> {

    /**
     * 查询采购订单金额
     *
     * @param orderId 采购订单 ID
     * @return 采购订单总金额，订单不存在返回 null
     */
    BigDecimal getPurchaseAmount(Long orderId);

    /**
     * 分页查询采购订单（返回 BO 分页结果）
     *
     * @param query 分页查询条件（orderNo/status + current/size）
     * @return BO 分页结果
     */
    PageResult<ScmPurchaseOrderBO> pageBO(ScmPurchaseOrderQuery query);

    /**
     * 根据订单 ID 查询采购订单（返回 BO）
     *
     * @param id 订单 ID
     * @return 订单 BO，订单不存在返回 null
     */
    ScmPurchaseOrderBO getBOById(Long id);

    /**
     * 新增采购订单（BO 入参，保存前调用 FICO 预算校验）
     * <p>
     * 业务流程：
     *   1. 调用 FicoVoucherApi.checkBudget 校验部门预算
     *   2. 预算不足抛 BizException，预算充足则经 MapStruct 转 DO 后持久化
     *
     * @param bo 采购订单业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(ScmPurchaseOrderBO bo);

    /**
     * 修改采购订单（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 采购订单业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(ScmPurchaseOrderBO bo);

    /**
     * 根据 ID 删除采购订单（逻辑删除）
     *
     * @param id 订单 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
