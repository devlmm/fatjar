package com.workspace.fatjar.auth.controller;

import com.workspace.fatjar.auth.bo.SysUserBO;
import com.workspace.fatjar.auth.convert.SysUserConverter;
import com.workspace.fatjar.auth.query.SysUserQuery;
import com.workspace.fatjar.auth.service.AuthService;
import com.workspace.fatjar.auth.vo.SysUserVO;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import com.workspace.fatjar.common.result.CommonResultCode;
import com.workspace.fatjar.auth.exception.AuthBizException;
import com.workspace.fatjar.auth.resultcode.AuthResultCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户控制器（CRUD）
 * <p>
 * 路径前缀：/sys/user
 * 接口列表：
 *   - GET    /sys/user/page        ：分页查询用户
 *   - GET    /sys/user/{id}        ：根据 ID 查询用户
 *   - POST   /sys/user/save        ：新增用户（密码 BCrypt 加密后存储）
 *   - PUT    /sys/user/update       ：修改用户（密码非空则同步加密）
 *   - DELETE /sys/user/{id}        ：根据 ID 删除用户
 * <p>
 * 安全提示：SysUserVO 不含 password 字段，从结构上避免响应泄露哈希密文。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
@Tag(name = "权限模块-用户管理", description = "系统用户 CRUD")
public class SysUserController {

    /** 权限 Service（同时承担 SysUser 的 IService 能力） */
    private final AuthService authService;
    /** MapStruct 转换器（BO -> VO） */
    private final SysUserConverter converter;
    /** 密码编码器（BCrypt，新增/修改用户时加密密码） */
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     *
     * @param query 分页查询参数（current/size + username/nickname/status）
     * @return 分页结果（VO 列表，不含密码）
     */
    @Operation(summary = "分页查询用户", description = "支持用户名/昵称模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<SysUserVO>> page(@Valid SysUserQuery query) {
        PageResult<SysUserBO> boPage = authService.pageBO(query);
        PageResult<SysUserVO> voPage = new PageResult<>(
                boPage.getCurrent(), boPage.getSize(), boPage.getTotal(), boPage.getPages(),
                converter.toVOList(boPage.getRecords()));
        return R.ok(voPage);
    }

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息（VO 不含密码）
     */
    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/{id}")
    public R<SysUserVO> get(@Parameter(description = "用户 ID") @PathVariable Long id) {
        SysUserBO bo = authService.getBOById(id);
        if (bo == null) {
            throw new AuthBizException(AuthResultCode.USER_NOT_FOUND);
        }
        return R.ok(converter.toVO(bo));
    }

    /**
     * 新增用户
     * <p>
     * 密码处理：明文密码经 PasswordEncoder.encode 加密后存储。
     *
     * @param bo 用户业务对象（含明文密码）
     * @return 操作结果
     */
    @Operation(summary = "新增用户", description = "密码自动 BCrypt 加密")
    @PostMapping("/save")
    public R<Void> save(@Parameter(description = "用户信息") @Valid @RequestBody SysUserBO bo) {
        if (!StringUtils.hasText(bo.getPassword())) {
            throw new AuthBizException(CommonResultCode.PARAM_INVALID, "密码不能为空");
        }
        // 加密密码
        bo.setPassword(passwordEncoder.encode(bo.getPassword()));
        boolean ok = authService.saveBO(bo);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改用户
     * <p>
     * 密码处理：若传入非空密码则同步加密更新；为空则置 null 避免覆盖原密码
     * （MyBatis-Plus updateById 默认忽略 null 字段）。
     *
     * @param bo 用户业务对象
     * @return 操作结果
     */
    @Operation(summary = "修改用户", description = "密码非空时同步加密更新")
    @PutMapping("/update")
    public R<Void> update(@Parameter(description = "用户信息") @Valid @RequestBody SysUserBO bo) {
        if (bo.getId() == null) {
            throw new AuthBizException(CommonResultCode.PARAM_INVALID, "用户 ID 不能为空");
        }
        // 密码非空则加密，为空则置 null 避免覆盖原密码
        if (StringUtils.hasText(bo.getPassword())) {
            bo.setPassword(passwordEncoder.encode(bo.getPassword()));
        } else {
            bo.setPassword(null);
        }
        boolean ok = authService.updateBO(bo);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 根据 ID 删除用户（逻辑删除，由 MyBatis-Plus 自动处理 deleted 字段）
     *
     * @param id 用户 ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户", description = "逻辑删除")
    @DeleteMapping("/{id}")
    public R<Void> delete(@Parameter(description = "用户 ID") @PathVariable Long id) {
        boolean ok = authService.removeBOById(id);
        if (!ok) {
            throw new AuthBizException(CommonResultCode.OPERATION_FAILED);
        }
        return R.ok();
    }
}
