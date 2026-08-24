package com.workspace.fatjar.fico.controller;

import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.fico.bo.FicoVoucherBO;
import com.workspace.fatjar.fico.convert.FicoVoucherConverter;
import com.workspace.fatjar.fico.query.FicoVoucherQuery;
import com.workspace.fatjar.fico.resultcode.FicoResultCode;
import com.workspace.fatjar.fico.service.FicoVoucherService;
import com.workspace.fatjar.fico.vo.FicoVoucherVO;
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
 * 跨模块调用，不在 Controller 重复暴露。Controller 通过 {@link FicoVoucherConverter} 将 Service
 * 返回的 BO 转换为 VO 返回前端，分页查询使用 {@link FicoVoucherQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/fico/voucher")
@RequiredArgsConstructor
@Tag(name = "财务会计模块-会计凭证", description = "会计凭证 CRUD")
public class FicoVoucherController {

    /** 会计凭证 Service（同时承担 FicoVoucherDO 的 IService 能力） */
    private final FicoVoucherService ficoVoucherService;

    /** MapStruct 转换器（BO -> VO） */
    private final FicoVoucherConverter converter;

    /**
     * 分页查询会计凭证
     * <p>
     * 支持凭证编号模糊查询、状态精确查询，结果按创建时间倒序。
     *
     * @param query 分页查询参数（current/size/voucherNo/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询会计凭证", description = "支持凭证编号模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<FicoVoucherVO>> page(@Valid FicoVoucherQuery query) {
        PageResult<FicoVoucherBO> boPage = ficoVoucherService.pageBO(query);
        PageResult<FicoVoucherVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询会计凭证
     *
     * @param id 凭证 ID
     * @return 凭证信息
     */
    @Operation(summary = "根据 ID 查询会计凭证")
    @GetMapping("/{id}")
    public R<FicoVoucherVO> get(@Parameter(description = "凭证 ID") @PathVariable Long id) {
        FicoVoucherBO bo = ficoVoucherService.getBOById(id);
        if (bo == null) {
            throw new com.workspace.fatjar.fico.exception.FicoBizException(FicoResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增会计凭证
     *
     * @param bo 凭证业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增会计凭证")
    @PostMapping
    public R<Void> save(@Parameter(description = "凭证信息") @Valid @RequestBody FicoVoucherBO bo) {
        boolean ok = ficoVoucherService.saveBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.fico.exception.FicoBizException(FicoResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改会计凭证
     *
     * @param bo 凭证业务对象
     * @return 操作结果
     */
    @Operation(summary = "修改会计凭证")
    @PutMapping
    public R<Void> update(@Parameter(description = "凭证信息") @Valid @RequestBody FicoVoucherBO bo) {
        if (bo.getId() == null) {
            throw new com.workspace.fatjar.fico.exception.FicoBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "凭证 ID 不能为空");
        }
        boolean ok = ficoVoucherService.updateBO(bo);
        if (!ok) {
            throw new com.workspace.fatjar.fico.exception.FicoBizException(FicoResultCode.OPERATION_FAILED);
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
        boolean ok = ficoVoucherService.removeBOById(id);
        if (!ok) {
            throw new com.workspace.fatjar.fico.exception.FicoBizException(FicoResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
