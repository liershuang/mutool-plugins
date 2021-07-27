package com.mutool.mock.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mutool.core.exception.BizException;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.proxy.MockInvoker;
import com.mutool.mock.util.JarUtil;
import com.taobao.hsf.app.api.util.HSFApiProviderBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：hsf帮助类<br>
 * 作者：les<br>
 * 日期：2021/2/20 10:06<br>
 */
@Slf4j
@Component
public class HsfHelper {

    /**
     * 注册jar包到注册中心
     * @param jarPath
     * @return
     */
    public List<HsfServiceInfo> registerServcie(String jarPath) {
        //获取jar中配置的hsf接口
        List<HsfServiceInfo> hsfModelList = resolveServiceList(jarPath);
        //发布注册接口
        return registerService(hsfModelList);
    }

    /**
     * 注册发布接口服务
     *
     * @param hsfModelList
     * @return 注册成功接口列表
     */
    public List<HsfServiceInfo> registerService(List<HsfServiceInfo> hsfModelList) {
        log.info("开始注册接口服务");
        List<HsfServiceInfo> succeessServiceList = new ArrayList<>();
        if (CollUtil.isEmpty(hsfModelList)) {
            return succeessServiceList;
        }
        for (HsfServiceInfo hsfServiceInfo : hsfModelList) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(hsfServiceInfo.getInterfaceName());
            } catch (ClassNotFoundException e) {
                log.error("获取接口class对象异常，接口为：{}", hsfServiceInfo.getInterfaceName(), e);
                continue;
            }

            try {
                HSFApiProviderBean hsfApiProviderBean = new HSFApiProviderBean();
                hsfApiProviderBean.setServiceInterface(hsfServiceInfo.getInterfaceName());
                if (StrUtil.isBlank(hsfServiceInfo.getVersion())) {
                    hsfApiProviderBean.setServiceVersion("1.0.0");
                } else {
                    hsfApiProviderBean.setServiceVersion(hsfServiceInfo.getVersion());
                }
                hsfApiProviderBean.setServiceGroup("HSF");
                //生成代理实现类
                Object serviceProxy = MockInvoker.getInstance(clazz);
                hsfApiProviderBean.setTarget(serviceProxy);
                // 初始化Provider Bean，发布服务
                hsfApiProviderBean.init();
            } catch (Exception e) {
                log.error("接口发布异常，接口为：{}", hsfServiceInfo.getInterfaceName(), e);
                e.printStackTrace();
                throw new BizException("hsf接口注册失败，请检查edas容器是否启动");
            }
            succeessServiceList.add(hsfServiceInfo);
        }
        log.info("注册接口服务完成，注册成功接口：{}", succeessServiceList);
        return succeessServiceList;
    }

    /**
     * 解析jar包中接口列表
     * @param jarPath
     * @return
     */
    public List<HsfServiceInfo> resolveServiceList(String jarPath) {
        //获取jar包中所有interface接口
        List<String> classList = JarUtil.listJarClassFilePath(jarPath);

        List<HsfServiceInfo> interfaceList = classList.stream()
                //class文件路径转类路径
                .map(i -> i.replace(".class", "").replace("/", "."))
                .filter(i -> {//过滤接口类
                    try {
                        Class clazz = Class.forName(i);
                        if (clazz.isInterface()) {
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                })
                .map(i -> {//构造接口类
                    HsfServiceInfo hsfInterfaceInfo = new HsfServiceInfo();
                    hsfInterfaceInfo.setInterfaceName(i);
                    return hsfInterfaceInfo;
                }).collect(Collectors.toList());
        log.info("jar包接口解析完成，接口列表：{}", interfaceList);
        return interfaceList;
    }

}
