package com.workspace.fatjar.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.crm.bo.CrmCustomerBO;
import com.workspace.fatjar.crm.domain.CrmCustomerDO;
import com.workspace.fatjar.crm.query.CrmCustomerQuery;

/**
 * 客户内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;CrmCustomerDO&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口仅承载内部 CRUD 契约（面向 service 层与 Controller），跨模块门面方法
 *      （getCustomerName）定义在 CrmCustomerApi 中
 *   3. 实现类 CrmCustomerServiceImpl 同时 implements CrmCustomerService + CrmCustomerApi，
 *      一个实现满足「内部」与「门面」双契约
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface CrmCustomerService extends IService<CrmCustomerDO> {

    /**
     * 分页查询客户（返回 BO 分页结果）
     *
     * @param query 分页查询条件（customerName/status + current/size）
     * @return BO 分页结果
     */
    PageResult<CrmCustomerBO> pageBO(CrmCustomerQuery query);

    /**
     * 根据客户 ID 查询客户（返回 BO）
     *
     * @param id 客户 ID
     * @return 客户 BO，客户不存在返回 null
     */
    CrmCustomerBO getBOById(Long id);

    /**
     * 新增客户（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 客户业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(CrmCustomerBO bo);

    /**
     * 修改客户（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 客户业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(CrmCustomerBO bo);

    /**
     * 根据 ID 删除客户（逻辑删除）
     *
     * @param id 客户 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
