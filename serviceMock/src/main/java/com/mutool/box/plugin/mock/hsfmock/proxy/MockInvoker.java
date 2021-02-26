package com.mutool.box.plugin.mock.hsfmock.proxy;

import java.lang.reflect.Proxy;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/1/29 12:19<br>
 */
public class MockInvoker {

    public static Object getInstance(Class<?> cls) {
        MethodProxy invocationHandler = new MethodProxy();
        Object newProxyInstance = Proxy.newProxyInstance(
                cls.getClassLoader(),
                new Class[]{cls},
                invocationHandler);
        return newProxyInstance;
    }

}
