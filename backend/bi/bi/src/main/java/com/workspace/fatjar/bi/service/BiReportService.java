package com.workspace.fatjar.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.bi.entity.BiReport;
import com.workspace.fatjar.bi.ro.BiReportPageRO;
import com.workspace.fatjar.common.result.PageResult;

/**
 * BI 报表内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;BiReport&gt;，自动拥有基础 CRUD（save/getById/update/remove 等）
 *   2. 实现类 BiReportServiceImpl 同时 implements BiReportService + BiReportApi（双契约）
 *   3. 仅声明内部 CRUD 业务方法，门面方法签名在 BiReportApi 中定义
 * <p>
 * 与 BiReportApi 的关系：BiReportService 是「内部视角」（面向 service 层 / controller），
 * BiReportApi 是「外部视角」（面向跨模块调用），二者实现类相同但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface BiReportService extends IService<BiReport> {

    /**
     * 分页查询报表
     *
     * @param ro 分页查询请求（含 current/size + reportName/status 过滤条件）
     * @return 分页结果
     */
    PageResult<BiReport> page(BiReportPageRO ro);

    /**
     * 根据 ID 查询报表
     *
     * @param id 报表 ID
     * @return 报表实体，不存在返回 null
     */
    BiReport getById(Long id);

    /**
     * 新增报表
     *
     * @param report 报表实体
     * @return true 表示新增成功
     */
    boolean save(BiReport report);

    /**
     * 修改报表
     *
     * @param report 报表实体
     * @return true 表示修改成功
     */
    boolean update(BiReport report);

    /**
     * 根据 ID 删除报表（逻辑删除）
     *
     * @param id 报表 ID
     * @return true 表示删除成功
     */
    boolean removeById(Long id);
}
