package com.mutool.mock.config;

import cn.hutool.system.SystemUtil;
import lombok.Data;

import java.io.File;

/**
 * 描述：hsf配置类<br>
 * 作者：les<br>
 * 日期：2021/2/28 21:42<br>
 */
@Data
public class HsfConfigProperties {

    /**
     * jar包存放位置
     */
    private String jarPath = SystemUtil.get("user.home") + "/mock/hsf/jar/";

    /**
     * 数据存放位置
     */
    private String dataPath = SystemUtil.get("user.home") + "/mock/hsf/data/";


    public void setJarPath(String jarPath) {
        this.jarPath = jarPath.endsWith(File.separator)?jarPath:jarPath+File.separator;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath.endsWith(File.separator)?dataPath:dataPath+File.separator;
    }

}
