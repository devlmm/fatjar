package com.workspace.fatjar.common.enums;

import lombok.Getter;

/**
 * 通用状态枚举（启用/禁用）
 * <p>
 * 适用于用户、角色、菜单等通用启用状态字段。
 *
 * @author fatjar
 * @since 1.0.0
 */
@Getter
public enum CommonStatusEnum {

    /** 启用 */
    ENABLE(0, "启用"),
    /** 禁用 */
    DISABLE(1, "禁用");

    /** 状态码 */
    private final int code;
    /** 描述 */
    private final String desc;

    CommonStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据 code 获取枚举
     *
     * @param code 状态码
     * @return 枚举值，未匹配返回 null
     */
    public static CommonStatusEnum of(Integer code) {
        if (code == null) {
            return null;
        }
        for (CommonStatusEnum e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
