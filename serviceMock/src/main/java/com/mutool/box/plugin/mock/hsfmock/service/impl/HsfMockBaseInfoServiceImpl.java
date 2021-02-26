package com.mutool.box.plugin.mock.hsfmock.service.impl;

import com.mutool.box.plugin.mock.hsfmock.service.MockBaseInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/20 10:27<br>
 */
@Service
public class HsfMockBaseInfoServiceImpl implements MockBaseInfoService {

    /**
     * jar包配置路径
     */
    @Value("${mock.hsf.jar.path:#{systemProperties['user.home']}/mock/hsf/jar/}")
    private String hsfJarPath;
    /**
     * mock数据路径
     */
    @Value("${mock.hsf.data.path:#{systemProperties['user.home']}/mock/hsf/data/}")
    private String hsfMockDataPath;

    @Override
    public String getJarDirPath() {
        return hsfJarPath;
    }

    @Override
    public String getMockDataPath() {
        return hsfMockDataPath;
    }

    @Override
    public String getMethodMockFilePath(String methodFullName) {
        return getMockDataPath() + "methodData/" + methodFullName + ".json";
    }
}
