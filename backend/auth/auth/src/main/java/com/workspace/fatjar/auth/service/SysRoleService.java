package com.workspace.fatjar.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.auth.bo.SysRoleBO;
import com.workspace.fatjar.auth.domain.SysRoleDO;
import com.workspace.fatjar.auth.query.SysRoleQuery;
import com.workspace.fatjar.common.result.PageResult;

/**
 * 系统角色内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;SysRoleDO&gt;，自动拥有基础 CRUD（save/getById/update/remove 等）
 *   2. 声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface SysRoleService extends IService<SysRoleDO> {

    /**
     * 分页查询角色（返回 BO 分页结果）
     *
     * @param query 分页查询条件（roleName/roleCode/status + current/size）
     * @return BO 分页结果
     */
    PageResult<SysRoleBO> pageBO(SysRoleQuery query);

    /**
     * 根据角色 ID 查询角色（返回 BO）
     *
     * @param id 角色 ID
     * @return 角色 BO，角色不存在返回 null
     */
    SysRoleBO getBOById(Long id);

    /**
     * 新增角色（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 角色业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(SysRoleBO bo);

    /**
     * 修改角色（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 角色业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(SysRoleBO bo);

    /**
     * 根据 ID 删除角色（逻辑删除）
     *
     * @param id 角色 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
