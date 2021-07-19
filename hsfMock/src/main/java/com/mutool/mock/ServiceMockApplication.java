package com.mutool.mock;

import cn.hutool.extra.spring.SpringUtil;
import com.mutool.mock.helper.MockHelper;
import com.taobao.pandora.boot.PandoraBootstrap;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mutool.**.mapper")
@SpringBootApplication(scanBasePackages = {"com.mutool"})
public class ServiceMockApplication {

    public static void main(String[] args) throws Exception {
        // 启动Pandora Boot用于加载Pandora容器
        PandoraBootstrap.run(args);
        //启动spring容器
        SpringApplication.run(ServiceMockApplication.class, args);
        //初始化
        init();
        //标记服务启动完成，并设置线程wait。防止业务代码运行完毕退出后，导致容器退出
        PandoraBootstrap.markStartupAndWait();
    }

    private static void init() {
        MockHelper mockHelper = SpringUtil.getBean(MockHelper.class);
        //启动加载目录jar包，自动注册到注册中心
        mockHelper.loadAllLocalService();
    }

}
