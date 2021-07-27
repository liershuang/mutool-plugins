package com.mutool.mock.bean.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/26 21:17<br>
 */
@Data
public class ServiceMock implements Serializable {

    private static final long serialVersionUID = -7166900536803048226L;

    private String serviceClass;

    private List<MethodMock> mockMethodDataList;

}
