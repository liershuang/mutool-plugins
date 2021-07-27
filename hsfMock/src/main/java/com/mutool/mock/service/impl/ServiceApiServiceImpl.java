package com.mutool.mock.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.mutool.mock.bean.dto.MethodMock;
import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.mapper.MethodInfoMapper;
import com.mutool.mock.mapper.ServiceApiMapper;
import com.mutool.mock.bean.dto.ServiceMock;
import com.mutool.mock.service.ServiceApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/16 19:57<br>
 */
@Service
public class ServiceApiServiceImpl implements ServiceApiService {
    @Autowired
    private ServiceApiMapper serviceApiMapper;
    @Autowired
    private MethodInfoMapper methodInfoMapper;

    @Override
    @Transactional
    public Integer addServiceInfo(ServiceApi serviceApi) {
        ServiceApi serviceApiInDB = serviceApiMapper.queryByClassName(serviceApi.getClassName());
        if(serviceApiInDB != null){
            return serviceApiInDB.getId();
        }
        serviceApiMapper.add(serviceApi);
        return serviceApi.getId();
    }

    @Override
    @Transactional
    public Integer batchDelete(List<Integer> serviceIdList) {
        Integer num = serviceApiMapper.batchDelete(serviceIdList);
        methodInfoMapper.batchDeleteByServiceId(serviceIdList);
        return num;
    }

    @Override
    public List<ServiceApi> queryServiceList(String className) {
        return serviceApiMapper.queryListByClassNameLike(className);
    }

    @Override
    public List<ServiceApi> queryServiceList(List<Integer> idList) {
        return serviceApiMapper.queryServiceList(idList);
    }

    @Override
    public void updateServiceVersion(Integer serviceId, String version) {
        serviceApiMapper.updateServiceVersionByServiceId(serviceId, version);
    }

    @Override
    public void deleteService(Integer serviceId) {
        serviceApiMapper.deleteByServiceId(serviceId);
        methodInfoMapper.deleteByServiceId(serviceId);
    }

    @Override
    public ServiceApi queryServiceById(Integer id) {
        return serviceApiMapper.queryServiceApiById(id);
    }

    @Override
    public List<ServiceMock> queryServiceMockData(List<Integer> idList) {
        List<ServiceApi> serviceList = queryServiceList(idList);
        if(CollUtil.isEmpty(serviceList)){
            return Collections.emptyList();
        }
        return serviceList.stream().map(i -> {
            ServiceMock serviceMock = new ServiceMock();
            serviceMock.setServiceClass(i.getClassName());
            List<MethodMock> methodMockList = methodInfoMapper.queryMethodDataByServiceId(i.getId());
            serviceMock.setMockMethodDataList(methodMockList);
            return serviceMock;
        }).collect(Collectors.toList());
    }

    @Override
    public void importMockData(List<ServiceMock> serviceMockList){
        if(CollUtil.isEmpty(serviceMockList)){
            return;
        }
        serviceMockList.forEach(i -> {
            ServiceApi serviceApi = serviceApiMapper.queryByClassName(i.getServiceClass());
            if(serviceApi == null || CollUtil.isEmpty(i.getMockMethodDataList())){
                //无此接口不设置，return跳过本条数据
                return;
            }
            i.getMockMethodDataList().forEach(methodInfo -> {
                methodInfoMapper.setMockDataByMethodFullName(methodInfo.getMethodFullName(), methodInfo.getMockData());
            });
        });
    }


}
