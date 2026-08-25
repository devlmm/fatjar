package com.workspace.fatjar.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.auth.bo.SysRoleBO;
import com.workspace.fatjar.auth.convert.SysRoleConverter;
import com.workspace.fatjar.auth.domain.SysRoleDO;
import com.workspace.fatjar.auth.mapper.SysRoleMapper;
import com.workspace.fatjar.auth.query.SysRoleQuery;
import com.workspace.fatjar.auth.service.SysRoleService;
import com.workspace.fatjar.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统角色 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;SysRoleMapper, SysRoleDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link SysRoleConverter}（MapStruct）双向转换
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * 查询方法为只读，无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleDO>
        implements SysRoleService {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final SysRoleConverter converter;

    /**
     * 分页查询角色（返回 BO 分页结果）
     * <p>
     * 支持角色名称/角色编码模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（roleName/roleCode/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<SysRoleBO> pageBO(SysRoleQuery query) {
        Page<SysRoleDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<SysRoleDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getRoleName()),
                SysRoleDO::getRoleName, query.getRoleName());
        wrapper.like(StringUtils.hasText(query.getRoleCode()),
                SysRoleDO::getRoleCode, query.getRoleCode());
        wrapper.eq(query.getStatus() != null, SysRoleDO::getStatus, query.getStatus());
        wrapper.orderByDesc(SysRoleDO::getCreateTime);
        Page<SysRoleDO> result = page(page, wrapper);
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据角色 ID 查询角色（返回 BO）
     *
     * @param id 角色 ID
     * @return 角色 BO，角色不存在返回 null
     */
    @Override
    public SysRoleBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        SysRoleDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增角色（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 角色业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(SysRoleBO bo) {
        SysRoleDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改角色（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 角色业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(SysRoleBO bo) {
        SysRoleDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除角色（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 角色 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
