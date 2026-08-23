package com.workspace.fatjar.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.workspace.fatjar.bi.entity.BiReport;
import org.apache.ibatis.annotations.Mapper;

/**
 * BI 报表 Mapper 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus BaseMapper，自动拥有基础 CRUD 方法（save/getById/update/remove 等）
 *   2. 手写 SQL 全部放在 resources/mapper/BiReportMapper.xml 中，Java 接口只保留方法声明
 *   3. namespace 与本接口全限定名一致，由 MyBatis 自动绑定
 *
 * @author fatjar
 * @since 1.0.0
 */
@Mapper
public interface BiReportMapper extends BaseMapper<BiReport> {
}
