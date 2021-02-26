package com.mutool.box.plugin.mock.hsfmock.model;

import lombok.Data;

/**
 * 描述：方法信息<br>
 * 作者：les<br>
 * 日期：2021/2/4 17:12<br>
 */
@Data
public class MethodInfo {

    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法全路径名（包含接口、方法名、参数）
     */
    private String methodFullName;

    /**
     * 返回类型
     */
    private String returnClass;

}
