package com.mutool.mock.controller;

import cn.hutool.core.io.FileUtil;
import com.mutool.mock.constants.ConfigConstant;
import com.mutool.mock.util.ConfigUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Properties;


/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/25 12:20<br>
 */
@Controller
@RequestMapping("mock/config")
public class SettingController {

    @ResponseBody
    @RequestMapping("setUserSettingsFile")
    public void setUserSettingsFile(String settingsFilePath) {
        if (!FileUtil.exist(settingsFilePath)) {
            throw new RuntimeException("文件不存在");
        }
        ConfigUtil.set(ConfigConstant.KEY_SETTINGS_FILE_PATH, settingsFilePath);
    }

    @ResponseBody
    @RequestMapping("getConfig")
    public Properties getConfig() {
        return ConfigUtil.getAllConfig();
    }


}
