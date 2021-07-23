package com.mutool.mock.controller;

import cn.hutool.core.collection.ListUtil;
import com.mutool.core.exception.ResultBody;
import com.mutool.mock.bean.model.MethodInfo;
import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.enums.YnEnum;
import com.mutool.mock.helper.MockHelper;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.service.MockService;
import com.mutool.mock.service.ServiceApiService;
import com.mutool.mock.util.MavenUtil;
import com.mutool.mock.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private MockHelper mockHelper;
    @Autowired
    private MockService mockService;
    @Autowired
    private ServiceApiService serviceApiService;

    /**
     * todo 1、方法名称筛选无效排查
     * todo 2、方法批量删除无效问题排查
     * todo 3、集成框架平台
     * todo 4、增加设置菜单，配置mavensettings路径
     * todo 5、集成菜单自定义功能，先取数据库菜单数据再取各系统自定义菜单，自定义菜单优先展示
     * todo 6、增加菜单功能说明，每个菜单一个问好按钮，点击弹出使用说明
     */

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
        MavenUtil.downloadMavenJar(jarPath);
        //添加jar及其依赖到系统中
        mockHelper.addJarToServer();
        //解析保存jar包中接口及方法信息
        mockHelper.analyseAndSaveServiceFromJar(jarPath);
        //注册接口
        List<HsfServiceInfo> successServiceList = mockHelper.registerServcie(jarPath);
        mockHelper.batchSetOnlineStatus(successServiceList, YnEnum.YES);
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
    public List<ServiceApi> queryServiceList(String className) {
        return mockService.queryServiceList(className);
    }

    @ResponseBody
    @RequestMapping("queryMethodList")
    public List<MethodInfo> queryMethodList(Integer serviceId){
        return mockService.queryMethodsByServiceId(serviceId);
    }

    /**
     * 查询方法返回结果信息
     *
     * @param methodId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("queryMethodMockData")
    public ResultBody<String> queryMethodResult(Integer methodId) throws Exception {
        return new ResultBody(mockService.queryMockDataByMethodId(methodId));
    }

    @ResponseBody
    @RequestMapping("updateServiceVersion")
    public void updateServiceVersion(Integer serviceId, String version) throws Exception {
        mockService.updateServiceVersion(serviceId, version);
    }

    /**
     * 保存方法返回结果json
     *
     * @param methodId
     * @param  mockData
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("setMockData")
    public void saveMethodMockData(Integer methodId, String mockData) throws Exception {
        mockService.setMockData(methodId, mockData);
    }

    @ResponseBody
    @RequestMapping("deleteService")
    public void deleteService(Integer serviceId) throws Exception {
        mockService.deleteService(serviceId);
    }

    @ResponseBody
    @RequestMapping("batchDelteService")
    public void batchDelteService(String serviceIds){
        List<Integer> serviceIdList = ListUtil.toList(serviceIds.split(",")).stream()
                .map(i -> Integer.parseInt(i)).collect(Collectors.toList());
        serviceApiService.batchDelete(serviceIdList);
    }

    @ResponseBody
    @RequestMapping("deleteMethod")
    public void deleteMethod(Integer methodId) throws Exception {
        mockService.deleteMethod(methodId);
    }

    /**
     * 导出mock数据
     *
     * @param methodId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping("exportMockData")
    public void exportMockData(Integer methodId) throws Exception {
        String mockData = mockService.queryMockDataByMethodId(methodId);
        //todo 下载数据文件
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
