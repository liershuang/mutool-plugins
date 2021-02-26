package com.mutool.box.plugin.mock.hsfmock.service;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/20 10:22<br>
 */
public interface MockBaseInfoService {

    String getJarDirPath();

    String getMockDataPath();

    String getMethodMockFilePath(String methodFullName);

}
