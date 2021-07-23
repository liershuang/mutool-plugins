package com.mutool.mock.service.impl;

import cn.hutool.core.util.StrUtil;
import com.mutool.core.exception.BizException;
import com.mutool.mock.bean.model.MethodInfo;
import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.mapper.MethodInfoMapper;
import com.mutool.mock.mapper.ServiceApiMapper;
import com.mutool.mock.model.MethodMsg;
import com.mutool.mock.service.MethodInfoService;
import com.mutool.mock.util.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/16 20:02<br>
 */
@Slf4j
@Service
public class MethodInfoServiceImpl implements MethodInfoService {

    @Autowired
    private MethodInfoMapper methodInfoMapper;
    @Autowired
    private ServiceApiMapper serviceApiMapper;

    @Override
    public Integer addMethodInfo(MethodInfo methodInfo) {
        MethodInfo methodInfoInDB = methodInfoMapper.queryByMethodFullName(methodInfo.getMethodFullName());
        if(methodInfoInDB != null){
            return methodInfoInDB.getId();
        }
        methodInfoMapper.add(methodInfo);
        return methodInfo.getId();
    }

    @Override
    public String getMethodMockData(String methodFullName) {
        MethodInfo methodInfo = methodInfoMapper.queryByMethodFullName(methodFullName);
        return StrUtil.blankToDefault(methodInfo.getMockData(), "");
    }

    @Override
    @Transactional
    public void addServiceMethodInfo(Integer serviceId) {
        ServiceApi serviceApi = serviceApiMapper.queryServiceApiByServiceId(serviceId);
        if(serviceApi == null){
            throw new BizException("接口不存在");
        }
        List<MethodMsg> classMethodList = null;
        try {
            classMethodList = ClassUtil.getPublicMethods(serviceApi.getClassName());
        } catch (ClassNotFoundException e) {
            log.error("解析方法列表异常，接口：{}"+serviceApi.getClassName(), e);
            throw new BizException("解析方法列表异常");
        }
        for(MethodMsg methodMsg : classMethodList){
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setServiceId(serviceId);
            methodInfo.setMethodName(methodMsg.getMethodName());
            methodInfo.setMethodFullName(methodMsg.getMethodFullName());
            methodInfo.setReturnClass(methodMsg.getReturnClass());
            //todo 解析返回class类，转为json作为mock数据
            //methodInfo.setMockData("");
            addMethodInfo(methodInfo);
        }
    }

    /**
     * 根据方法名设置mock数据
     * @param methodFullName
     * @param mockData
     */
    @Override
    public void setMockDataByMethodFullName(String methodFullName, String mockData){
        methodInfoMapper.setMockDataByMethodFullName(methodFullName, mockData);
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
    public void deleteMethod(Integer methodId) {
        methodInfoMapper.deleteByMethodId(methodId);
    }

}
