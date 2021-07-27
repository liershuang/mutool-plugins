package com.mutool.mock.bean.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/26 21:22<br>
 */
@Data
public class MethodMock implements Serializable {

    private static final long serialVersionUID = 3018268032900796157L;

    private String methodFullName;

    private String mockData;

}
