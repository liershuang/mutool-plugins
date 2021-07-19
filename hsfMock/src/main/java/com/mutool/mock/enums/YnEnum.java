package com.mutool.mock.enums;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 22:09<br>
 */
public enum YnEnum {

    YES("1", "是"), NO("0", "否");

    private String code;

    private String text;


    YnEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
