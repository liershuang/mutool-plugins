package com.mutool.mock.service;


/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/20 10:22<br>
 */
public interface MockConfigService<T> {
    /**
     * 获取配置
     * @return
     */
    T getConfig();

    String getMethodMockDataFilePath(String methodFullName);

}
