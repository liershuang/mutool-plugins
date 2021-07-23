package com.mutool.mock.model;

import cn.hutool.system.SystemUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 描述：hsf配置类<br>
 * 作者：les<br>
 * 日期：2021/2/28 21:42<br>
 */
@Data
@Component
@ConfigurationProperties(prefix = "mock.hsf")
public class HsfConfigProperties {

    /**
     * jar包存放位置
     */
    private String jarPath = SystemUtil.get("user.home") + "/mutool/hsf/jar/";

}
