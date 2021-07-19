package com.mutool.mock.service.impl;

import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.mapper.MethodInfoMapper;
import com.mutool.mock.mapper.ServiceApiMapper;
import com.mutool.mock.service.ServiceApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

}
