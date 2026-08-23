package com.workspace.fatjar.mes.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.mes.api.MesWorkOrderApi;
import com.workspace.fatjar.mes.dto.MesWorkOrderDTO;
import com.workspace.fatjar.mes.entity.MesWorkOrder;
import com.workspace.fatjar.mes.mapper.MesWorkOrderMapper;
import com.workspace.fatjar.mes.service.MesWorkOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 工单 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;MesWorkOrderMapper, MesWorkOrder&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements MesWorkOrderService + MesWorkOrderApi，一个实现满足「内部」与「门面」双契约
 *   3. getWorkOrderById 查询工单实体并转换为对外 DTO（仅包含跨模块所需字段，剔除计划时间与审计字段）
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * getWorkOrderById 为只读查询，无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class MesWorkOrderServiceImpl extends ServiceImpl<MesWorkOrderMapper, MesWorkOrder>
        implements MesWorkOrderService, MesWorkOrderApi {

    /**
     * 根据工单 ID 查询工单（返回对外 DTO）
     *
     * @param id 工单 ID
     * @return 工单 DTO，工单不存在返回 null
     */
    @Override
    public MesWorkOrderDTO getWorkOrderById(Long id) {
        if (id == null) {
            return null;
        }
        MesWorkOrder workOrder = getById(id);
        if (workOrder == null) {
            return null;
        }
        return toDTO(workOrder);
    }

    /**
     * MesWorkOrder 实体转 MesWorkOrderDTO（字段逐一拷贝，剔除计划时间与审计字段）
     *
     * @param workOrder 工单实体
     * @return 工单 DTO
     */
    private MesWorkOrderDTO toDTO(MesWorkOrder workOrder) {
        MesWorkOrderDTO dto = new MesWorkOrderDTO();
        dto.setId(workOrder.getId());
        dto.setWorkOrderNo(workOrder.getWorkOrderNo());
        dto.setProductName(workOrder.getProductName());
        dto.setQuantity(workOrder.getQuantity());
        dto.setStatus(workOrder.getStatus());
        return dto;
    }
}
