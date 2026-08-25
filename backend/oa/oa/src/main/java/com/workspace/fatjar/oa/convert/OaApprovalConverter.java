package com.workspace.fatjar.oa.convert;

import com.workspace.fatjar.oa.bo.OaApprovalBO;
import com.workspace.fatjar.oa.domain.OaApprovalDO;
import com.workspace.fatjar.oa.dto.OaApprovalDTO;
import com.workspace.fatjar.oa.vo.OaApprovalVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 审批 MapStruct 转换器（DO/BO/VO/DTO 互转）
 * <p>
 * 设计说明：
 *   1. @Mapper(componentModel="spring")，由 Spring 容器注入，Service/Controller 直接使用
 *   2. toDO 忽略 createBy/updateBy/deleted（由 MetaObjectHandler 自动填充，不从 BO 回填）
 *   3. toDTO 剔除 createTime/updateTime（DTO 不承载审计时间，见 BaseDTO 设计）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface OaApprovalConverter {

    /**
     * DO 转 BO
     *
     * @param doEntity 审批 DO
     * @return 审批 BO
     */
    OaApprovalBO toBO(OaApprovalDO doEntity);

    /**
     * BO 转 DO（忽略审计人/逻辑删除字段，由框架自动填充）
     *
     * @param bo 审批 BO
     * @return 审批 DO
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    OaApprovalDO toDO(OaApprovalBO bo);

    /**
     * BO 转 VO
     *
     * @param bo 审批 BO
     * @return 审批 VO
     */
    OaApprovalVO toVO(OaApprovalBO bo);

    /**
     * BO 列表转 VO 列表
     *
     * @param boList 审批 BO 列表
     * @return 审批 VO 列表
     */
    List<OaApprovalVO> toVOList(List<OaApprovalBO> boList);

    /**
     * BO 转 DTO
     * <p>
     * {@link com.workspace.fatjar.oa.dto.OaApprovalDTO} 继承 {@link com.workspace.fatjar.common.dto.BaseDTO}，
     * 仅含 id 与业务字段，本身不含 createTime/updateTime（BaseDTO 设计上即剔除审计时间）；
     * MapStruct 对源（BO）存在而目标（DTO）不存在的属性自动跳过，故无需显式 @Mapping(ignore=true)。
     * 注意：若在此处声明 target="createTime"/"updateTime" 的 ignore，因 DTO 无对应属性，MapStruct 会报
     * "Unknown property" 编译错误。
     *
     * @param bo 审批 BO
     * @return 审批 DTO
     */
    OaApprovalDTO toDTO(OaApprovalBO bo);
}
