package com.workspace.fatjar.oa.controller;

import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.oa.bo.OaApprovalBO;
import com.workspace.fatjar.oa.convert.OaApprovalConverter;
import com.workspace.fatjar.oa.exception.OaBizException;
import com.workspace.fatjar.oa.query.OaApprovalQuery;
import com.workspace.fatjar.oa.resultcode.OaResultCode;
import com.workspace.fatjar.oa.service.OaApprovalService;
import com.workspace.fatjar.oa.vo.OaApprovalVO;
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
 * OA 审批控制器（CRUD REST）
 * <p>
 * 路径前缀：/oa/approval
 * 接口列表：
 *   - GET    /oa/approval/page   ：分页查询审批
 *   - GET    /oa/approval/{id}   ：根据 ID 查询审批
 *   - POST   /oa/approval        ：新增审批
 *   - PUT    /oa/approval        ：修改审批
 *   - DELETE /oa/approval/{id}   ：根据 ID 删除审批
 * <p>
 * 说明：Controller 仅暴露内部 CRUD，不暴露门面方法（门面方法供其他模块跨模块调用）。
 * Controller 通过 {@link OaApprovalConverter} 将 Service 返回的 BO 转换为 VO 返回前端，
 * 分页查询使用 {@link OaApprovalQuery} 接收条件。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/oa/approval")
@RequiredArgsConstructor
@Tag(name = "办公自动化-审批管理", description = "OA 审批 CRUD")
public class OaApprovalController {

    /** 审批 Service（内部视角，同时承担 OaApprovalApi 门面实现） */
    private final OaApprovalService oaApprovalService;

    /** MapStruct 转换器（BO -> VO） */
    private final OaApprovalConverter converter;

    /**
     * 分页查询审批
     *
     * @param query 分页查询参数（current/size/title/status）
     * @return 分页结果（VO 列表）
     */
    @Operation(summary = "分页查询审批", description = "支持标题模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<OaApprovalVO>> page(@Valid OaApprovalQuery query) {
        PageResult<OaApprovalBO> boPage = oaApprovalService.pageBO(query);
        PageResult<OaApprovalVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询审批
     *
     * @param id 审批 ID
     * @return 审批信息
     */
    @Operation(summary = "根据 ID 查询审批")
    @GetMapping("/{id}")
    public R<OaApprovalVO> get(@Parameter(description = "审批 ID") @PathVariable Long id) {
        OaApprovalBO bo = oaApprovalService.getBOById(id);
        if (bo == null) {
            throw new OaBizException(OaResultCode.DATA_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增审批
     *
     * @param bo 审批业务对象
     * @return 操作结果
     */
    @Operation(summary = "新增审批")
    @PostMapping
    public R<Void> save(@Parameter(description = "审批信息") @Valid @RequestBody OaApprovalBO bo) {
        boolean ok = oaApprovalService.saveBO(bo);
        if (!ok) {
            throw new OaBizException(OaResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改审批
     *
     * @param bo 审批业务对象（id 不能为空）
     * @return 操作结果
     */
    @Operation(summary = "修改审批")
    @PutMapping
    public R<Void> update(@Parameter(description = "审批信息") @Valid @RequestBody OaApprovalBO bo) {
        if (bo.getId() == null) {
            throw new OaBizException(
                    com.workspace.fatjar.common.result.CommonResultCode.PARAM_INVALID, "审批 ID 不能为空");
        }
        boolean ok = oaApprovalService.updateBO(bo);
        if (!ok) {
            throw new OaBizException(OaResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除审批（逻辑删除）
     *
     * @param id 审批 ID
     * @return 操作结果
     */
    @Operation(summary = "删除审批", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "审批 ID") @PathVariable Long id) {
        boolean ok = oaApprovalService.removeBOById(id);
        if (!ok) {
            throw new OaBizException(OaResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
