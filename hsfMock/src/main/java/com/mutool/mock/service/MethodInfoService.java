package com.mutool.mock.service;

import com.mutool.mock.bean.model.MethodInfo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/16 20:01<br>
 */
public interface MethodInfoService {

    /**
     * 插入方法，返回id
     * @param methodInfo
     * @return
     */
    Integer addMethodInfo(MethodInfo methodInfo);

    String getMethodMockData(String methodFullName);

    /**
     * 根据方法名解析方法并保存
     * @param serviceId
     * @return
     */
    void addServiceMethodInfo(Integer serviceId);

    /**
     * 设置方法mock数据
     * @param methodFullName
     * @param mockData
     */
    void setMockDataByMethodFullName(String methodFullName, String mockData);

    List<MethodInfo> queryMethodsByServiceId(Integer serviceId);

    String queryMockDataByMethodId(@Valid @NotNull(message = "方法id不能为空") Integer methodId);

    void setMockData(Integer methodId, String mockData);

    void deleteMethod(Integer methodId);

    void batchDelete(List<Integer> idList);
}
