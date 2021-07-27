package com.mutool.mock.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mutool.core.exception.BizException;
import com.mutool.core.exception.ResultBody;
import com.mutool.mock.bean.dto.ServiceMock;
import com.mutool.mock.bean.model.MethodInfo;
import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.enums.YnEnum;
import com.mutool.mock.helper.MockHelper;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.service.MethodInfoService;
import com.mutool.mock.service.ServiceApiService;
import com.mutool.mock.service.UploadService;
import com.mutool.mock.util.FileUtil;
import com.mutool.mock.util.MavenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/1/31 22:19<br>
 */
@Slf4j
@Validated
@Controller
@RequestMapping("mock")
public class MockController {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private MockHelper mockHelper;
    @Autowired
    private MethodInfoService methodInfoService;
    @Autowired
    private ServiceApiService serviceApiService;

    /**
     * todo 1、方法名称筛选无效排查
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

    @ResponseBody
    @RequestMapping("queryServiceById")
    public ServiceApi queryServiceById(Integer id) {
        return serviceApiService.queryServiceById(id);
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
        return serviceApiService.queryServiceList(className);
    }

    @ResponseBody
    @RequestMapping("queryMethodList")
    public List<MethodInfo> queryMethodList(Integer serviceId){
        return methodInfoService.queryMethodsByServiceId(serviceId);
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
        return new ResultBody(methodInfoService.queryMockDataByMethodId(methodId));
    }

    @ResponseBody
    @RequestMapping("updateServiceVersion")
    public void updateServiceVersion(Integer serviceId, String version) throws Exception {
        serviceApiService.updateServiceVersion(serviceId, version);
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
    public void saveMethodMockData(@NotNull(message = "方法id不能为空") Integer methodId, String mockData) {
        methodInfoService.setMockData(methodId, mockData);
    }

    @ResponseBody
    @RequestMapping("deleteService")
    public void deleteService(Integer serviceId) throws Exception {
        serviceApiService.deleteService(serviceId);
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
        methodInfoService.deleteMethod(methodId);
    }

    @ResponseBody
    @RequestMapping("batchDelteMethod")
    public void batchDelteMethod(String delIds){
        List<Integer> idList = ListUtil.toList(delIds.split(",")).stream()
                .map(i -> Integer.parseInt(i)).collect(Collectors.toList());
        methodInfoService.batchDelete(idList);
    }

    /**
     * 导出mock数据
     * @param serviceIds serviceId列表
     * @param response
     */
    @ResponseBody
    @RequestMapping("exportMockData")
    public void exportMockData(String serviceIds, HttpServletRequest request, HttpServletResponse response) {
        List<Integer> idList = Collections.emptyList();
        if(StrUtil.isNotBlank(serviceIds)){
            idList = ListUtil.toList(serviceIds.split(",")).stream()
                    .map(i -> Integer.parseInt(i)).collect(Collectors.toList());
        }
        List<ServiceMock> serviceMockDataList = serviceApiService.queryServiceMockData(idList);
        String jsonData = JSONUtil.toJsonStr(serviceMockDataList);
        uploadService.downloadFile("hsf-mock数据.json", jsonData, request, response);
    }

    /**
     * 导入mock数据
     * @param file
     */
    @ResponseBody
    @RequestMapping("importMockData")
    public void importMockData(MultipartFile file) {
        String fileContent = "";
        try {
            fileContent = FileUtil.readMultipartFile(file);
        } catch (IOException e) {
            log.error("文件内容读取失败", e);
            throw new BizException("文件内容读取失败");
        }
        List<ServiceMock> serviceMockList = JSONUtil.toBean(fileContent, new TypeReference<List<ServiceMock>>() {}, true);
        serviceApiService.importMockData(serviceMockList);
    }


}
