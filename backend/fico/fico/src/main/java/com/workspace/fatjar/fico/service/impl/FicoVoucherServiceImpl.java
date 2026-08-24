package com.workspace.fatjar.fico.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.fico.api.FicoVoucherApi;
import com.workspace.fatjar.fico.bo.FicoVoucherBO;
import com.workspace.fatjar.fico.convert.FicoVoucherConverter;
import com.workspace.fatjar.fico.domain.FicoVoucherDO;
import com.workspace.fatjar.fico.dto.FicoVoucherDTO;
import com.workspace.fatjar.fico.mapper.FicoVoucherMapper;
import com.workspace.fatjar.fico.query.FicoVoucherQuery;
import com.workspace.fatjar.fico.service.FicoVoucherService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 会计凭证 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;FicoVoucherMapper, FicoVoucherDO&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements FicoVoucherService + FicoVoucherApi，一个实现满足「内部」与「门面」双契约
 *   3. Controller 层调用 BO 系列方法（pageBO/getBOById/saveBO/updateBO/removeBOById），
 *      BO 与 DO 通过 {@link FicoVoucherConverter}（MapStruct）双向转换
 *   4. checkBudget 为简化 demo：直接返回 true，表示预算充足，便于跨模块调用方（如 SCM 采购）联调
 *   5. getVoucherById 查询 DO 并通过 converter 转换为对外 DTO（仅包含跨模块所需字段，剔除审计字段）
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * checkBudget 为简化校验（不写库）、getVoucherById 为只读查询，均无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FicoVoucherServiceImpl extends ServiceImpl<FicoVoucherMapper, FicoVoucherDO>
        implements FicoVoucherService, FicoVoucherApi {

    /** MapStruct 转换器（Spring Bean，构造器注入） */
    private final FicoVoucherConverter converter;

    /**
     * 预算校验（简化 demo）
     * <p>
     * 当前为简化实现，直接返回 true，表示预算充足。
     * 实际生产中应：根据部门 ID 查询预算主数据，减去已占用额度，判断剩余是否 >= 申请金额。
     *
     * @param deptId 部门 ID
     * @param amount 申请金额
     * @return true 表示预算充足
     */
    @Override
    public boolean checkBudget(Long deptId, BigDecimal amount) {
        log.info("预算校验（简化 demo 直接放行）：deptId={}, amount={}", deptId, amount);
        return true;
    }

    /**
     * 根据凭证 ID 查询会计凭证（返回对外 DTO）
     * <p>
     * 查询 DO 并通过 {@link FicoVoucherConverter} 转换为 DTO，剔除审计字段。
     *
     * @param id 凭证 ID
     * @return 凭证 DTO，凭证不存在返回 null
     */
    @Override
    public FicoVoucherDTO getVoucherById(Long id) {
        if (id == null) {
            return null;
        }
        FicoVoucherDO doEntity = getById(id);
        if (doEntity == null) {
            return null;
        }
        return converter.toDTO(converter.toBO(doEntity));
    }

    /**
     * 分页查询会计凭证（返回 BO 分页结果）
     * <p>
     * 支持凭证编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询条件（voucherNo/status + current/size）
     * @return BO 分页结果
     */
    @Override
    public PageResult<FicoVoucherBO> pageBO(FicoVoucherQuery query) {
        Page<FicoVoucherDO> page = new Page<>(query.getCurrent(), query.getSize());
        LambdaQueryWrapper<FicoVoucherDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getVoucherNo()),
                FicoVoucherDO::getVoucherNo, query.getVoucherNo());
        wrapper.eq(query.getStatus() != null, FicoVoucherDO::getStatus, query.getStatus());
        wrapper.orderByDesc(FicoVoucherDO::getCreateTime);
        Page<FicoVoucherDO> result = page(page, wrapper);
        // DO 列表转 BO 列表（先转 BO 再转分页结果）
        return PageResult.of(result, converter::toBO);
    }

    /**
     * 根据凭证 ID 查询会计凭证（返回 BO）
     *
     * @param id 凭证 ID
     * @return 凭证 BO，凭证不存在返回 null
     */
    @Override
    public FicoVoucherBO getBOById(Long id) {
        if (id == null) {
            return null;
        }
        FicoVoucherDO doEntity = getById(id);
        return doEntity == null ? null : converter.toBO(doEntity);
    }

    /**
     * 新增会计凭证（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 凭证业务对象
     * @return true 表示保存成功
     */
    @Override
    public boolean saveBO(FicoVoucherBO bo) {
        FicoVoucherDO doEntity = converter.toDO(bo);
        return save(doEntity);
    }

    /**
     * 修改会计凭证（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 凭证业务对象
     * @return true 表示更新成功
     */
    @Override
    public boolean updateBO(FicoVoucherBO bo) {
        FicoVoucherDO doEntity = converter.toDO(bo);
        return updateById(doEntity);
    }

    /**
     * 根据 ID 删除会计凭证（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 凭证 ID
     * @return true 表示删除成功
     */
    @Override
    public boolean removeBOById(Long id) {
        return removeById(id);
    }
}
