package com.workspace.fatjar.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workspace.fatjar.auth.domain.SysUserRoleDO;

/**
 * 用户-角色关联 Mapper 接口
 * <p>
 * 设计说明：仅依赖 BaseMapper 提供的 CRUD（save/removeById/selectList 等），
 * 无需手写 SQL，故无对应 XML 文件。
 * <p>
 * 典型使用：根据 userId 删除/批量插入用户角色绑定关系。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRoleDO> {
}
