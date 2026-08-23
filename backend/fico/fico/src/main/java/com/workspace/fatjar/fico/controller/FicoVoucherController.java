package com.workspace.fatjar.fico.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.fico.entity.FicoVoucher;
import com.workspace.fatjar.fico.ro.FicoVoucherPageRO;
import com.workspace.fatjar.fico.service.FicoVoucherService;
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
 * 会计凭证控制器（CRUD）
 * <p>
 * 路径前缀：/fico/voucher
 * 接口列表：
 *   - GET    /fico/voucher/page        ：分页查询会计凭证
 *   - GET    /fico/voucher/{id}        ：根据 ID 查询会计凭证
 *   - POST   /fico/voucher             ：新增会计凭证
 *   - PUT    /fico/voucher             ：修改会计凭证
 *   - DELETE /fico/voucher/{id}        ：根据 ID 删除会计凭证（逻辑删除）
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，门面方法（checkBudget/getVoucherById）由 FicoVoucherApi
 * 跨模块调用，不在 Controller 重复暴露。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/fico/voucher")
@Tag(name = "财务会计模块-会计凭证", description = "会计凭证 CRUD")
public class FicoVoucherController {

    /** 会计凭证 Service（同时承担 FicoVoucher 的 IService 能力） */
    private final FicoVoucherService ficoVoucherService;

    /**
     * 构造器注入（@Autowired 标注构造器，显式声明依赖）
     *
     * @param ficoVoucherService 会计凭证 Service
     */
    @Autowired
    public FicoVoucherController(FicoVoucherService ficoVoucherService) {
        this.ficoVoucherService = ficoVoucherService;
    }

    /**
     * 分页查询会计凭证
     * <p>
     * 支持凭证编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param ro 分页查询参数（current/size/voucherNo/status）
     * @return 分页结果
     */
    @Operation(summary = "分页查询会计凭证", description = "支持凭证编号模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<FicoVoucher>> page(@Valid FicoVoucherPageRO ro) {
        Page<FicoVoucher> page = new Page<>(ro.getCurrent(), ro.getSize());
        LambdaQueryWrapper<FicoVoucher> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(ro.getVoucherNo() != null && !ro.getVoucherNo().isEmpty(),
                FicoVoucher::getVoucherNo, ro.getVoucherNo());
        wrapper.eq(ro.getStatus() != null, FicoVoucher::getStatus, ro.getStatus());
        wrapper.orderByDesc(FicoVoucher::getCreateTime);
        Page<FicoVoucher> result = ficoVoucherService.page(page, wrapper);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询会计凭证
     *
     * @param id 凭证 ID
     * @return 凭证信息
     */
    @Operation(summary = "根据 ID 查询会计凭证")
    @GetMapping("/{id}")
    public R<FicoVoucher> get(@Parameter(description = "凭证 ID") @PathVariable Long id) {
        FicoVoucher voucher = ficoVoucherService.getById(id);
        if (voucher == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(voucher);
    }

    /**
     * 新增会计凭证
     *
     * @param voucher 凭证实体
     * @return 操作结果
     */
    @Operation(summary = "新增会计凭证")
    @PostMapping
    public R<Void> save(@Parameter(description = "凭证信息") @Valid @RequestBody FicoVoucher voucher) {
        boolean ok = ficoVoucherService.save(voucher);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改会计凭证
     *
     * @param voucher 凭证实体
     * @return 操作结果
     */
    @Operation(summary = "修改会计凭证")
    @PutMapping
    public R<Void> update(@Parameter(description = "凭证信息") @Valid @RequestBody FicoVoucher voucher) {
        if (voucher.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "凭证 ID 不能为空");
        }
        boolean ok = ficoVoucherService.updateById(voucher);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除会计凭证（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 凭证 ID
     * @return 操作结果
     */
    @Operation(summary = "删除会计凭证", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "凭证 ID") @PathVariable Long id) {
        boolean ok = ficoVoucherService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
