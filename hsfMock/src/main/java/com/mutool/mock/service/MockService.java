package com.mutool.mock.service;

import com.mutool.mock.bean.dto.ServiceApiInfo;
import com.mutool.mock.bean.model.MethodInfo;
import com.mutool.mock.bean.model.ServiceApi;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 18:56<br>
 */
public interface MockService {

    /**
     * 根据名称或class名称查询接口列表
     * @param className 类名（为空时查询所有）
     * @return
     */
    List<ServiceApiInfo> queryList(String className);

    List<ServiceApi> queryServiceList(String className);

    void updateServiceVersion(Integer serviceId, String version);

    /**
     * 根据serviceId查询方法列表
     * @param serviceId
     * @return
     */
    List<MethodInfo> queryMethodsByServiceId(Integer serviceId);

    /**
     * 根据方法id查询方法mock数据
     * @param methodId
     * @return
     */
    String queryMockDataByMethodId(Integer methodId);

    void setMockData(Integer methodId, String mockData);

    void deleteService(Integer serviceId);

    void deleteMethod(Integer methodId);
}
