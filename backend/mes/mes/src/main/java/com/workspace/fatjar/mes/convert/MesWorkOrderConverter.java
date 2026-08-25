package com.workspace.fatjar.mes.convert;

import com.workspace.fatjar.mes.bo.MesWorkOrderBO;
import com.workspace.fatjar.mes.domain.MesWorkOrderDO;
import com.workspace.fatjar.mes.dto.MesWorkOrderDTO;
import com.workspace.fatjar.mes.vo.MesWorkOrderVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 工单对象转换器（MapStruct，Spring Bean）
 * <p>
 * 承载 BO/DO/VO/DTO 之间的转换：
 *   - toBO(DO)：DO 转 BO（Service 查询后产出 BO）
 *   - toDO(BO)：BO 转 DO（持久化前，忽略审计字段 createBy/updateBy/deleted，由 MetaObjectHandler 填充）
 *   - toVO(BO)：BO 转 VO（Controller 返回前端，单向不可逆）
 *   - toVOList(List&lt;BO&gt;)：BO 列表转 VO 列表（分页结果转换）
 *   - toDTO(BO)：BO 转 对外 DTO（跨模块门面，剔除计划时间 plannedStart/plannedEnd 与审计时间）
 * <p>
 * 说明：{@link MesWorkOrderDTO} 继承自仅含 id 的 BaseDTO，无 createTime/updateTime 字段，
 * 且 DTO 不含计划时间字段，MapStruct 自动不映射源 BO 的这些字段，故 toDTO 无需 @Mapping(ignore=true)。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface MesWorkOrderConverter {

    /**
     * DO 转 BO
     *
     * @param doEntity 工单 DO
     * @return 工单 BO
     */
    MesWorkOrderBO toBO(MesWorkOrderDO doEntity);

    /**
     * BO 转 DO（持久化前转换；审计字段 createBy/updateBy/deleted 由 MetaObjectHandler 自动填充，此处忽略）
     *
     * @param bo 工单 BO
     * @return 工单 DO
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    MesWorkOrderDO toDO(MesWorkOrderBO bo);

    /**
     * BO 转 VO（Controller 返回前端）
     *
     * @param bo 工单 BO
     * @return 工单 VO
     */
    MesWorkOrderVO toVO(MesWorkOrderBO bo);

    /**
     * BO 列表转 VO 列表（分页结果转换）
     *
     * @param boList 工单 BO 列表
     * @return 工单 VO 列表
     */
    List<MesWorkOrderVO> toVOList(List<MesWorkOrderBO> boList);

    /**
     * BO 转 对外 DTO（跨模块门面，剔除计划时间字段与审计时间字段；DTO 仅含 id + 对外业务字段）
     *
     * @param bo 工单 BO
     * @return 工单 DTO
     */
    MesWorkOrderDTO toDTO(MesWorkOrderBO bo);
}
