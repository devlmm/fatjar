package com.workspace.fatjar.bi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.bi.bo.BiReportBO;
import com.workspace.fatjar.bi.domain.BiReportDO;
import com.workspace.fatjar.bi.query.BiReportQuery;

/**
 * BI 报表内部 Service 接口
 * <p>
 * 设计说明：
 *   1. 继承 MyBatis-Plus IService&lt;BiReportDO&gt;，自动拥有基础 CRUD（page/getById/save/update/removeById）
 *   2. 本接口声明 bi 模块对外/对内的业务方法，方法签名与 BiReportApi 门面接口保持一致，
 *      便于实现类一次实现两个接口（双契约）
 *   3. 实现类 BiReportServiceImpl 同时 implements BiReportService + BiReportApi
 *   4. 额外声明 BO 系列方法：pageBO/getBOById/saveBO/updateBO/removeBOById，
 *      Controller 层调用本系列方法，BO 与 DO 通过 MapStruct 双向转换
 * <p>
 * 与 BiReportApi 的关系：BiReportService 是「内部视角」（面向 service 层与 Controller），
 * BiReportApi 是「外部视角」（面向跨模块调用），二者方法签名一致但语义不同。
 *
 * @author fatjar
 * @since 1.0.0
 */
public interface BiReportService extends IService<BiReportDO> {

    /**
     * 报表名称查询（跨模块门面方法）
     * <p>
     * 供其他业务模块在无需感知 bi 实现的前提下，根据报表 ID 获取报表名称，
     * 常用于关联展示、审批明细、推送通知等场景。
     *
     * @param reportId 报表 ID
     * @return 报表名称；报表不存在时返回 null
     */
    String getReportName(Long reportId);

    /**
     * 分页查询报表（返回 BO 分页结果）
     *
     * @param query 分页查询条件（reportName/status + current/size）
     * @return BO 分页结果
     */
    PageResult<BiReportBO> pageBO(BiReportQuery query);

    /**
     * 根据报表 ID 查询报表（返回 BO）
     *
     * @param id 报表 ID
     * @return 报表 BO，报表不存在返回 null
     */
    BiReportBO getBOById(Long id);

    /**
     * 新增报表（BO 入参，经 MapStruct 转 DO 后持久化）
     *
     * @param bo 报表业务对象
     * @return true 表示保存成功
     */
    boolean saveBO(BiReportBO bo);

    /**
     * 修改报表（BO 入参，经 MapStruct 转 DO 后更新）
     *
     * @param bo 报表业务对象
     * @return true 表示更新成功
     */
    boolean updateBO(BiReportBO bo);

    /**
     * 根据 ID 删除报表（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 报表 ID
     * @return true 表示删除成功
     */
    boolean removeBOById(Long id);
}
