package com.workspace.fatjar.pm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.pm.bo.PmProjectBO;
import com.workspace.fatjar.pm.domain.PmProjectDO;
import com.workspace.fatjar.pm.query.PmProjectQuery;

/**
 * 项目内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;PmProjectDO&gt;，自动拥有基础 CRUD：
 *        - page(IPage, Wrapper)       分页查询
 *        - getById(Serializable)      根据 ID 查询
 *        - save(T)                    新增
 *        - updateById(T)              根据 ID 修改
 *        - removeById(Serializable)   根据 ID 删除（逻辑删除）
 *   2. 本接口声明 pm 模块对外/对内的业务方法，方法签名与 PmProjectApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 PmProjectServiceImpl 同时 implements PmProjectService + PmProjectApi
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 * <p>
 * 与 PmProjectApi 的关系：PmProjectService 是「内部视角」（面向 service 层与 Controller），
 * PmProjectApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface PmProjectService extends IService<PmProjectDO> {

    /**
     * 项目经理姓名查询（跨模块门面方法）
     * <p>
     * 内部通过 HrmEmployeeApi.getEmployeeName 反查项目经理姓名，便于其他模块展示。
     * 项目不存在、项目经理未指定（managerId 为空）或员工已被逻辑删除时返回 null。
     *
     * @param projectId 项目 ID
     * @return 项目经理姓名，不存在返回 null
     */
    String getProjectManagerName(Long projectId);

    /**
     * 分页查询项目（返回 BO 分页结果）
     *
     * @param query 分页查询条件（projectName/status + current/size）
     * @return BO 分页结果
     */
    PageResult<PmProjectBO> pageBO(PmProjectQuery query);

    /**
     * 根据项目 ID 查询项目（返回 BO）
     *
     * @param id 项目 ID
     * @return 项目 BO，项目不存在返回 null
     */
    PmProjectBO getBOById(Long id);

    /**
     * 新增项目（BO 入参，经 MapStruct 转 DO 后持久化）
     * <p>
     * 内部在事务内调用 HrmEmployeeApi 校验项目经理存在性，
     * 若项目经理不存在则抛 PmBizException(DATA_NOT_FOUND, "项目经理不存在")。
     *
     * @param bo 项目业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(PmProjectBO bo);

    /**
     * 修改项目（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 项目业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(PmProjectBO bo);

    /**
     * 根据 ID 删除项目（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 项目 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
