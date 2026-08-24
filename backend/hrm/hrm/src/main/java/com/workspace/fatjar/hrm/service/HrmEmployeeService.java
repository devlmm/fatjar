package com.workspace.fatjar.hrm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.hrm.bo.HrmEmployeeBO;
import com.workspace.fatjar.hrm.domain.HrmEmployeeDO;
import com.workspace.fatjar.hrm.query.HrmEmployeeQuery;

/**
 * 员工内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;HrmEmployeeDO&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口仅承载内部 CRUD 契约（面向 service 层与 Controller），跨模块门面方法
 *      （getEmployeeName / getEmployeeById）定义在 HrmEmployeeApi 中
 *   3. 实现类 HrmEmployeeServiceImpl 同时 implements HrmEmployeeService + HrmEmployeeApi，
 *      一个实现满足「内部」与「门面」双契约
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface HrmEmployeeService extends IService<HrmEmployeeDO> {

    /**
     * 分页查询员工（返回 BO 分页结果）
     *
     * @param query 分页查询条件（name/status + current/size）
     * @return BO 分页结果
     */
    PageResult<HrmEmployeeBO> pageBO(HrmEmployeeQuery query);

    /**
     * 根据员工 ID 查询员工（返回 BO）
     *
     * @param id 员工 ID
     * @return 员工 BO，员工不存在返回 null
     */
    HrmEmployeeBO getBOById(Long id);

    /**
     * 新增员工（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 员工业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(HrmEmployeeBO bo);

    /**
     * 修改员工（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 员工业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(HrmEmployeeBO bo);

    /**
     * 根据 ID 删除员工（逻辑删除）
     *
     * @param id 员工 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
