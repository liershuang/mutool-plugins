package com.mutool.box.plugin.mock.hsfmock.proxy;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mutool.box.plugin.mock.hsfmock.util.MockUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/1/29 11:23<br>
 */
@Slf4j
public class MethodProxy implements InvocationHandler {

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
            String methodMockData = MockUtil.getMethodMockData(method.toString());
            if (StrUtil.isBlank(methodMockData)) {
                return null;
            }
            Object methodMockObj = JSONUtil.toBean(methodMockData, method.getReturnType());
            return methodMockObj;
        }
        return null;
    }
}
