package com.workspace.fatjar.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.crm.entity.CrmCustomer;

/**
 * 客户内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;CrmCustomer&gt;，自动拥有基础 CRUD：
 *        - page(IPage, Wrapper)       分页查询
 *        - getById(Serializable)      根据 ID 查询
 *        - save(T)                    新增
 *        - updateById(T)              根据 ID 修改
 *        - removeById(Serializable)   根据 ID 删除（逻辑删除）
 *   2. 本接口仅承载内部 CRUD 契约（面向 service 层），跨模块门面方法
 *      （getCustomerName）定义在 CrmCustomerApi 中
 *   3. 实现类 CrmCustomerServiceImpl 同时 implements CrmCustomerService + CrmCustomerApi，
 *      一个实现满足「内部」与「门面」双契约
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface CrmCustomerService extends IService<CrmCustomer> {

}
