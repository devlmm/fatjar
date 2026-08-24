package com.workspace.fatjar.oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.oa.bo.OaApprovalBO;
import com.workspace.fatjar.oa.domain.OaApprovalDO;
import com.workspace.fatjar.oa.query.OaApprovalQuery;

/**
 * OA 审批内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;OaApprovalDO&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 oa 模块对外/对内的业务方法，方法签名与 OaApprovalApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 OaApprovalServiceImpl 同时 implements OaApprovalService + OaApprovalApi
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 * <p>
 * 与 OaApprovalApi 的关系：OaApprovalService 是「内部视角」（面向 service 层与 Controller），
 * OaApprovalApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface OaApprovalService extends IService<OaApprovalDO> {

    /**
     * 审批标题查询（跨模块门面方法）
     * <p>
     * 供其他业务模块在无需感知 oa 实现的前提下，根据审批 ID 获取审批标题，
     * 常用于消息推送、待办关联、流程追溯等场景。
     *
     * @param approvalId 审批 ID
     * @return 审批标题；审批不存在时返回 null
     */
    String getApprovalTitle(Long approvalId);

    /**
     * 分页查询审批（返回 BO 分页结果）
     *
     * @param query 分页查询条件（title/status + current/size）
     * @return BO 分页结果
     */
    PageResult<OaApprovalBO> pageBO(OaApprovalQuery query);

    /**
     * 根据审批 ID 查询审批（返回 BO）
     *
     * @param id 审批 ID
     * @return 审批 BO，审批不存在返回 null
     */
    OaApprovalBO getBOById(Long id);

    /**
     * 新增审批（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 审批业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(OaApprovalBO bo);

    /**
     * 修改审批（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 审批业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(OaApprovalBO bo);

    /**
     * 根据 ID 删除审批（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 审批 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
