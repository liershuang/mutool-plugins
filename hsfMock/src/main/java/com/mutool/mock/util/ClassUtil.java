package com.mutool.mock.util;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.mutool.mock.model.MethodMsg;

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
    public static List<MethodMsg> getPublicMethods(String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        Method[] publicMethods = cn.hutool.core.util.ClassUtil.getPublicMethods(clazz);
        if (ArrayUtil.isEmpty(publicMethods)) {
            return Collections.EMPTY_LIST;
        }
        List<MethodMsg> methodInfoList = new ArrayList<>();
        for (Method method : publicMethods) {
            MethodMsg methodInfo = new MethodMsg();
            methodInfo.setClassName(className);
            methodInfo.setMethodName(method.getName());
            methodInfo.setMethodFullName(method.toString());
            methodInfo.setReturnClass(method.getReturnType().getName());

            methodInfoList.add(methodInfo);
        }

        return methodInfoList;
    }

    /**
     * class对象转json字符串
     *
     * @param clazz
     * @return
     */
    //问题解决：返回class对象为list时无法实例化
    //todo 返回对象为Integer等基本数据类型无法转换
    public static String turnClassToJson(Class clazz) throws IllegalAccessException, InstantiationException {
        Object returnObj = clazz.newInstance();
        if (!StrUtil.isBlank(String.valueOf(returnObj))) {
            //转json，设置空值不忽略
            JSON returnJSON = JSONUtil.parse(returnObj, JSONConfig.create().setIgnoreNullValue(false));
            return JSONUtil.toJsonStr(returnJSON);
        }
        return "";
    }


}
