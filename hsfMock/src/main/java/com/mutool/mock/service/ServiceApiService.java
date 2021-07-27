package com.mutool.mock.service;

import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.bean.dto.ServiceMock;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/16 19:56<br>
 */
public interface ServiceApiService {

    Integer addServiceInfo(ServiceApi serviceApi);

    Integer batchDelete(List<Integer> serviceIdList);

    List<ServiceApi> queryServiceList(String className);

    List<ServiceApi> queryServiceList(List<Integer> idList);

    void updateServiceVersion(Integer serviceId, String version);

    void deleteService(Integer serviceId);

    ServiceApi queryServiceById(Integer id);

    List<ServiceMock> queryServiceMockData(List<Integer> idList);

    void importMockData(List<ServiceMock> serviceMockList);
}
