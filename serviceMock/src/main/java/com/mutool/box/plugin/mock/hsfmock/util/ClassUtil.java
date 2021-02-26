package com.mutool.box.plugin.mock.hsfmock.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import com.mutool.box.plugin.mock.hsfmock.model.MethodInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/5 10:33<br>
 */
public class ClassUtil extends cn.hutool.core.util.ClassUtil {

    /**
     * 获取类的公共方法列表
     *
     * @param className 类路径名
     * @return
     * @throws ClassNotFoundException
     */
    public static List<MethodInfo> getPublicMethods(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Method[] publicMethods = cn.hutool.core.util.ClassUtil.getPublicMethods(clazz);
        if (ArrayUtil.isEmpty(publicMethods)) {
            return Collections.EMPTY_LIST;
        }
        List<MethodInfo> methodInfoList = new ArrayList<>();
        for (Method method : publicMethods) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setClassName(className);
            methodInfo.setMethodName(method.getName());
            methodInfo.setMethodFullName(method.toString());
            methodInfo.setReturnClass(method.getReturnType().getName());

            methodInfoList.add(methodInfo);
        }

        return methodInfoList;
    }


}
