package com.workspace.fatjar.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workspace.fatjar.auth.entity.SysUser;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户 Mapper 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus BaseMapper，自动拥有基础 CRUD 方法（save/getById/update/remove 等）
 *   2. 手写 SQL 全部放在 resources 下对应 XML 文件中，Java 接口只保留方法声明 + @Param
 *   3. namespace 与本接口全限定名一致，由 MyBatis 自动绑定
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（登录场景使用）
     *
     * @param username 用户名（登录账号）
     * @return 用户实体，不存在返回 null
     */
    SysUser selectByUsername(@Param("username") String username);
}
