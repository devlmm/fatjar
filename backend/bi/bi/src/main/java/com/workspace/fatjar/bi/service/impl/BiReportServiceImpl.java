package com.workspace.fatjar.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.bi.api.BiReportApi;
import com.workspace.fatjar.bi.entity.BiReport;
import com.workspace.fatjar.bi.mapper.BiReportMapper;
import com.workspace.fatjar.bi.ro.BiReportPageRO;
import com.workspace.fatjar.bi.service.BiReportService;
import com.workspace.fatjar.common.result.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * BI 报表 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;BiReportMapper, BiReport&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements BiReportService + BiReportApi，一个实现满足「内部」与「门面」双契约
 *   3. 内部 CRUD：page（分页）/getById/save/update/removeById
 *   4. 门面方法：getReportName（跨模块调用，仅返回报表名称，避免暴露完整实体）
 *   5. 写操作（save/update/removeById）统一加 @Transactional(rollbackFor=Exception.class)
 * <p>
 * 依赖注入：本类仅依赖继承的 baseMapper，无需额外构造器注入。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class BiReportServiceImpl extends ServiceImpl<BiReportMapper, BiReport> implements BiReportService, BiReportApi {

    /**
     * 分页查询报表
     * <p>
     * 查询条件：
     *   - reportName：模糊匹配（LIKE）
     *   - status：精确匹配（=）
     * 排序：按 createTime 倒序（最新报表在前）
     *
     * @param ro 分页查询请求
     * @return 分页结果
     */
    @Override
    public PageResult<BiReport> page(BiReportPageRO ro) {
        Page<BiReport> page = new Page<>(ro.getCurrent(), ro.getSize());
        LambdaQueryWrapper<BiReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ro.getReportName() != null && !ro.getReportName().isEmpty(),
                BiReport::getReportName, ro.getReportName());
        wrapper.eq(ro.getStatus() != null, BiReport::getStatus, ro.getStatus());
        wrapper.orderByDesc(BiReport::getCreateTime);
        Page<BiReport> result = page(page, wrapper);
        return PageResult.of(result);
    }

    /**
     * 根据 ID 查询报表
     *
     * @param id 报表 ID
     * @return 报表实体，不存在返回 null
     */
    @Override
    public BiReport getById(Long id) {
        if (id == null) {
            return null;
        }
        return super.getById(id);
    }

    /**
     * 新增报表
     *
     * @param report 报表实体
     * @return true 表示新增成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(BiReport report) {
        return super.save(report);
    }

    /**
     * 修改报表
     *
     * @param report 报表实体
     * @return true 表示修改成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(BiReport report) {
        return updateById(report);
    }

    /**
     * 根据 ID 删除报表（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 报表 ID
     * @return true 表示删除成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeById(Long id) {
        if (id == null) {
            return false;
        }
        return super.removeById(id);
    }

    /**
     * 报表名称查询（门面方法，跨模块调用）
     * <p>
     * 供其他业务模块根据报表 ID 获取报表名称，常用于关联展示场景。
     *
     * @param reportId 报表 ID
     * @return 报表名称；报表不存在返回 null
     */
    @Override
    public String getReportName(Long reportId) {
        if (reportId == null) {
            return null;
        }
        BiReport report = getById(reportId);
        return report == null ? null : report.getReportName();
    }
}
