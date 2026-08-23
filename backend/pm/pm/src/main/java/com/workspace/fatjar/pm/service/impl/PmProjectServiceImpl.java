package com.workspace.fatjar.pm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.hrm.api.HrmEmployeeApi;
import com.workspace.fatjar.pm.api.PmProjectApi;
import com.workspace.fatjar.pm.entity.PmProject;
import com.workspace.fatjar.pm.mapper.PmProjectMapper;
import com.workspace.fatjar.pm.service.PmProjectService;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 项目 Service 实现（跨模块协作示例）
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;PmProjectMapper, PmProject&gt;，自动拥有
 *      baseMapper 与 IService 全部基础 CRUD 方法
 *   2. 同时 implements PmProjectService + PmProjectApi，一个实现满足
 *      「内部」与「门面」双契约
 *   3. 写操作（save / updateById / removeById）统一标注 @Transactional(rollbackFor = Exception.class)，
 *      覆盖所有运行时异常回滚；读操作（page / getById）不开启事务
 *   4. 跨模块协作：注入 HrmEmployeeApi（HRM 门面契约），
 *      - save 时校验项目经理存在性：managerId 非空则反查员工姓名，为空抛
 *        BizException(DATA_NOT_FOUND, "项目经理不存在")
 *      - getProjectManagerName 时反查项目经理姓名
 *   5. @Transactional 同时覆盖「跨模块校验」与「本地落库」，保证校验失败时已落库数据一并回滚
 * <p>
 * 跨模块依赖说明：PM 仅依赖 fatjar-hrm-api 契约（HrmEmployeeApi），运行期由
 *   fatjar-hrm 模块提供实现 Bean，从根源规避 Maven 循环依赖。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class PmProjectServiceImpl extends ServiceImpl<PmProjectMapper, PmProject>
        implements PmProjectService, PmProjectApi {

    /** HRM 员工门面契约（跨模块调用：项目经理存在性校验与姓名反查） */
    @Autowired
    private HrmEmployeeApi hrmEmployeeApi;

    /**
     * 新增项目（事务覆盖跨模块校验 + 本地落库）
     * <p>
     * 业务流程：
     *   1. 若项目经理 ID（managerId）非空，调用 HrmEmployeeApi.getEmployeeName 反查员工姓名：
     *      - 返回 null 表示员工不存在或已离职/逻辑删除，抛 BizException(DATA_NOT_FOUND, "项目经理不存在")
     *      - 返回非空表示校验通过，继续落库
     *   2. 调用 super.save 落库
     * <p>
     * 事务边界：@Transactional 同时覆盖第 1 步校验与第 2 步落库，
     *   保证校验抛异常时不会产生半成品数据。
     *
     * @param entity 项目实体
     * @return true 表示新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(PmProject entity) {
        // 项目经理存在性校验（跨模块调用 HRM 门面）
        if (entity.getManagerId() != null) {
            String managerName = hrmEmployeeApi.getEmployeeName(entity.getManagerId());
            if (managerName == null) {
                throw new BizException(ErrorCode.DATA_NOT_FOUND, "项目经理不存在");
            }
        }
        return super.save(entity);
    }

    /**
     * 根据 ID 修改项目（事务覆盖单条 update）
     *
     * @param entity 项目实体（id 不能为空）
     * @return true 表示修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(PmProject entity) {
        return super.updateById(entity);
    }

    /**
     * 根据 ID 删除项目（事务覆盖逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 项目主键 ID
     * @return true 表示删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    /**
     * 项目经理姓名查询（跨模块门面方法）
     * <p>
     * 业务流程：
     *   1. 根据 projectId 查询项目实体
     *   2. 项目不存在或 managerId 为空时返回 null
     *   3. 通过 HrmEmployeeApi.getEmployeeName 反查员工姓名并返回
     *
     * @param projectId 项目 ID
     * @return 项目经理姓名，项目不存在或未指定经理或员工已删除时返回 null
     */
    @Override
    public String getProjectManagerName(Long projectId) {
        if (projectId == null) {
            return null;
        }
        PmProject project = getById(projectId);
        if (project == null || project.getManagerId() == null) {
            return null;
        }
        return hrmEmployeeApi.getEmployeeName(project.getManagerId());
    }
}
