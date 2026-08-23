package com.workspace.fatjar.oa.controller;

import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.oa.entity.OaApproval;
import com.workspace.fatjar.oa.ro.OaApprovalPageRO;
import com.workspace.fatjar.oa.service.OaApprovalService;
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
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/oa/approval")
@Tag(name = "办公自动化-审批管理", description = "OA 审批 CRUD")
public class OaApprovalController {

    /** 审批 Service（内部视角，同时承担 OaApprovalApi 门面实现） */
    private final OaApprovalService oaApprovalService;

    /**
     * 构造器注入（@Autowired 显式声明）
     *
     * @param oaApprovalService 审批 Service
     */
    @Autowired
    public OaApprovalController(OaApprovalService oaApprovalService) {
        this.oaApprovalService = oaApprovalService;
    }

    /**
     * 分页查询审批
     *
     * @param ro 分页查询请求（含 current/size + title/status 过滤条件）
     * @return 分页结果
     */
    @Operation(summary = "分页查询审批", description = "支持标题模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<OaApproval>> page(@Valid OaApprovalPageRO ro) {
        PageResult<OaApproval> result = oaApprovalService.page(ro);
        return R.ok(result);
    }

    /**
     * 根据 ID 查询审批
     *
     * @param id 审批 ID
     * @return 审批信息
     */
    @Operation(summary = "根据 ID 查询审批")
    @GetMapping("/{id}")
    public R<OaApproval> get(@Parameter(description = "审批 ID") @PathVariable Long id) {
        OaApproval approval = oaApprovalService.getById(id);
        if (approval == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        return R.ok(approval);
    }

    /**
     * 新增审批
     *
     * @param approval 审批实体
     * @return 操作结果
     */
    @Operation(summary = "新增审批")
    @PostMapping
    public R<Void> save(@Parameter(description = "审批信息") @Valid @RequestBody OaApproval approval) {
        boolean ok = oaApprovalService.save(approval);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改审批
     *
     * @param approval 审批实体
     * @return 操作结果
     */
    @Operation(summary = "修改审批")
    @PutMapping
    public R<Void> update(@Parameter(description = "审批信息") @Valid @RequestBody OaApproval approval) {
        if (approval.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "审批 ID 不能为空");
        }
        boolean ok = oaApprovalService.update(approval);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
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
        boolean ok = oaApprovalService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
