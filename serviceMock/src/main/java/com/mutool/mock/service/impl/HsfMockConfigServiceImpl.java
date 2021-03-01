package com.mutool.mock.service.impl;

import com.mutool.mock.config.HsfConfigProperties;
import com.mutool.mock.service.MockConfigService;
import org.springframework.stereotype.Service;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/20 10:27<br>
 */
@Service
public class HsfMockConfigServiceImpl implements MockConfigService<HsfConfigProperties> {


    @Override
    public HsfConfigProperties getConfig() {
        return new HsfConfigProperties();
    }

    @Override
    public String getMethodMockDataFilePath(String methodFullName) {
        return getConfig().getDataPath() + "methodData/" + methodFullName + ".json";
    }
}
