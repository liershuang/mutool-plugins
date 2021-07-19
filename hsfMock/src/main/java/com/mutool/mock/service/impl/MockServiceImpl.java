package com.mutool.mock.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.mutool.mock.bean.dto.ServiceApiInfo;
import com.mutool.mock.mapper.MethodInfoMapper;
import com.mutool.mock.mapper.ServiceApiMapper;
import com.mutool.mock.bean.model.MethodInfo;
import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.service.MockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 19:01<br>
 */
@Service
public class MockServiceImpl implements MockService {

    @Autowired
    private ServiceApiMapper serviceApiMapper;
    @Autowired
    private MethodInfoMapper methodInfoMapper;


    /**
     * 根据名称或class名称查询接口列表
     *
     * @param className
     * @return
     */
    @Override
    public List<ServiceApiInfo> queryList(String className) {
        List<ServiceApi> serviceList = serviceApiMapper.queryListByClassNameLike(className);
        if(CollUtil.isEmpty(serviceList)){
            return Collections.emptyList();
        }
        return serviceList.stream().map(i -> {
            ServiceApiInfo apiInfo = new ServiceApiInfo();
            apiInfo.setId(i.getId());
            apiInfo.setClassName(i.getClassName());
            apiInfo.setVersion(i.getVersion());
            apiInfo.setOnlineStatus(i.getOnlineStatus());

            List<MethodInfo> methodList = methodInfoMapper.queryByServiceId(i.getId());
            apiInfo.setMethodList(methodList);
            return apiInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ServiceApi> queryServiceList(String className) {
        return serviceApiMapper.queryListByClassNameLike(className);
    }

    @Override
    public void updateServiceVersion(Integer serviceId, String version) {
        serviceApiMapper.updateServiceVersionByServiceId(serviceId, version);
    }

    /**
     * 根据serviceId查询方法列表
     *
     * @param serviceId
     * @return
     */
    @Override
    public List<MethodInfo> queryMethodsByServiceId(Integer serviceId) {
        return methodInfoMapper.queryByServiceId(serviceId);
    }

    /**
     * 根据方法id查询方法mock数据
     *
     * @param methodId
     * @return
     */
    @Override
    public String queryMockDataByMethodId(Integer methodId) {
        return methodInfoMapper.queryMockDataByMethodId(methodId);
    }

    @Override
    public void setMockData(Integer methodId, String mockData) {
        methodInfoMapper.setMockDataByMethodId(methodId, mockData);
    }

    @Override
    public void deleteService(Integer serviceId) {
        serviceApiMapper.deleteByServiceId(serviceId);
        methodInfoMapper.deleteByServiceId(serviceId);
    }

    @Override
    public void deleteMethod(Integer methodId) {
        methodInfoMapper.deleteByMethodId(methodId);
    }
}
