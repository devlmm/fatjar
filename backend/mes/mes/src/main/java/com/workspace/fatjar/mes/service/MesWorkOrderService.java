package com.workspace.fatjar.mes.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.mes.bo.MesWorkOrderBO;
import com.workspace.fatjar.mes.domain.MesWorkOrderDO;
import com.workspace.fatjar.mes.dto.MesWorkOrderDTO;
import com.workspace.fatjar.mes.query.MesWorkOrderQuery;

/**
 * 工单内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;MesWorkOrderDO&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 mes 模块对外/对内的业务方法，方法签名与 MesWorkOrderApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 MesWorkOrderServiceImpl 同时 implements MesWorkOrderService + MesWorkOrderApi
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 * <p>
 * 与 MesWorkOrderApi 的关系：MesWorkOrderService 是「内部视角」（面向 service 层与 Controller），
 * MesWorkOrderApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface MesWorkOrderService extends IService<MesWorkOrderDO> {

    /**
     * 根据工单 ID 查询工单（返回对外 DTO）
     *
     * @param id 工单 ID
     * @return 工单 DTO，工单不存在返回 null
     */
    MesWorkOrderDTO getWorkOrderById(Long id);

    /**
     * 分页查询工单（返回 BO 分页结果）
     *
     * @param query 分页查询条件（workOrderNo/status + current/size）
     * @return BO 分页结果
     */
    PageResult<MesWorkOrderBO> pageBO(MesWorkOrderQuery query);

    /**
     * 根据工单 ID 查询工单（返回 BO）
     *
     * @param id 工单 ID
     * @return 工单 BO，工单不存在返回 null
     */
    MesWorkOrderBO getBOById(Long id);

    /**
     * 新增工单（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 工单业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(MesWorkOrderBO bo);

    /**
     * 修改工单（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 工单业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(MesWorkOrderBO bo);

    /**
     * 根据 ID 删除工单（逻辑删除）
     *
     * @param id 工单 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
