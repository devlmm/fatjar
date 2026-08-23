package com.workspace.fatjar.mes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.mes.dto.MesWorkOrderDTO;
import com.workspace.fatjar.mes.entity.MesWorkOrder;

/**
 * 工单内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;MesWorkOrder&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 mes 模块对外/对内的业务方法，方法签名与 MesWorkOrderApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 MesWorkOrderServiceImpl 同时 implements MesWorkOrderService + MesWorkOrderApi
 * <p>
 * 与 MesWorkOrderApi 的关系：MesWorkOrderService 是「内部视角」（面向 service 层与 Controller），
 * MesWorkOrderApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface MesWorkOrderService extends IService<MesWorkOrder> {

    /**
     * 根据工单 ID 查询工单（返回对外 DTO）
     *
     * @param id 工单 ID
     * @return 工单 DTO，工单不存在返回 null
     */
    MesWorkOrderDTO getWorkOrderById(Long id);
}
