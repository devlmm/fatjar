package com.workspace.fatjar.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.oa.api.OaApprovalApi;
import com.workspace.fatjar.oa.entity.OaApproval;
import com.workspace.fatjar.oa.mapper.OaApprovalMapper;
import com.workspace.fatjar.oa.ro.OaApprovalPageRO;
import com.workspace.fatjar.oa.service.OaApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OA 审批 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;OaApprovalMapper, OaApproval&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements OaApprovalService + OaApprovalApi，一个实现满足「内部」与「门面」双契约
 *   3. 内部 CRUD：page（分页）/getById/save/update/removeById
 *   4. 门面方法：getApprovalTitle（跨模块调用，仅返回审批标题，避免暴露完整实体）
 *   5. 写操作（save/update/removeById）统一加 @Transactional(rollbackFor=Exception.class)
 * <p>
 * 依赖注入：本类仅依赖继承的 baseMapper，无需额外构造器注入。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class OaApprovalServiceImpl extends ServiceImpl<OaApprovalMapper, OaApproval> implements OaApprovalService, OaApprovalApi {

    /**
     * 分页查询审批
     * <p>
     * 查询条件：
     *   - title：模糊匹配（LIKE）
     *   - status：精确匹配（=）
     * 排序：按 createTime 倒序（最新审批在前）
     *
     * @param ro 分页查询请求
     * @return 分页结果
     */
    @Override
    public PageResult<OaApproval> page(OaApprovalPageRO ro) {
        Page<OaApproval> page = new Page<>(ro.getCurrent(), ro.getSize());
        LambdaQueryWrapper<OaApproval> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ro.getTitle() != null && !ro.getTitle().isEmpty(),
                OaApproval::getTitle, ro.getTitle());
        wrapper.eq(ro.getStatus() != null, OaApproval::getStatus, ro.getStatus());
        wrapper.orderByDesc(OaApproval::getCreateTime);
        Page<OaApproval> result = page(page, wrapper);
        return PageResult.of(result);
    }

    /**
     * 根据 ID 查询审批
     *
     * @param id 审批 ID
     * @return 审批实体，不存在返回 null
     */
    @Override
    public OaApproval getById(Long id) {
        if (id == null) {
            return null;
        }
        return super.getById(id);
    }

    /**
     * 新增审批
     *
     * @param approval 审批实体
     * @return true 表示新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(OaApproval approval) {
        return super.save(approval);
    }

    /**
     * 修改审批
     *
     * @param approval 审批实体
     * @return true 表示修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(OaApproval approval) {
        return updateById(approval);
    }

    /**
     * 根据 ID 删除审批（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 审批 ID
     * @return true 表示删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        if (id == null) {
            return false;
        }
        return super.removeById(id);
    }

    /**
     * 审批标题查询（门面方法，跨模块调用）
     * <p>
     * 供其他业务模块根据审批 ID 获取审批标题，常用于消息推送、待办关联场景。
     *
     * @param approvalId 审批 ID
     * @return 审批标题；审批不存在返回 null
     */
    @Override
    public String getApprovalTitle(Long approvalId) {
        if (approvalId == null) {
            return null;
        }
        OaApproval approval = getById(approvalId);
        return approval == null ? null : approval.getTitle();
    }
}
