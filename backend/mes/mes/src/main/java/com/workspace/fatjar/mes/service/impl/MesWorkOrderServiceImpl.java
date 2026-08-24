package com.workspace.fatjar.mes.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.mes.api.MesWorkOrderApi;
import com.workspace.fatjar.mes.bo.MesWorkOrderBO;
import com.workspace.fatjar.mes.convert.MesWorkOrderConverter;
import com.workspace.fatjar.mes.domain.MesWorkOrderDO;
import com.workspace.fatjar.mes.dto.MesWorkOrderDTO;
import com.workspace.fatjar.mes.mapper.MesWorkOrderMapper;
import com.workspace.fatjar.mes.query.MesWorkOrderQuery;
import com.workspace.fatjar.mes.service.MesWorkOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 工单 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;MesWorkOrderMapper, MesWorkOrderDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements MesWorkOrderService + MesWorkOrderApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link MesWorkOrderConverter}（MapStruct）双向转换
 *   4. getWorkOrderById 查询 DO 并通过 converter 转换为对外 DTO（仅包含跨模块所需字段，剔除计划时间与审计字段）
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * getWorkOrderById 为只读查询，无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MesWorkOrderServiceImpl extends ServiceImpl<MesWorkOrderMapper, MesWorkOrderDO>
        implements MesWorkOrderService, MesWorkOrderApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final MesWorkOrderConverter converter;

    /**
     * 根据工单 ID 查询工单（返回对外 DTO）
     * <p>
     * 查询 DO 并通过 {@link MesWorkOrderConverter} 转换为 DTO，剔除计划时间与审计字段。
     *
     * @param id 工单 ID
     * @return 工单 DTO，工单不存在返回 null
     */
    @Override
    public MesWorkOrderDTO getWorkOrderById(Long id) {
        if (id == null) {
            return null;
        }
        MesWorkOrderDO doEntity = getById(id);
        if (doEntity == null) {
            return null;
        }
        return converter.toDTO(converter.toBO(doEntity));
    }

    /**
     * 分页查询工单（返回 BO 分页结果）
     * <p>
     * 支持工单编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（workOrderNo/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<MesWorkOrderBO> pageBO(MesWorkOrderQuery query) {
        Page<MesWorkOrderDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<MesWorkOrderDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getWorkOrderNo()),
                MesWorkOrderDO::getWorkOrderNo, query.getWorkOrderNo());
        wrapper.eq(query.getStatus() != null, MesWorkOrderDO::getStatus, query.getStatus());
        wrapper.orderByDesc(MesWorkOrderDO::getCreateTime);
        Page<MesWorkOrderDO> result = page(page, wrapper);
        // DO 列表转 BO 列表（先转 BO 再转分页结果）
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据工单 ID 查询工单（返回 BO）
     *
     * @param id 工单 ID
     * @return 工单 BO，工单不存在返回 null
     */
    @Override
    public MesWorkOrderBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        MesWorkOrderDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增工单（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 工单业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(MesWorkOrderBO bo) {
        MesWorkOrderDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改工单（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 工单业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(MesWorkOrderBO bo) {
        MesWorkOrderDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除工单（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 工单 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
