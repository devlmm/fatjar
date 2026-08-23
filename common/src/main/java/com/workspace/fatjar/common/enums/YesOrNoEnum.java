package com.workspace.fatjar.common.enums;

import lombok.Getter;

/**
 * 是否枚举（通用 0/1 二值标识）
 *
 * @author fatjar
 * @since 1.0.0
 */
@Getter
public enum YesOrNoEnum {

    /** 否 */
    NO(0, "否"),
    /** 是 */
    YES(1, "是");

    /** 状态码 */
    private final int code;
    /** 描述 */
    private final String desc;

    YesOrNoEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 判断是否为「是」
     *
     * @param code 状态码
     * @return true 表示 code == 1
     */
    public static boolean isYes(Integer code) {
        return code != null && code == YES.code;
    }
}
