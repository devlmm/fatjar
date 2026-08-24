package com.workspace.fatjar.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workspace.fatjar.auth.domain.SysRoleDO;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 系统角色 Mapper 接口
 * <p>
 * 继承 BaseMapper 自动拥有基础 CRUD；自定义 SQL 放在对应 XML 中。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface SysRoleMapper extends BaseMapper<SysRoleDO> {

    /**
     * 根据用户 ID 查询其所有关联角色（JOIN sys_user_role 中间表）
     *
     * @param userId 用户 ID
     * @return 角色数据对象列表，未关联返回空集合
     */
    List<SysRoleDO> selectRolesByUserId(@Param("userId") Long userId);
}
