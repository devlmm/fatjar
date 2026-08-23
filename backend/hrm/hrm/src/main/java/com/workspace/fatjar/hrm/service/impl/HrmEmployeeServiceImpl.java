package com.workspace.fatjar.hrm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.hrm.api.HrmEmployeeApi;
import com.workspace.fatjar.hrm.dto.HrmEmployeeDTO;
import com.workspace.fatjar.hrm.entity.HrmEmployee;
import com.workspace.fatjar.hrm.mapper.HrmEmployeeMapper;
import com.workspace.fatjar.hrm.service.HrmEmployeeService;
import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;HrmEmployeeMapper, HrmEmployee&gt;，自动拥有
 *      baseMapper 与 IService 全部基础 CRUD 方法
 *   2. 同时 implements HrmEmployeeService + HrmEmployeeApi，一个实现满足
 *      「内部」与「门面」双契约
 *   3. 写操作（save / updateById / removeById）统一标注 @Transactional(rollbackFor = Exception.class)，
 *      覆盖所有运行时异常回滚；读操作（page / getById）不开启事务
 *   4. 门面方法 getEmployeeName / getEmployeeById 返回 DTO 或字符串，不暴露 Entity，
 *      且员工不存在或被逻辑删除时返回 null，调用方自行处理
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class HrmEmployeeServiceImpl extends ServiceImpl<HrmEmployeeMapper, HrmEmployee>
        implements HrmEmployeeService, HrmEmployeeApi {

    /**
     * 新增员工（事务覆盖单条 insert）
     *
     * @param entity 员工实体
     * @return true 表示新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(HrmEmployee entity) {
        return super.save(entity);
    }

    /**
     * 根据 ID 修改员工（事务覆盖单条 update）
     *
     * @param entity 员工实体（id 不能为空）
     * @return true 表示修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(HrmEmployee entity) {
        return super.updateById(entity);
    }

    /**
     * 根据 ID 删除员工（事务覆盖逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 员工主键 ID
     * @return true 表示删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    /**
     * 员工姓名查询（跨模块门面方法）
     * <p>
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
        HrmEmployee employee = getById(empId);
        return employee == null ? null : employee.getName();
    }

    /**
     * 员工详情查询（跨模块门面方法）
     * <p>
     * 将 Entity 转换为对外 DTO（仅含必要字段），员工不存在或被逻辑删除时返回 null。
     *
     * @param empId 员工 ID
     * @return 员工 DTO，员工不存在返回 null
     */
    @Override
    public HrmEmployeeDTO getEmployeeById(Long empId) {
        if (empId == null) {
            return null;
        }
        HrmEmployee employee = getById(empId);
        if (employee == null) {
            return null;
        }
        HrmEmployeeDTO dto = new HrmEmployeeDTO();
        dto.setId(employee.getId());
        dto.setEmpNo(employee.getEmpNo());
        dto.setName(employee.getName());
        dto.setDeptId(employee.getDeptId());
        dto.setPosition(employee.getPosition());
        dto.setPhone(employee.getPhone());
        dto.setEmail(employee.getEmail());
        dto.setStatus(employee.getStatus());
        return dto;
    }
}
