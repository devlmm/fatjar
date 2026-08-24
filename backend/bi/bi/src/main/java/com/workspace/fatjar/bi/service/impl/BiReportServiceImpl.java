package com.workspace.fatjar.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.bi.api.BiReportApi;
import com.workspace.fatjar.bi.bo.BiReportBO;
import com.workspace.fatjar.bi.convert.BiReportConverter;
import com.workspace.fatjar.bi.domain.BiReportDO;
import com.workspace.fatjar.bi.mapper.BiReportMapper;
import com.workspace.fatjar.bi.query.BiReportQuery;
import com.workspace.fatjar.bi.service.BiReportService;
import com.workspace.fatjar.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * BI 报表 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;BiReportMapper, BiReportDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements BiReportService + BiReportApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link BiReportConverter}（MapStruct）双向转换
 *   4. 门面方法 getReportName 返回字符串，不暴露 DO，
 *      且报表不存在时返回 null，调用方自行处理
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
public class BiReportServiceImpl extends ServiceImpl<BiReportMapper, BiReportDO>
        implements BiReportService, BiReportApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final BiReportConverter converter;

    /**
     * 报表名称查询（跨模块门面方法）
     * <p>
     * 供其他业务模块在无需感知 bi 实现的前提下，根据报表 ID 获取报表名称，
     * 常用于关联展示、审批明细、推送通知等场景。
     * 报表不存在或被逻辑删除时返回 null。
     *
     * @param reportId 报表 ID
     * @return 报表名称；报表不存在时返回 null
     */
    @Override
    public String getReportName(Long reportId) {
        if (reportId == null) {
            return null;
        }
        BiReportDO doEntity = getById(reportId);
        return doEntity == null ? null : doEntity.getReportName();
    }

    /**
     * 分页查询报表（返回 BO 分页结果）
     * <p>
     * 支持报表名称模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（reportName/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<BiReportBO> pageBO(BiReportQuery query) {
        Page<BiReportDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<BiReportDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getReportName()),
                BiReportDO::getReportName, query.getReportName());
        wrapper.eq(query.getStatus() != null, BiReportDO::getStatus, query.getStatus());
        wrapper.orderByDesc(BiReportDO::getCreateTime);
        Page<BiReportDO> result = page(page, wrapper);
        // DO 列表转 BO 列表（先转 BO 再转分页结果）
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据报表 ID 查询报表（返回 BO）
     *
     * @param id 报表 ID
     * @return 报表 BO，报表不存在返回 null
     */
    @Override
    public BiReportBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        BiReportDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增报表（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 报表业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(BiReportBO bo) {
        BiReportDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改报表（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 报表业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(BiReportBO bo) {
        BiReportDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除报表（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 报表 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
