package com.mutool.box.plugin.mock.hsfmock.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mutool.box.plugin.mock.hsfmock.model.HsfInterfaceInfo;
import com.mutool.box.plugin.mock.hsfmock.proxy.MockInvoker;
import com.taobao.hsf.app.api.util.HSFApiProviderBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：hsf帮助类<br>
 * 作者：les<br>
 * 日期：2021/2/20 10:06<br>
 */
@Slf4j
@Component
public class HsfHelper {

    /**
     * 注册发布接口服务
     *
     * @param hsfModelList
     * @return 注册成功接口列表
     */
    public List<HsfInterfaceInfo> registerService(List<HsfInterfaceInfo> hsfModelList) {
        log.info("开始注册接口服务");
        List<HsfInterfaceInfo> succeessServiceList = new ArrayList<>();
        if (CollUtil.isEmpty(hsfModelList)) {
            return succeessServiceList;
        }
        for (HsfInterfaceInfo hsfInterfaceInfo : hsfModelList) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(hsfInterfaceInfo.getInterfaceName());
            } catch (ClassNotFoundException e) {
                log.error("获取接口class对象异常，接口为：{}", hsfInterfaceInfo.getInterfaceName(), e);
                continue;
            }
            HSFApiProviderBean hsfApiProviderBean = new HSFApiProviderBean();
            hsfApiProviderBean.setServiceInterface(hsfInterfaceInfo.getInterfaceName());
            if (StrUtil.isBlank(hsfInterfaceInfo.getVersion())) {
                hsfApiProviderBean.setServiceVersion("1.0.0");
            } else {
                hsfApiProviderBean.setServiceVersion(hsfInterfaceInfo.getVersion());
            }
            hsfApiProviderBean.setServiceGroup("HSF");
            //生成代理实现类
            Object serviceProxy = MockInvoker.getInstance(clazz);
            hsfApiProviderBean.setTarget(serviceProxy);
            // 初始化Provider Bean，发布服务
            try {
                hsfApiProviderBean.init();
            } catch (Exception e) {
                log.error("接口发布异常，接口为：{}", hsfInterfaceInfo.getInterfaceName(), e);
                e.printStackTrace();
            }
            succeessServiceList.add(hsfInterfaceInfo);
        }
        log.info("注册接口服务完成，注册成功接口：{}", succeessServiceList);
        return succeessServiceList;
    }

    public List<HsfInterfaceInfo> save(List<HsfInterfaceInfo> hsfModelList) {
        List<HsfInterfaceInfo> succeessServiceList = new ArrayList<>();
        if (CollUtil.isEmpty(hsfModelList)) {
            return succeessServiceList;
        }
        for (HsfInterfaceInfo hsfInterfaceInfo : hsfModelList) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(hsfInterfaceInfo.getInterfaceName());
            } catch (ClassNotFoundException e) {
                log.error("获取接口class对象异常，接口为：{}", hsfInterfaceInfo.getInterfaceName(), e);
                continue;
            }
            HSFApiProviderBean hsfApiProviderBean = new HSFApiProviderBean();
            hsfApiProviderBean.setServiceInterface(hsfInterfaceInfo.getInterfaceName());
            hsfApiProviderBean.setServiceVersion(hsfInterfaceInfo.getVersion());
            hsfApiProviderBean.setServiceGroup("HSF");
            //生成代理实现类
            Object serviceProxy = MockInvoker.getInstance(clazz);
            hsfApiProviderBean.setTarget(serviceProxy);
            // 初始化Provider Bean，发布服务
            try {
                hsfApiProviderBean.init();
            } catch (Exception e) {
                log.error("接口发布异常，接口为：{}", hsfInterfaceInfo.getInterfaceName(), e);
                e.printStackTrace();
            }
            succeessServiceList.add(hsfInterfaceInfo);
        }
        return succeessServiceList;
    }

}
