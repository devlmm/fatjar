package com.workspace.fatjar.oa.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.oa.api.OaApprovalApi;
import com.workspace.fatjar.oa.bo.OaApprovalBO;
import com.workspace.fatjar.oa.convert.OaApprovalConverter;
import com.workspace.fatjar.oa.domain.OaApprovalDO;
import com.workspace.fatjar.oa.mapper.OaApprovalMapper;
import com.workspace.fatjar.oa.query.OaApprovalQuery;
import com.workspace.fatjar.oa.service.OaApprovalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * OA 审批 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;OaApprovalMapper, OaApprovalDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements OaApprovalService + OaApprovalApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link OaApprovalConverter}（MapStruct）双向转换
 *   4. 门面方法 getApprovalTitle 返回字符串，不暴露 DO，
 *      且审批不存在时返回 null，调用方自行处理
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
public class OaApprovalServiceImpl extends ServiceImpl<OaApprovalMapper, OaApprovalDO>
        implements OaApprovalService, OaApprovalApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final OaApprovalConverter converter;

    /**
     * 审批标题查询（跨模块门面方法）
     * <p>
     * 供其他业务模块在无需感知 oa 实现的前提下，根据审批 ID 获取审批标题，
     * 常用于消息推送、待办关联、流程追溯等场景。
     * 审批不存在或被逻辑删除时返回 null。
     *
     * @param approvalId 审批 ID
     * @return 审批标题；审批不存在时返回 null
     */
    @Override
    public String getApprovalTitle(Long approvalId) {
        if (approvalId == null) {
            return null;
        }
        OaApprovalDO doEntity = getById(approvalId);
        return doEntity == null ? null : doEntity.getTitle();
    }

    /**
     * 分页查询审批（返回 BO 分页结果）
     * <p>
     * 支持审批标题模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（title/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<OaApprovalBO> pageBO(OaApprovalQuery query) {
        Page<OaApprovalDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<OaApprovalDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getTitle()),
                OaApprovalDO::getTitle, query.getTitle());
        wrapper.eq(query.getStatus() != null, OaApprovalDO::getStatus, query.getStatus());
        wrapper.orderByDesc(OaApprovalDO::getCreateTime);
        Page<OaApprovalDO> result = page(page, wrapper);
        // DO 列表转 BO 列表（先转 BO 再转分页结果）
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据审批 ID 查询审批（返回 BO）
     *
     * @param id 审批 ID
     * @return 审批 BO，审批不存在返回 null
     */
    @Override
    public OaApprovalBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        OaApprovalDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增审批（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 审批业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(OaApprovalBO bo) {
        OaApprovalDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改审批（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 审批业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(OaApprovalBO bo) {
        OaApprovalDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除审批（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 审批 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
