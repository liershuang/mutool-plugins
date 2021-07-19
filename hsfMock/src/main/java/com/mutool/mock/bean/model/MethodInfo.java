package com.mutool.mock.bean.model;

import lombok.Data;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 18:40<br>
 */
@Data
public class MethodInfo {

    private Integer id;

    private Integer serviceId;

    private String methodName;

    private String methodFullName;

    private String returnClass;

    private String mockData;

}
