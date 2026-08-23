package com.workspace.fatjar.oa.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.oa.entity.OaApproval;
import com.workspace.fatjar.oa.ro.OaApprovalPageRO;

/**
 * OA 审批内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;OaApproval&gt;，自动拥有基础 CRUD（save/getById/update/remove 等）
 *   2. 实现类 OaApprovalServiceImpl 同时 implements OaApprovalService + OaApprovalApi（双契约）
 *   3. 仅声明内部 CRUD 业务方法，门面方法签名在 OaApprovalApi 中定义
 * <p>
 * 与 OaApprovalApi 的关系：OaApprovalService 是「内部视角」（面向 service 层 / controller），
 * OaApprovalApi 是「外部视角」（面向跨模块调用），二者实现类相同但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface OaApprovalService extends IService<OaApproval> {

    /**
     * 分页查询审批
     *
     * @param ro 分页查询请求（含 current/size + title/status 过滤条件）
     * @return 分页结果
     */
    PageResult<OaApproval> page(OaApprovalPageRO ro);

    /**
     * 根据 ID 查询审批
     *
     * @param id 审批 ID
     * @return 审批实体，不存在返回 null
     */
    OaApproval getById(Long id);

    /**
     * 新增审批
     *
     * @param approval 审批实体
     * @return true 表示新增成功
     */
    boolean save(OaApproval approval);

    /**
     * 修改审批
     *
     * @param approval 审批实体
     * @return true 表示修改成功
     */
    boolean update(OaApproval approval);

    /**
     * 根据 ID 删除审批（逻辑删除）
     *
     * @param id 审批 ID
     * @return true 表示删除成功
     */
    boolean removeById(Long id);
}
