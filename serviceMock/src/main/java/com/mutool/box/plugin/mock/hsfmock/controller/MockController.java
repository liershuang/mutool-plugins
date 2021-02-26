package com.mutool.box.plugin.mock.hsfmock.controller;

import com.mutool.box.plugin.mock.hsfmock.util.MockUtil;
import com.mutool.box.plugin.mock.hsfmock.model.HsfInterfaceInfo;
import com.mutool.box.plugin.mock.hsfmock.model.JarInfo;
import com.mutool.box.plugin.mock.hsfmock.model.MethodInfo;
import com.mutool.box.plugin.mock.hsfmock.service.MockService;
import com.mutool.box.plugin.mock.hsfmock.service.UploadService;
import com.mutool.box.plugin.mock.hsfmock.util.ClassUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/1/31 22:19<br>
 */
@Controller
@RequestMapping("mock")
public class MockController {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private MockService mockService;

    @RequestMapping("mockManager")
    public String mockManager() {
        return "mockManager";
    }

    /**
     * 导入服务jar包
     *
     * @return
     * @throws Exception
     */
    //解析jar方法信息，保存到json文件
    // jar位置信息 ：jar名称（实时获取）
    // 类信息 ： jar名称、类class路径、hsfmodel
    // 方法信息：类全限定名、方法列表（方法全限定名、入参（类型、名称）、出参）
    @ResponseBody
    @RequestMapping("importJar")
    public String importJar(MultipartFile file) {
        //保存jar到指定位置
        String jarPath = uploadService.fileUpload(file);
        //下载jar包pom文件中依赖jar
        MockUtil.downloadMavenJar(jarPath);
        //添加jar及其依赖到系统中
        mockService.addJarToServer();

        //注册服务，返回注册成功的service列表
        List<HsfInterfaceInfo> successServceList = mockService.registerServcie(jarPath);

        //保存class对象为mock数据
        JarInfo jarInfo = new JarInfo();
        jarInfo.setJarPath(jarPath);
        jarInfo.setServiceList(successServceList);
        MockUtil.saveServiceMockData(jarInfo);

        return jarPath;
    }

    /**
     * 查询服务列表
     *
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("queryServiceList")
    public List queryServiceList() {
        return mockService.queryServiceList();
    }

    /**
     * 查询接口方法列表
     *
     * @param serviceName
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("queryMethodList")
    public List<MethodInfo> queryMethodList(String serviceName) throws Exception {
        return ClassUtil.getPublicMethods(serviceName);
    }

    /**
     * 查询方法返回结果信息
     *
     * @param methodFullName
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("queryMethodMockData")
    public String queryMethodResult(String methodFullName) throws Exception {
        return MockUtil.getMethodMockData(methodFullName);
    }

    @ResponseBody
    @RequestMapping("updateService")
    public void updateService(HsfInterfaceInfo serviceInfo) throws Exception {
        JarInfo jarInfo = MockUtil.getJarInfoByService(serviceInfo.getInterfaceName());
        if (jarInfo == null) {
            throw new RuntimeException("接口信息不存在");
        }
        jarInfo.getServiceList().removeIf(i -> serviceInfo.getInterfaceName().equals(i.getInterfaceName()));
        jarInfo.getServiceList().add(serviceInfo);
        MockUtil.saveServiceMockData(jarInfo);
    }

    /**
     * 保存方法返回结果json
     *
     * @param methodFullName
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("saveMethodMockData")
    public void saveMethodMockData(String methodFullName, String mockData) throws Exception {
        mockService.saveMethodMockData(methodFullName, mockData);
    }

    /**
     * 导出mock数据
     *
     * @param methodFullName
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("exportMockData")
    public String exportMockData(String methodFullName) throws Exception {
        //jarName为空时查询所有

        return "";
    }

    /**
     * 导入mock数据
     *
     * @param methodFullName
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("importMockData")
    public String importMockData(String methodFullName) throws Exception {
        //jarName为空时查询所有

        return "";
    }


}
