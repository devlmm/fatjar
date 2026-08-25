package com.workspace.fatjar.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.auth.bo.SysMenuBO;
import com.workspace.fatjar.auth.domain.SysMenuDO;
import com.workspace.fatjar.auth.dto.MenuDTO;
import com.workspace.fatjar.auth.query.SysMenuQuery;
import com.workspace.fatjar.common.result.PageResult;
import java.util.List;

/**
 * 系统菜单内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;SysMenuDO&gt;，自动拥有基础 CRUD（save/getById/update/remove 等）
 *   2. 声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 *   3. 额外声明 treeBO：查询全量菜单并按 parentId 递归构建父子树，返回 MenuDTO 列表
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface SysMenuService extends IService<SysMenuDO> {

    /**
     * 分页查询菜单（返回 BO 分页结果）
     *
     * @param query 分页查询条件（name/type/status + current/size）
     * @return BO 分页结果
     */
    PageResult<SysMenuBO> pageBO(SysMenuQuery query);

    /**
     * 根据菜单 ID 查询菜单（返回 BO）
     *
     * @param id 菜单 ID
     * @return 菜单 BO，菜单不存在返回 null
     */
    SysMenuBO getBOById(Long id);

    /**
     * 新增菜单（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 菜单业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(SysMenuBO bo);

    /**
     * 修改菜单（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 菜单业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(SysMenuBO bo);

    /**
     * 根据 ID 删除菜单（逻辑删除）
     *
     * @param id 菜单 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);

    /**
     * 获取全量菜单树（不分页，用于后台管理界面）
     * <p>
     * 查询所有未删除菜单，按 parentId 递归构建父子树。
     *
     * @return 菜单树（顶级菜单 parentId=0），按 sort 升序排列
     */
    List<MenuDTO> treeBO();
}
