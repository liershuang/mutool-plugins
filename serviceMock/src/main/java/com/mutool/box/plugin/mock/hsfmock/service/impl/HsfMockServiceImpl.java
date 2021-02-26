package com.mutool.box.plugin.mock.hsfmock.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.mutool.box.plugin.mock.hsfmock.helper.HsfHelper;
import com.mutool.box.plugin.mock.hsfmock.util.MockUtil;
import com.mutool.box.plugin.mock.hsfmock.model.HsfInterfaceInfo;
import com.mutool.box.plugin.mock.hsfmock.model.JarInfo;
import com.mutool.box.plugin.mock.hsfmock.service.MockBaseInfoService;
import com.mutool.box.plugin.mock.hsfmock.service.MockService;
import com.mutool.box.plugin.mock.hsfmock.util.JarUtil;
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
public class HsfMockServiceImpl implements MockService<HsfInterfaceInfo> {

    @Autowired
    private HsfHelper hsfHelper;
    @Autowired
    private MockBaseInfoService mockBaseInfoService;

    @Override
    public List<HsfInterfaceInfo> resolveServiceList(String jarPath) {
        //获取jar包中所有interface接口
        List<String> classList = JarUtil.listJarClassFilePath(jarPath);

        List<HsfInterfaceInfo> interfaceList = classList.stream()
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
                    HsfInterfaceInfo hsfInterfaceInfo = new HsfInterfaceInfo();
                    hsfInterfaceInfo.setInterfaceName(i);
                    return hsfInterfaceInfo;
                }).collect(Collectors.toList());

        return interfaceList;
    }

    @Override
    public void addJarToServer() {
        //将jar路径下所有文件加载到内存中
        List<File> jarList = FileUtil.loopFiles(mockBaseInfoService.getJarDirPath(), i -> i.getName().endsWith(".jar"));
        jarList.forEach(i -> {
            try {
                log.debug("添加jar到系统中，jar路径：{}", i.getAbsolutePath());
                JarUtil.addJarClassToSystem(i);
            } catch (Exception e) {
                log.error("添加jar到系统中异常", e);
            }
        });
        log.info("添加jar包到系统完成");
    }

    @Override
    public List<HsfInterfaceInfo> registerServcie(String jarPath) {
        //获取jar中配置的hsf接口
        List<HsfInterfaceInfo> hsfModelList = resolveServiceList(jarPath);
        log.debug("解析jar包中接口完成，接口列表：{}", hsfModelList);
        //发布注册接口
        return hsfHelper.registerService(hsfModelList);
    }

    @Override
    public void saveMethodMockData(String methodFullName, String mockData) {
        String dataFileName = MockUtil.getMethodMockFilePath(methodFullName);
        FileUtil.del(dataFileName);
        FileUtil.writeUtf8String(mockData, dataFileName);
    }

    @Override
    public String getMockDataByMethod(String methodFullPath) {
        return null;
    }

    @Override
    public List<HsfInterfaceInfo> queryServiceList() {
        List<JarInfo> mockJarInfo = MockUtil.getJarInfoList();
        if (CollUtil.isEmpty(mockJarInfo)) {
            return Collections.emptyList();
        }
        List<HsfInterfaceInfo> serviceList = new ArrayList<>();
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
        List<File> servceJarList = FileUtil.loopFiles(new File(mockBaseInfoService.getJarDirPath()), 1,
                i -> i.getName().endsWith(".jar"));
        //注册微服务接口
        servceJarList.forEach(i -> {
            registerServcie(i.getPath());
        });
    }
}
