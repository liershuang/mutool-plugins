package com.mutool.box.plugin.mock.hsfmock.service;

import com.mutool.box.plugin.mock.hsfmock.model.HsfInterfaceInfo;

import java.util.List;

/**
 * 描述：mock服务类<br>
 * 作者：les<br>
 * 日期：2021/2/2 16:42<br>
 */
public interface MockService<T> {

    /**
     * 从jar中解析服务接口列表
     *
     * @param jarPath
     * @return
     */
    List<T> resolveServiceList(String jarPath);

    /**
     * 添加jar包类到系统中（不针对某个jar，因jar有依赖，解析麻烦，直接全量）
     */
    void addJarToServer();

    /**
     * 注册jar包的service
     *
     * @param jarPath
     * @return 注册成功的接口
     */
    List<T> registerServcie(String jarPath);

    void saveMethodMockData(String methodFullName, String mockData);

    /**
     * 根据方法全限定名查找mock对应的json数据
     *
     * @param methodFullPath 方法全限定名（包含参数）
     * @return
     */
    String getMockDataByMethod(String methodFullPath);

    List<T> queryServiceList();

    /**
     * 记载本地已有jar中service
     */
    void loadAllLocalService();

}
