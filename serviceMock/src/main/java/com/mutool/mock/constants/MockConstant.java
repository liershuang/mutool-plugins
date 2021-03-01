package com.mutool.mock.constants;

import cn.hutool.system.SystemUtil;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/4 16:55<br>
 */
public class MockConstant {

    /**
     * class信息存储文件名
     */
    public static final String CLASS_DATA_FILENAME = "class_data.json";

    /**
     * 方法信息存储文件名
     */
    public static final String METHOD_DATA_FILENAME = "method_data.json";

    /**
     * 配置文件路径
     */
    public static final String CONFIG_FILE_PATH = SystemUtil.get("user.home") + "/mock/config/mock_config.properties";


}
