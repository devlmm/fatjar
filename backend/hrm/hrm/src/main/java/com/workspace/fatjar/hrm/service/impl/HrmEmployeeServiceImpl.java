package com.workspace.fatjar.hrm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.hrm.api.HrmEmployeeApi;
import com.workspace.fatjar.hrm.bo.HrmEmployeeBO;
import com.workspace.fatjar.hrm.convert.HrmEmployeeConverter;
import com.workspace.fatjar.hrm.domain.HrmEmployeeDO;
import com.workspace.fatjar.hrm.dto.HrmEmployeeDTO;
import com.workspace.fatjar.hrm.mapper.HrmEmployeeMapper;
import com.workspace.fatjar.hrm.query.HrmEmployeeQuery;
import com.workspace.fatjar.hrm.service.HrmEmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 员工 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;HrmEmployeeMapper, HrmEmployeeDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements HrmEmployeeService + HrmEmployeeApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link HrmEmployeeConverter}（MapStruct）双向转换
 *   4. 门面方法 getEmployeeName / getEmployeeById 返回字符串或 DTO，不暴露 DO，
 *      且员工不存在或被逻辑删除时返回 null，调用方自行处理
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * 门面方法为只读查询，无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HrmEmployeeServiceImpl extends ServiceImpl<HrmEmployeeMapper, HrmEmployeeDO>
        implements HrmEmployeeService, HrmEmployeeApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final HrmEmployeeConverter converter;

    /**
     * 员工姓名查询（跨模块门面方法）
     * <p>
     * 供其他模块（如 pm 项目管理）校验员工存在性或获取展示姓名使用。
     * 员工不存在或被逻辑删除时返回 null。
     *
     * @param empId 员工 ID
     * @return 员工姓名，员工不存在返回 null
     */
    @Override
    public String getEmployeeName(Long empId) {
        if (empId == null) {
            return null;
        }
        HrmEmployeeDO doEntity = getById(empId);
        return doEntity == null ? null : doEntity.getName();
    }

    /**
     * 员工详情查询（跨模块门面方法）
     * <p>
     * 查询 DO 并通过 {@link HrmEmployeeConverter} 转换为对外 DTO，剔除审计字段。
     * 员工不存在或被逻辑删除时返回 null。
     *
     * @param empId 员工 ID
     * @return 员工 DTO，员工不存在返回 null
     */
    @Override
    public HrmEmployeeDTO getEmployeeById(Long empId) {
        if (empId == null) {
            return null;
        }
        HrmEmployeeDO doEntity = getById(empId);
        if (doEntity == null) {
            return null;
        }
        return converter.toDTO(converter.toBO(doEntity));
    }

    /**
     * 分页查询员工（返回 BO 分页结果）
     * <p>
     * 支持员工姓名模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（name/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<HrmEmployeeBO> pageBO(HrmEmployeeQuery query) {
        Page<HrmEmployeeDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<HrmEmployeeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getName()),
                HrmEmployeeDO::getName, query.getName());
        wrapper.eq(query.getStatus() != null, HrmEmployeeDO::getStatus, query.getStatus());
        wrapper.orderByDesc(HrmEmployeeDO::getCreateTime);
        Page<HrmEmployeeDO> result = page(page, wrapper);
        // DO 列表转 BO 列表（先转 BO 再转分页结果）
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据员工 ID 查询员工（返回 BO）
     *
     * @param id 员工 ID
     * @return 员工 BO，员工不存在返回 null
     */
    @Override
    public HrmEmployeeBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        HrmEmployeeDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增员工（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 员工业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(HrmEmployeeBO bo) {
        HrmEmployeeDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改员工（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 员工业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(HrmEmployeeBO bo) {
        HrmEmployeeDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除员工（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 员工 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
