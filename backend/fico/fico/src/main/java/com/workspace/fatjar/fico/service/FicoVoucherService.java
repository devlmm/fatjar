package com.workspace.fatjar.fico.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.fico.bo.FicoVoucherBO;
import com.workspace.fatjar.fico.domain.FicoVoucherDO;
import com.workspace.fatjar.fico.dto.FicoVoucherDTO;
import com.workspace.fatjar.fico.query.FicoVoucherQuery;
import java.math.BigDecimal;

/**
 * 会计凭证内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;FicoVoucherDO&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 fico 模块对外/对内的业务方法，方法签名与 FicoVoucherApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 FicoVoucherServiceImpl 同时 implements FicoVoucherService + FicoVoucherApi
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 * <p>
 * 与 FicoVoucherApi 的关系：FicoVoucherService 是「内部视角」（面向 service 层与 Controller），
 * FicoVoucherApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface FicoVoucherService extends IService<FicoVoucherDO> {

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

    /**
     * 分页查询会计凭证（返回 BO 分页结果）
     *
     * @param query 分页查询条件（voucherNo/status + current/size）
     * @return BO 分页结果
     */
    PageResult<FicoVoucherBO> pageBO(FicoVoucherQuery query);

    /**
     * 根据凭证 ID 查询会计凭证（返回 BO）
     *
     * @param id 凭证 ID
     * @return 凭证 BO，凭证不存在返回 null
     */
    FicoVoucherBO getBOById(Long id);

    /**
     * 新增会计凭证（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 凭证业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(FicoVoucherBO bo);

    /**
     * 修改会计凭证（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 凭证业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(FicoVoucherBO bo);

    /**
     * 根据 ID 删除会计凭证（逻辑删除）
     *
     * @param id 凭证 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
