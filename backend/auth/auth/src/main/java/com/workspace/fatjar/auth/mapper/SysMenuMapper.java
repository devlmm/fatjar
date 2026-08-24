package com.workspace.fatjar.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workspace.fatjar.auth.domain.SysMenuDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 系统菜单 Mapper 接口
 * <p>
 * 继承 BaseMapper 自动拥有基础 CRUD；自定义 SQL 放在对应 XML 中。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface SysMenuMapper extends BaseMapper<SysMenuDO> {

    /**
     * 根据用户 ID 查询其可访问的所有菜单（JOIN sys_role_menu + sys_user_role）
     * <p>
     * 结果包含目录/菜单/按钮三种类型，按 sort 升序排列。
     * 业务层根据该结果递归构建父子树形结构。
     *
     * @param userId 用户 ID
     * @return 菜单数据对象列表，未关联返回空集合
     */
    List<SysMenuDO> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 查询其所有权限标识集合（用于接口/按钮鉴权）
     * <p>
     * 仅查询类型为按钮（type=2）且 permission 字段非空的菜单，结果 distinct 去重。
     *
     * @param userId 用户 ID
     * @return 权限标识字符串列表，未关联返回空集合
     */
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);
}
