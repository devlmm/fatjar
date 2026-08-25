package com.workspace.fatjar.crm.convert;

import com.workspace.fatjar.crm.bo.CrmCustomerBO;
import com.workspace.fatjar.crm.domain.CrmCustomerDO;
import com.workspace.fatjar.crm.dto.CrmCustomerDTO;
import com.workspace.fatjar.crm.vo.CrmCustomerVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 客户对象转换器（MapStruct，Spring Bean）
 * <p>
 * 设计说明：
 *   1. @Mapper(componentModel = "spring") 由 MapStruct 生成 CrmCustomerConverterImpl 并注册为 Spring Bean
 *   2. 承载 DO/BO/VO/DTO 之间的对象转换，规避手写 getter/setter 拷贝
 *   3. toBO（DO -> BO）/ toDO（BO -> DO）：Service 层双向转换，createBy/updateBy/deleted 由
 *      MetaObjectHandler 自动填充，故 toDO 显式 ignore 以免被 BO 空值覆盖
 *   4. toVO / toVOList：Controller 层 BO -> VO 转换，列表方法基于 toVO 自动生成
 *   5. toDTO：门面方法 BO -> DTO，DTO 结构上不含审计字段（BaseDTO 仅 id），未匹配源字段自动忽略
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface CrmCustomerConverter {

    /** DO -> BO */
    CrmCustomerBO toBO(CrmCustomerDO doEntity);

    /**
     * BO -> DO
     * <p>
     * createBy/updateBy/deleted 由 MetaObjectHandler 自动填充，转换时忽略
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    CrmCustomerDO toDO(CrmCustomerBO bo);

    /** BO -> VO */
    CrmCustomerVO toVO(CrmCustomerBO bo);

    /** BO 列表 -> VO 列表（基于 {@link #toVO(CrmCustomerBO)} 自动生成） */
    List<CrmCustomerVO> toVOList(List<CrmCustomerBO> boList);

    /** BO -> DTO（跨模块门面，DTO 仅含对外必要字段） */
    CrmCustomerDTO toDTO(CrmCustomerBO bo);
}
