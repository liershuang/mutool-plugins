package com.mutool.mock.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.mutool.mock.model.HsfConfigProperties;
import com.mutool.mock.helper.HsfHelper;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.model.JarInfo;
import com.mutool.mock.service.MockConfigService;
import com.mutool.mock.service.MockService;
import com.mutool.mock.util.JarUtil;
import com.mutool.mock.util.MockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/4 09:46<br>
 */
@Slf4j
@Service
public class HsfMockServiceImpl implements MockService<HsfServiceInfo> {

    @Autowired
    private HsfHelper hsfHelper;
    @Autowired
    private MockConfigService<HsfConfigProperties> mockConfigService;

    @Override
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

        return interfaceList;
    }

    @Override
    public void addJarToServer() {
        MockUtil.addJarToServer(mockConfigService.getConfig().getJarPath());
    }

    @Override
    public List<HsfServiceInfo> registerServcie(String jarPath) {
        //获取jar中配置的hsf接口
        List<HsfServiceInfo> hsfModelList = resolveServiceList(jarPath);
        log.debug("解析jar包中接口完成，接口列表：{}", hsfModelList);
        //发布注册接口
        return hsfHelper.registerService(hsfModelList);
    }

    @Override
    public void saveMethodMockData(String methodFullName, String mockData) {
        String dataFileName = mockConfigService.getMethodMockDataFilePath(methodFullName);
        FileUtil.del(dataFileName);
        FileUtil.writeUtf8String(mockData, dataFileName);
    }

    @Override
    public String getMockDataByMethod(String methodFullPath) {
        return hsfHelper.getMethodMockData(methodFullPath);
    }

    @Override
    public List<HsfServiceInfo> queryServiceList() {
        List<JarInfo<HsfServiceInfo>> mockJarInfo = hsfHelper.getJarInfoList();
        if (CollUtil.isEmpty(mockJarInfo)) {
            return Collections.emptyList();
        }
        List<HsfServiceInfo> serviceList = new ArrayList<>();
        mockJarInfo.forEach(i -> {
            serviceList.addAll(i.getServiceList());
        });
        return serviceList;
    }

    @Override
    public void loadAllLocalService() {
        //遍历所有jar加载到内存中
        addJarToServer();
        //过滤jar包直接目录下jar并解析其中service接口作为微服务接口，因直接目录下都是导入的jar
        List<File> servceJarList = FileUtil.loopFiles(new File(mockConfigService.getConfig().getJarPath()), 1,
                i -> i.getName().endsWith(".jar"));
        //注册微服务接口
        servceJarList.forEach(i -> {
            registerServcie(i.getPath());
        });
    }

    @Override
    public void updateServiceInfo(HsfServiceInfo serviceInfo) {
        JarInfo<HsfServiceInfo> jarInfo = hsfHelper.getJarInfoByService(serviceInfo.getInterfaceName());
        if (jarInfo == null) {
            throw new RuntimeException("接口信息不存在");
        }
        jarInfo.getServiceList().removeIf(i -> serviceInfo.getInterfaceName().equals(i.getInterfaceName()));
        jarInfo.getServiceList().add(serviceInfo);
        hsfHelper.saveServiceMockData(jarInfo);
    }

    @Override
    public void saveServiceMockData(JarInfo jarInfo) {
        hsfHelper.saveServiceMockData(jarInfo);
    }
}
