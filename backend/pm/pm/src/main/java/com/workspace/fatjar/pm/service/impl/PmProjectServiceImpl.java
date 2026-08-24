package com.workspace.fatjar.pm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.hrm.api.HrmEmployeeApi;
import com.workspace.fatjar.pm.api.PmProjectApi;
import com.workspace.fatjar.pm.bo.PmProjectBO;
import com.workspace.fatjar.pm.convert.PmProjectConverter;
import com.workspace.fatjar.pm.domain.PmProjectDO;
import com.workspace.fatjar.pm.exception.PmBizException;
import com.workspace.fatjar.pm.mapper.PmProjectMapper;
import com.workspace.fatjar.pm.query.PmProjectQuery;
import com.workspace.fatjar.pm.resultcode.PmResultCode;
import com.workspace.fatjar.pm.service.PmProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 项目 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;PmProjectMapper, PmProjectDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements PmProjectService + PmProjectApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link PmProjectConverter}（MapStruct）双向转换
 *   4. 门面方法 getProjectManagerName 内部调用 HrmEmployeeApi.getEmployeeName 反查项目经理姓名，
 *      体现「impl 仅依赖对方 api 契约」的跨模块协作模式
 *   5. saveBO 在事务内调用 HrmEmployeeApi 校验项目经理存在性，
 *      若项目经理不存在则抛 PmBizException(DATA_NOT_FOUND, "项目经理不存在")
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PmProjectServiceImpl extends ServiceImpl<PmProjectMapper, PmProjectDO>
        implements PmProjectService, PmProjectApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final PmProjectConverter converter;

    /** HRM 员工门面（跨模块校验项目经理存在性 / 反查姓名，构造器注入） */
    private final HrmEmployeeApi hrmEmployeeApi;

    /**
     * 项目经理姓名查询（跨模块门面方法）
     * <p>
     * 内部通过 HrmEmployeeApi.getEmployeeName 反查项目经理姓名，便于其他模块展示。
     * 项目不存在、项目经理未指定（managerId 为空）或员工已被逻辑删除时返回 null。
     *
     * @param projectId 项目 ID
     * @return 项目经理姓名，不存在返回 null
     */
    @Override
    public String getProjectManagerName(Long projectId) {
        if (projectId == null) {
            return null;
        }
        PmProjectDO doEntity = getById(projectId);
        if (doEntity == null) {
            return null;
        }
        Long managerId = doEntity.getManagerId();
        if (managerId == null) {
            return null;
        }
        return hrmEmployeeApi.getEmployeeName(managerId);
    }

    /**
     * 分页查询项目（返回 BO 分页结果）
     * <p>
     * 支持项目名称模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（projectName/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<PmProjectBO> pageBO(PmProjectQuery query) {
        Page<PmProjectDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<PmProjectDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getProjectName()),
                PmProjectDO::getProjectName, query.getProjectName());
        wrapper.eq(query.getStatus() != null, PmProjectDO::getStatus, query.getStatus());
        wrapper.orderByDesc(PmProjectDO::getCreateTime);
        Page<PmProjectDO> result = page(page, wrapper);
        // DO 列表转 BO 列表（先转 BO 再转分页结果）
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据项目 ID 查询项目（返回 BO）
     *
     * @param id 项目 ID
     * @return 项目 BO，项目不存在返回 null
     */
    @Override
    public PmProjectBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        PmProjectDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增项目（BO 入参，经 MapStruct 转 DO 后持久化）
     * <p>
     * 内部在事务内调用 HrmEmployeeApi 校验项目经理存在性，
     * 若项目经理不存在则抛 PmBizException(DATA_NOT_FOUND, "项目经理不存在")。
     *
     * @param bo 项目业务对象
     * @return true 表示保存成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBO(PmProjectBO bo) {
        // 跨模块校验项目经理存在性（HRM）
        if (bo.getManagerId() != null) {
            String managerName = hrmEmployeeApi.getEmployeeName(bo.getManagerId());
            if (managerName == null) {
                throw new PmBizException(PmResultCode.DATA_NOT_FOUND, "项目经理不存在");
            }
        }
        PmProjectDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改项目（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 项目业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(PmProjectBO bo) {
        PmProjectDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除项目（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 项目 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
