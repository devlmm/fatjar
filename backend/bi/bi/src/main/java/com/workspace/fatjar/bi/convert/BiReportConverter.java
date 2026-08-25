package com.workspace.fatjar.bi.convert;

import com.workspace.fatjar.bi.bo.BiReportBO;
import com.workspace.fatjar.bi.domain.BiReportDO;
import com.workspace.fatjar.bi.dto.BiReportDTO;
import com.workspace.fatjar.bi.vo.BiReportVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 报表 MapStruct 转换器（DO/BO/VO/DTO 互转）
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
public interface BiReportConverter {

    /**
     * DO 转 BO
     *
     * @param doEntity 报表 DO
     * @return 报表 BO
     */
    BiReportBO toBO(BiReportDO doEntity);

    /**
     * BO 转 DO（忽略审计人/逻辑删除字段，由框架自动填充）
     *
     * @param bo 报表 BO
     * @return 报表 DO
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    BiReportDO toDO(BiReportBO bo);

    /**
     * BO 转 VO
     *
     * @param bo 报表 BO
     * @return 报表 VO
     */
    BiReportVO toVO(BiReportBO bo);

    /**
     * BO 列表转 VO 列表
     *
     * @param boList 报表 BO 列表
     * @return 报表 VO 列表
     */
    List<BiReportVO> toVOList(List<BiReportBO> boList);

    /**
     * BO 转 DTO
     * <p>
     * {@link com.workspace.fatjar.bi.dto.BiReportDTO} 继承 {@link com.workspace.fatjar.common.dto.BaseDTO}，
     * 仅含 id 与业务字段，本身不含 createTime/updateTime（BaseDTO 设计上即剔除审计时间）；
     * MapStruct 对源（BO）存在而目标（DTO）不存在的属性自动跳过，故无需显式 @Mapping(ignore=true)。
     * 注意：若在此处声明 target="createTime"/"updateTime" 的 ignore，因 DTO 无对应属性，MapStruct 会报
     * "Unknown property" 编译错误。
     *
     * @param bo 报表 BO
     * @return 报表 DTO
     */
    BiReportDTO toDTO(BiReportBO bo);
}
