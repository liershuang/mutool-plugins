package com.mutool.mock.proxy;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mutool.mock.helper.HsfHelper;
import com.mutool.mock.service.MethodInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/1/29 11:23<br>
 */
@Slf4j
@Component
public class MethodProxy implements InvocationHandler {
    @Autowired
    private MethodInfoService methodInfoService;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //如果传进来是一个已实现的具体类
        if (Object.class.equals(method.getDeclaringClass())) {
            try {
                return method.invoke(this, args);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            //如果传进来的是一个接口（核心)
        } else {
            log.info("开始调用方法，方法名：{}", method.toString());
            //根据方法名返回对应的方法配置的json结果数据
            String methodMockData = methodInfoService.getMethodMockData(method.toString());
            if (StrUtil.isBlank(methodMockData)) {
                return null;
            }
            Object methodMockObj = JSONUtil.toBean(methodMockData, method.getReturnType());
            return methodMockObj;
        }
        return null;
    }
}
