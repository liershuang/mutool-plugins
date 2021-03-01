package com.mutool.mock.controller;

import com.mutool.core.exception.ResultBody;
import com.mutool.mock.util.MockUtil;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.model.JarInfo;
import com.mutool.mock.model.MethodInfo;
import com.mutool.mock.service.MockService;
import com.mutool.mock.service.UploadService;
import com.mutool.mock.util.ClassUtil;
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
        List<HsfServiceInfo> successServceList = mockService.registerServcie(jarPath);

        //保存class对象为mock数据
        JarInfo jarInfo = new JarInfo();
        jarInfo.setJarPath(jarPath);
        jarInfo.setServiceList(successServceList);
        mockService.saveServiceMockData(jarInfo);

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
    public ResultBody<String> queryMethodResult(String methodFullName) throws Exception {
        return new ResultBody(mockService.getMockDataByMethod(methodFullName));
    }

    @ResponseBody
    @RequestMapping("updateService")
    public void updateService(HsfServiceInfo serviceInfo) throws Exception {
        mockService.updateServiceInfo(serviceInfo);
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
