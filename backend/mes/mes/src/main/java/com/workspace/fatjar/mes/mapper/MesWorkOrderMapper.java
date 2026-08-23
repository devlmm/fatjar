package com.workspace.fatjar.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workspace.fatjar.mes.entity.MesWorkOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单 Mapper 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus BaseMapper，自动拥有基础 CRUD 方法（save/getById/update/remove 等）
 *   2. 手写 SQL 全部放在 resources 下对应 XML 文件中，Java 接口只保留方法声明 + @Param
 *   3. namespace 与本接口全限定名一致，由 MyBatis 自动绑定
 *   4. 本模块无自定义查询，BaseMapper 提供的 CRUD 已覆盖全部场景，故接口内无方法声明
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper
public interface MesWorkOrderMapper extends BaseMapper<MesWorkOrder> {
}
