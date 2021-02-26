package com.mutool.box.plugin.mock.hsfmock.config;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/24 10:22<br>
 */
@Configuration
public class BeanLoadConfig {

    @Bean
    public SpringUtil springUtil() {
        return new SpringUtil();
    }

}
