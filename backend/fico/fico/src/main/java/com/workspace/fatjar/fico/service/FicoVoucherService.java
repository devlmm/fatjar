package com.workspace.fatjar.fico.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.fico.dto.FicoVoucherDTO;
import com.workspace.fatjar.fico.entity.FicoVoucher;
import java.math.BigDecimal;

/**
 * 会计凭证内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;FicoVoucher&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 fico 模块对外/对内的业务方法，方法签名与 FicoVoucherApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 FicoVoucherServiceImpl 同时 implements FicoVoucherService + FicoVoucherApi
 * <p>
 * 与 FicoVoucherApi 的关系：FicoVoucherService 是「内部视角」（面向 service 层与 Controller），
 * FicoVoucherApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface FicoVoucherService extends IService<FicoVoucher> {

    /**
     * 预算校验（校验指定部门是否具备对应金额的预算额度）
     *
     * @param deptId 部门 ID
     * @param amount 申请金额
     * @return true 表示预算充足，false 表示预算不足
     */
    boolean checkBudget(Long deptId, BigDecimal amount);

    /**
     * 根据凭证 ID 查询会计凭证（返回对外 DTO）
     *
     * @param id 凭证 ID
     * @return 凭证 DTO，凭证不存在返回 null
     */
    FicoVoucherDTO getVoucherById(Long id);
}
