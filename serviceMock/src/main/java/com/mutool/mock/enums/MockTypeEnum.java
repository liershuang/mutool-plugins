package com.mutool.mock.enums;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/28 22:20<br>
 */
public enum MockTypeEnum {
    HSF("hsf", "hsf服务");

    private String code;
    private String msg;

    MockTypeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
