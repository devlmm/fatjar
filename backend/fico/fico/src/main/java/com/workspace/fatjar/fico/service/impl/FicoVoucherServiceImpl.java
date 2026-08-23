package com.workspace.fatjar.fico.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workspace.fatjar.fico.api.FicoVoucherApi;
import com.workspace.fatjar.fico.dto.FicoVoucherDTO;
import com.workspace.fatjar.fico.entity.FicoVoucher;
import com.workspace.fatjar.fico.mapper.FicoVoucherMapper;
import com.workspace.fatjar.fico.service.FicoVoucherService;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 会计凭证 Service 实现
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus ServiceImpl&lt;FicoVoucherMapper, FicoVoucher&gt;，自动拥有 baseMapper 与 IService 全部方法
 *   2. 同时 implements FicoVoucherService + FicoVoucherApi，一个实现满足「内部」与「门面」双契约
 *   3. checkBudget 为简化 demo：直接返回 true，表示预算充足，便于跨模块调用方（如 SCM 采购）联调
 *   4. getVoucherById 查询凭证实体并转换为对外 DTO（仅包含跨模块所需字段，剔除审计字段）
 * <p>
 * 事务说明：本实现未覆盖默认 CRUD（save/update/removeById），其事务由父类 ServiceImpl 默认实现提供；
 * checkBudget 为简化校验（不写库）、getVoucherById 为只读查询，均无需显式 @Transactional。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@Service
public class FicoVoucherServiceImpl extends ServiceImpl<FicoVoucherMapper, FicoVoucher>
        implements FicoVoucherService, FicoVoucherApi {

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
     *
     * @param id 凭证 ID
     * @return 凭证 DTO，凭证不存在返回 null
     */
    @Override
    public FicoVoucherDTO getVoucherById(Long id) {
        if (id == null) {
            return null;
        }
        FicoVoucher voucher = getById(id);
        if (voucher == null) {
            return null;
        }
        return toDTO(voucher);
    }

    /**
     * FicoVoucher 实体转 FicoVoucherDTO（字段逐一拷贝，剔除审计字段）
     *
     * @param voucher 凭证实体
     * @return 凭证 DTO
     */
    private FicoVoucherDTO toDTO(FicoVoucher voucher) {
        FicoVoucherDTO dto = new FicoVoucherDTO();
        dto.setId(voucher.getId());
        dto.setVoucherNo(voucher.getVoucherNo());
        dto.setTitle(voucher.getTitle());
        dto.setAmount(voucher.getAmount());
        dto.setDirection(voucher.getDirection());
        dto.setPeriod(voucher.getPeriod());
        dto.setStatus(voucher.getStatus());
        return dto;
    }
}
