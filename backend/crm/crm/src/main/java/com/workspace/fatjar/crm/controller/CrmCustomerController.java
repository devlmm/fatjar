package com.workspace.fatjar.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.crm.entity.CrmCustomer;
import com.workspace.fatjar.crm.ro.CrmCustomerPageRO;
import com.workspace.fatjar.crm.service.CrmCustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 由 CrmCustomerApi 承载，仅供跨模块调用，不在此暴露 HTTP 入口。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/crm/customer")
@Tag(name = "客户关系-客户管理", description = "客户 CRUD")
public class CrmCustomerController {

    /** 客户 Service（内部契约，承载 IService CRUD 能力） */
    private final CrmCustomerService crmCustomerService;

    /**
     * 构造器注入（推荐方式，便于单元测试与最终字段保证）
     *
     * @param crmCustomerService 客户 Service
     */
    @Autowired
    public CrmCustomerController(CrmCustomerService crmCustomerService) {
        this.crmCustomerService = crmCustomerService;
    }

    /**
     * 分页查询客户
     *
     * @param ro 分页 + 过滤参数（current/size/customerName/status）
     * @return 分页结果
     */
    @Operation(summary = "分页查询客户", description = "支持名称模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<CrmCustomer>> page(@Valid CrmCustomerPageRO ro) {
        Page<CrmCustomer> page = new Page<>(ro.getCurrent(), ro.getSize());
        LambdaQueryWrapper<CrmCustomer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ro.getCustomerName() != null && !ro.getCustomerName().isEmpty(),
                CrmCustomer::getCustomerName, ro.getCustomerName());
        wrapper.eq(ro.getStatus() != null, CrmCustomer::getStatus, ro.getStatus());
        wrapper.orderByDesc(CrmCustomer::getCreateTime);
        Page<CrmCustomer> result = crmCustomerService.page(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询客户
     *
     * @param id 客户 ID
     * @return 客户信息
     */
    @Operation(summary = "根据 ID 查询客户")
    @GetMapping("/{id}")
    public R<CrmCustomer> get(@Parameter(description = "客户 ID") @PathVariable Long id) {
        CrmCustomer customer = crmCustomerService.getById(id);
        if (customer == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(customer);
    }

    /**
     * 新增客户
     *
     * @param entity 客户实体
     * @return 操作结果
     */
    @Operation(summary = "新增客户")
    @PostMapping
    public R<Void> save(@Parameter(description = "客户信息") @Valid @RequestBody CrmCustomer entity) {
        boolean ok = crmCustomerService.save(entity);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改客户
     *
     * @param entity 客户实体（id 不能为空）
     * @return 操作结果
     */
    @Operation(summary = "修改客户")
    @PutMapping
    public R<Void> update(@Parameter(description = "客户信息") @Valid @RequestBody CrmCustomer entity) {
        if (entity.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "客户 ID 不能为空");
        }
        boolean ok = crmCustomerService.updateById(entity);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
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
        boolean ok = crmCustomerService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
