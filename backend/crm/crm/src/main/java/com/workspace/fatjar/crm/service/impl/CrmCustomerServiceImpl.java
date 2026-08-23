package com.workspace.fatjar.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.crm.api.CrmCustomerApi;
import com.workspace.fatjar.crm.entity.CrmCustomer;
import com.workspace.fatjar.crm.mapper.CrmCustomerMapper;
import com.workspace.fatjar.crm.service.CrmCustomerService;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 客户 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;CrmCustomerMapper, CrmCustomer&gt;，自动拥有
 *      baseMapper 与 IService 全部基础 CRUD 方法
 *   2. 同时 implements CrmCustomerService + CrmCustomerApi，一个实现满足
 *      「内部」与「门面」双契约
 *   3. 写操作（save / updateById / removeById）统一标注 @Transactional(rollbackFor = Exception.class)，
 *      覆盖所有运行时异常回滚；读操作（page / getById）不开启事务
 *   4. 门面方法 getCustomerName 返回字符串，不暴露 Entity，
 *      且客户不存在或被逻辑删除时返回 null，调用方自行处理
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class CrmCustomerServiceImpl extends ServiceImpl<CrmCustomerMapper, CrmCustomer>
        implements CrmCustomerService, CrmCustomerApi {

    /**
     * 新增客户（事务覆盖单条 insert）
     *
     * @param entity 客户实体
     * @return true 表示新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(CrmCustomer entity) {
        return super.save(entity);
    }

    /**
     * 根据 ID 修改客户（事务覆盖单条 update）
     *
     * @param entity 客户实体（id 不能为空）
     * @return true 表示修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(CrmCustomer entity) {
        return super.updateById(entity);
    }

    /**
     * 根据 ID 删除客户（事务覆盖逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 客户主键 ID
     * @return true 表示删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    /**
     * 客户名称查询（跨模块门面方法）
     * <p>
     * 客户不存在或被逻辑删除时返回 null。
     *
     * @param customerId 客户 ID
     * @return 客户名称，客户不存在返回 null
     */
    @Override
    public String getCustomerName(Long customerId) {
        if (customerId == null) {
            return null;
        }
        CrmCustomer customer = getById(customerId);
        return customer == null ? null : customer.getCustomerName();
    }
}
