package com.workspace.fatjar.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.crm.api.CrmCustomerApi;
import com.workspace.fatjar.crm.bo.CrmCustomerBO;
import com.workspace.fatjar.crm.convert.CrmCustomerConverter;
import com.workspace.fatjar.crm.domain.CrmCustomerDO;
import com.workspace.fatjar.crm.mapper.CrmCustomerMapper;
import com.workspace.fatjar.crm.query.CrmCustomerQuery;
import com.workspace.fatjar.crm.service.CrmCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 客户 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;CrmCustomerMapper, CrmCustomerDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements CrmCustomerService + CrmCustomerApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link CrmCustomerConverter}（MapStruct）双向转换
 *   4. 门面方法 getCustomerName 返回字符串，不暴露 DO，
 *      且客户不存在或被逻辑删除时返回 null，调用方自行处理
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * 门面方法为只读查询，无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrmCustomerServiceImpl extends ServiceImpl<CrmCustomerMapper, CrmCustomerDO>
        implements CrmCustomerService, CrmCustomerApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final CrmCustomerConverter converter;

    /**
     * 客户名称查询（跨模块门面方法）
     * <p>
     * 供其他模块（如销售订单、合同等）校验客户存在性或获取展示名称使用。
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
        CrmCustomerDO doEntity = getById(customerId);
        return doEntity == null ? null : doEntity.getCustomerName();
    }

    /**
     * 分页查询客户（返回 BO 分页结果）
     * <p>
     * 支持客户名称模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（customerName/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<CrmCustomerBO> pageBO(CrmCustomerQuery query) {
        Page<CrmCustomerDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<CrmCustomerDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getCustomerName()),
                CrmCustomerDO::getCustomerName, query.getCustomerName());
        wrapper.eq(query.getStatus() != null, CrmCustomerDO::getStatus, query.getStatus());
        wrapper.orderByDesc(CrmCustomerDO::getCreateTime);
        Page<CrmCustomerDO> result = page(page, wrapper);
        // DO 列表转 BO 列表（先转 BO 再转分页结果）
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据客户 ID 查询客户（返回 BO）
     *
     * @param id 客户 ID
     * @return 客户 BO，客户不存在返回 null
     */
    @Override
    public CrmCustomerBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        CrmCustomerDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增客户（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 客户业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(CrmCustomerBO bo) {
        CrmCustomerDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改客户（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 客户业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(CrmCustomerBO bo) {
        CrmCustomerDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除客户（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 客户 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
