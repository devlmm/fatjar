package com.workspace.fatjar.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workspace.fatjar.auth.entity.SysUser;
import com.workspace.fatjar.auth.service.AuthService;
import com.workspace.fatjar.common.exception.BizException;
import com.workspace.fatjar.common.exception.ErrorCode;
import com.workspace.fatjar.common.result.PageQuery;
import com.workspace.fatjar.common.result.PageResult;
import com.workspace.fatjar.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * 安全提示：所有响应中的 SysUser.password 字段会被清空，避免泄露哈希密文。
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
    /** 密码编码器（BCrypt，注册/修改用户时加密密码） */
    private final PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户
     *
     * @param pageQuery 分页参数（current/size）
     * @param username  用户名（模糊查询，可空）
     * @param nickname  昵称（模糊查询，可空）
     * @param status    状态（精确匹配，可空）
     * @return 分页结果（password 字段已清空）
     */
    @Operation(summary = "分页查询用户", description = "支持用户名/昵称模糊、状态精确查询")
    @GetMapping("/page")
    public R<PageResult<SysUser>> page(@Valid PageQuery pageQuery,
                                       @Parameter(description = "用户名（模糊）") @RequestParam(required = false) String username,
                                       @Parameter(description = "昵称（模糊）") @RequestParam(required = false) String nickname,
                                       @Parameter(description = "状态") @RequestParam(required = false) Integer status) {
        Page<SysUser> page = new Page<>(pageQuery.getCurrent(), pageQuery.getSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(username != null && !username.isEmpty(), SysUser::getUsername, username);
        wrapper.like(nickname != null && !nickname.isEmpty(), SysUser::getNickname, nickname);
        wrapper.eq(status != null, SysUser::getStatus, status);
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = authService.page(page, wrapper);
        // 清空密码字段
        result.getRecords().forEach(this::clearPassword);
        return R.ok(PageResult.of(result));
    }

    /**
     * 根据 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户信息（password 已清空）
     */
    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/{id}")
    public R<SysUser> get(@Parameter(description = "用户 ID") @PathVariable Long id) {
        SysUser user = authService.getById(id);
        if (user == null) {
            throw new BizException(ErrorCode.DATA_NOT_FOUND);
        }
        clearPassword(user);
        return R.ok(user);
    }

    /**
     * 新增用户
     * <p>
     * 密码处理：明文密码经 PasswordEncoder.encode 加密后存储。
     *
     * @param user 用户实体（含明文密码）
     * @return 操作结果
     */
    @Operation(summary = "新增用户", description = "密码自动 BCrypt 加密")
    @PostMapping("/save")
    public R<Void> save(@Parameter(description = "用户信息") @Valid @RequestBody SysUser user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, "密码不能为空");
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean ok = authService.save(user);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 修改用户
     * <p>
     * 密码处理：若传入非空密码则同步加密更新；为空则不更新密码（保持原密码）。
     *
     * @param user 用户实体
     * @return 操作结果
     */
    @Operation(summary = "修改用户", description = "密码非空时同步加密更新")
    @PutMapping("/update")
    public R<Void> update(@Parameter(description = "用户信息") @Valid @RequestBody SysUser user) {
        if (user.getId() == null) {
            throw new BizException(ErrorCode.PARAM_INVALID, "用户 ID 不能为空");
        }
        // 密码非空则加密，为空则置 null 避免覆盖原密码
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        boolean ok = authService.updateById(user);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
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
        boolean ok = authService.removeById(id);
        if (!ok) {
            throw new BizException(ErrorCode.OPERATION_FAILED);
        }
        return R.ok();
    }

    /**
     * 清空用户密码字段（避免响应泄露哈希密文）
     *
     * @param user 用户实体
     */
    private void clearPassword(SysUser user) {
        if (user != null) {
            user.setPassword(null);
        }
    }
}
