package com.workspace.fatjar.fico.convert;

import com.workspace.fatjar.fico.bo.FicoVoucherBO;
import com.workspace.fatjar.fico.domain.FicoVoucherDO;
import com.workspace.fatjar.fico.dto.FicoVoucherDTO;
import com.workspace.fatjar.fico.vo.FicoVoucherVO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 会计凭证对象转换器（MapStruct，Spring Bean）
 * <p>
 * 承载 BO/DO/VO/DTO 之间的转换：
 *   - toBO(DO)：DO 转 BO（Service 查询后产出 BO）
 *   - toDO(BO)：BO 转 DO（持久化前，忽略审计字段 createBy/updateBy/deleted，由 MetaObjectHandler 填充）
 *   - toVO(BO)：BO 转 VO（Controller 返回前端，单向不可逆）
 *   - toVOList(List&lt;BO&gt;)：BO 列表转 VO 列表（分页结果转换）
 *   - toDTO(BO)：BO 转 对外 DTO（跨模块门面，剔除 createTime/updateTime，DTO 仅含 id + 业务字段）
 * <p>
 * 说明：{@link FicoVoucherDTO} 继承自仅含 id 的 BaseDTO，无 createTime/updateTime 字段，
 * MapStruct 自动不映射源 BO 的审计时间字段，故 toDTO 无需 @Mapping(ignore=true)。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface FicoVoucherConverter {

    /**
     * DO 转 BO
     *
     * @param doEntity 凭证 DO
     * @return 凭证 BO
     */
    FicoVoucherBO toBO(FicoVoucherDO doEntity);

    /**
     * BO 转 DO（持久化前转换；审计字段 createBy/updateBy/deleted 由 MetaObjectHandler 自动填充，此处忽略）
     *
     * @param bo 凭证 BO
     * @return 凭证 DO
     */
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    FicoVoucherDO toDO(FicoVoucherBO bo);

    /**
     * BO 转 VO（Controller 返回前端）
     *
     * @param bo 凭证 BO
     * @return 凭证 VO
     */
    FicoVoucherVO toVO(FicoVoucherBO bo);

    /**
     * BO 列表转 VO 列表（分页结果转换）
     *
     * @param boList 凭证 BO 列表
     * @return 凭证 VO 列表
     */
    List<FicoVoucherVO> toVOList(List<FicoVoucherBO> boList);

    /**
     * BO 转 对外 DTO（跨模块门面，剔除审计时间字段；DTO 仅含 id + 业务字段）
     *
     * @param bo 凭证 BO
     * @return 凭证 DTO
     */
    FicoVoucherDTO toDTO(FicoVoucherBO bo);
}
