package com.workspace.fatjar.crm.controller;

import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.crm.bo.CrmCustomerBO;
import com.workspace.fatjar.crm.convert.CrmCustomerConverter;
import com.workspace.fatjar.crm.query.CrmCustomerQuery;
import com.workspace.fatjar.crm.resultcode.CrmResultCode;
import com.workspace.fatjar.crm.service.CrmCustomerService;
import com.workspace.fatjar.crm.vo.CrmCustomerVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户控制器（CRUD）
 * <p>
 * 路径前缀：/crm/customer
 * 接口列表：
 *   - GET    /crm/customer/page        ：分页查询客户（支持名称模糊、状态精确）
 *   - GET    /crm/customer/{id}        ：根据 ID 查询客户
 *   - POST   /crm/customer             ：新增客户
 *   - PUT    /crm/customer             ：修改客户
 *   - DELETE /crm/customer/{id}        ：根据 ID 删除客户（逻辑删除）
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，门面方法（getCustomerName）
 * 由 CrmCustomerApi 承载，仅供跨模块调用，不在此暴露 HTTP 入口。Controller 通过
 * {@link CrmCustomerConverter} 将 Service 返回的 BO 转换为 VO 返回前端，
 * 分页查询使用 {@link CrmCustomerQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/crm/customer")
@RequiredArgsConstructor
@Tag(name = "客户关系-客户管理", description = "客户 CRUD")
public class CrmCustomerController {

    /** 客户 Service（同时承担 CrmCustomerDO 的 IService 能力） */
    private final CrmCustomerService crmCustomerService;

    /** MapStruct 转换器（BO -> VO） */
    private final CrmCustomerConverter converter;

    /**
     * 分页查询客户
     * <p>
     * 支持客户名称模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询参数（current/size/customerName/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询客户", description = "支持名称模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<CrmCustomerVO>> page(@Valid CrmCustomerQuery query) {
        PageResult<CrmCustomerBO> boPage = crmCustomerService.pageBO(query);
        PageResult<CrmCustomerVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询客户
     *
     * @param id 客户 ID
     * @return 客户信息
     */
    @Operation(summary = "根据 ID 查询客户")
    @GetMapping("/{id}")
    public R<CrmCustomerVO> get(@Parameter(description = "客户 ID") @PathVariable Long id) {
        CrmCustomerBO bo = crmCustomerService.getBOById(id);
        if (bo == null) {
            throw new com.workspace.fatjar.crm.exception.CrmBizException(CrmResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增客户
     *
     * @param bo 客户业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增客户")
    @PostMapping
    public R<Void> save(@Parameter(description = "客户信息") @Valid @RequestBody CrmCustomerBO bo) {
        boolean ok = crmCustomerService.saveBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.crm.exception.CrmBizException(CrmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改客户
     *
     * @param bo 客户业务对象（id 不能为空）
     * @return 操作结果
     */
    @Operation(summary = "修改客户")
    @PutMapping
    public R<Void> update(@Parameter(description = "客户信息") @Valid @RequestBody CrmCustomerBO bo) {
        if (bo.getId() == null) {
            throw new com.workspace.fatjar.crm.exception.CrmBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "客户 ID 不能为空");
        }
        boolean ok = crmCustomerService.updateBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.crm.exception.CrmBizException(CrmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除客户（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 客户 ID
     * @return 操作结果
     */
    @Operation(summary = "删除客户", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "客户 ID") @PathVariable Long id) {
        boolean ok = crmCustomerService.removeBOById(id);
        if (!ok) {
            throw new com.workspace.fatjar.crm.exception.CrmBizException(CrmResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
