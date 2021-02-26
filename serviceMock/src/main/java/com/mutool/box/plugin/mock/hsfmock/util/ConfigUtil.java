package com.mutool.box.plugin.mock.hsfmock.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.setting.Setting;
import com.mutool.box.plugin.mock.hsfmock.constants.MockConstant;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/25 13:23<br>
 */
public class ConfigUtil {


    public static void set(String key, String value) {
        if (!FileUtil.exist(MockConstant.CONFIG_FILE_PATH)) {
            FileUtil.touch(MockConstant.CONFIG_FILE_PATH);
        }
        Setting setting = new Setting(new File(MockConstant.CONFIG_FILE_PATH), Charset.defaultCharset(), true);
        //在配置文件变更时自动加载
        setting.autoLoad(true);
        setting.set(key, value);
        setting.store();
    }

    public static String get(String key) {
        if (!FileUtil.exist(MockConstant.CONFIG_FILE_PATH)) {
            FileUtil.touch(MockConstant.CONFIG_FILE_PATH);
        }
        Setting setting = new Setting(new File(MockConstant.CONFIG_FILE_PATH), Charset.defaultCharset(), true);
        return setting.getStr(key);
    }

    public static Properties getAllConfig() {
        if (!FileUtil.exist(MockConstant.CONFIG_FILE_PATH)) {
            FileUtil.touch(MockConstant.CONFIG_FILE_PATH);
        }
        Setting setting = new Setting(new File(MockConstant.CONFIG_FILE_PATH), Charset.defaultCharset(), true);
        return setting.toProperties();
    }


}
