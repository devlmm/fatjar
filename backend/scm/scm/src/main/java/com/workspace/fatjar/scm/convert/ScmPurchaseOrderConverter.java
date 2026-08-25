package com.workspace.fatjar.scm.convert;

import com.workspace.fatjar.scm.bo.ScmPurchaseOrderBO;
import com.workspace.fatjar.scm.domain.ScmPurchaseOrderDO;
import com.workspace.fatjar.scm.dto.ScmPurchaseOrderDTO;
import com.workspace.fatjar.scm.vo.ScmPurchaseOrderVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 采购订单对象转换器（MapStruct，Spring Bean）
 * <p>
 * 承载 BO/DO/VO/DTO 之间的转换：
 *   - toBO(DO)：DO 转 BO（Service 查询后产出 BO）
 *   - toDO(BO)：BO 转 DO（持久化前，忽略审计字段 createBy/updateBy/deleted，由 MetaObjectHandler 填充）
 *   - toVO(BO)：BO 转 VO（Controller 返回前端，单向不可逆）
 *   - toVOList(List&lt;BO&gt;)：BO 列表转 VO 列表（分页结果转换）
 *   - toDTO(BO)：BO 转 对外 DTO（跨模块门面，剔除内部字段 deptId 与审计时间）
 * <p>
 * 说明：{@link ScmPurchaseOrderDTO} 继承自仅含 id 的 BaseDTO，无 createTime/updateTime 字段，
 * 且 DTO 不含内部字段 deptId，MapStruct 自动不映射源 BO 的这些字段，故 toDTO 无需 @Mapping(ignore=true)。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ScmPurchaseOrderConverter {

    /**
     * DO 转 BO
     *
     * @param doEntity 采购订单 DO
     * @return 采购订单 BO
     */
    ScmPurchaseOrderBO toBO(ScmPurchaseOrderDO doEntity);

    /**
     * BO 转 DO（持久化前转换；审计字段 createBy/updateBy/deleted 由 MetaObjectHandler 自动填充，此处忽略）
     *
     * @param bo 采购订单 BO
     * @return 采购订单 DO
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    ScmPurchaseOrderDO toDO(ScmPurchaseOrderBO bo);

    /**
     * BO 转 VO（Controller 返回前端）
     *
     * @param bo 采购订单 BO
     * @return 采购订单 VO
     */
    ScmPurchaseOrderVO toVO(ScmPurchaseOrderBO bo);

    /**
     * BO 列表转 VO 列表（分页结果转换）
     *
     * @param boList 采购订单 BO 列表
     * @return 采购订单 VO 列表
     */
    List<ScmPurchaseOrderVO> toVOList(List<ScmPurchaseOrderBO> boList);

    /**
     * BO 转 对外 DTO（跨模块门面，剔除内部字段 deptId 与审计时间字段；DTO 仅含 id + 对外业务字段）
     *
     * @param bo 采购订单 BO
     * @return 采购订单 DTO
     */
    ScmPurchaseOrderDTO toDTO(ScmPurchaseOrderBO bo);
}
