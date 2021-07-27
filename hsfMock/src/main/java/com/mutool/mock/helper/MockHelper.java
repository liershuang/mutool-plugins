package com.mutool.mock.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.mutool.mock.bean.model.ServiceApi;
import com.mutool.mock.constants.MockConstant;
import com.mutool.mock.enums.YnEnum;
import com.mutool.mock.mapper.ServiceApiMapper;
import com.mutool.mock.model.HsfConfigProperties;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.service.MethodInfoService;
import com.mutool.mock.service.ServiceApiService;
import com.mutool.mock.util.JarUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/25 13:19<br>
 */
@Slf4j
@Component
public class MockHelper {

    @Autowired
    private HsfHelper hsfHelper;
    @Autowired
    private ServiceApiService serviceApiService;
    @Autowired
    private MethodInfoService methodInfoService;
    @Autowired
    private ServiceApiMapper serviceApiMapper;
    @Autowired
    private HsfConfigProperties hsfConfigProperties;

    /**
     * 注册jar包到注册中心
     * @param jarPath
     * @return
     */
    public List<HsfServiceInfo> registerServcie(String jarPath) {
        //获取jar中配置的hsf接口
        List<HsfServiceInfo> hsfModelList = hsfHelper.resolveServiceList(jarPath);
        //发布注册接口
        return hsfHelper.registerService(hsfModelList);
    }

    /**
     * 加载所有本地jar包（加载到系统、注册接口）
     */
    public void loadAllLocalService() {
        //遍历所有jar加载到内存中
        addJarToServer();
        //过滤jar包直接目录下jar并解析其中service接口作为微服务接口，因直接目录下都是导入的jar
        List<File> servceJarList = FileUtil.loopFiles(new File(hsfConfigProperties.getJarPath()), 1,
                i -> i.getName().endsWith(".jar"));
        //设置所有接口状态为离线
        serviceApiMapper.offlineAllService();
        //注册微服务接口
        servceJarList.forEach(i -> {
            List<HsfServiceInfo> successServiceList = hsfHelper.registerServcie(i.getPath());
            batchSetOnlineStatus(successServiceList, YnEnum.YES);
        });
    }

    /**
     * 批量设置在线状态
     * @param serviceList
     * @param ynEnum
     */
    public void batchSetOnlineStatus(List<HsfServiceInfo> serviceList, YnEnum ynEnum){
        if(CollUtil.isEmpty(serviceList)){
            return;
        }
        serviceList.forEach(service -> serviceApiMapper.updateOnlineStatus(service.getInterfaceName(), ynEnum.getCode()));
    }

    public void addJarToServer(){
        JarUtil.addJarToServer(hsfConfigProperties.getJarPath());
    }

    /**
     * 从jar包中分析保存接口
     * @param jarPath
     */
    public void analyseAndSaveServiceFromJar(String jarPath){
        List<HsfServiceInfo> hsfServiceList = hsfHelper.resolveServiceList(jarPath);
        for(HsfServiceInfo hsfInfo : hsfServiceList){
            ServiceApi serviceApi = serviceApiMapper.queryByClassName(hsfInfo.getInterfaceName());
            if(serviceApi == null){
                ServiceApi addService = new ServiceApi();
                addService.setClassName(hsfInfo.getInterfaceName());
                addService.setVersion(StrUtil.isBlank(hsfInfo.getVersion())?MockConstant.HSF_DEFAULT_VERSION:hsfInfo.getVersion());
                addService.setOnlineStatus(YnEnum.YES.getCode());
                Integer serviceId = serviceApiService.addServiceInfo(addService);
                try {
                    //保存方法信息
                    methodInfoService.addServiceMethodInfo(serviceId);
                } catch (Exception e) {
                    log.error("方法保存异常，接口id："+serviceId, e);
                }
            }else{
                if(StrUtil.isBlank(hsfInfo.getVersion())){
                    continue;
                }
                //数据库版本号为空或入参版本号不为默认版本号则进行更新
                if(StrUtil.isBlank(serviceApi.getVersion())
                        || !MockConstant.HSF_DEFAULT_VERSION.equals(hsfInfo.getVersion())){
                    serviceApiMapper.updateServiceVersionByClassName(hsfInfo.getInterfaceName(), hsfInfo.getVersion());
                }
            }
        }
    }

    public void saveServiceInfo(List<HsfServiceInfo> hsfServiceList){
        if(CollUtil.isEmpty(hsfServiceList)){
            return;
        }
        for(HsfServiceInfo hsfInfo : hsfServiceList){
            ServiceApi serviceApi = serviceApiMapper.queryByClassName(hsfInfo.getInterfaceName());
            if(serviceApi == null){
                ServiceApi addService = new ServiceApi();
                addService.setClassName(hsfInfo.getInterfaceName());
                addService.setVersion(StrUtil.isBlank(hsfInfo.getVersion())?MockConstant.HSF_DEFAULT_VERSION:hsfInfo.getVersion());
                addService.setOnlineStatus(YnEnum.YES.getCode());
                Integer serviceId = serviceApiService.addServiceInfo(addService);
                try {
                    //保存方法信息
                    methodInfoService.addServiceMethodInfo(serviceId);
                } catch (Exception e) {
                    log.error("方法保存异常，接口id："+serviceId, e);
                }
            }else{
                if(StrUtil.isBlank(hsfInfo.getVersion())){
                    continue;
                }
                //数据库版本号为空或入参版本号不为默认版本号则进行更新
                if(StrUtil.isBlank(serviceApi.getVersion())
                        || !MockConstant.HSF_DEFAULT_VERSION.equals(hsfInfo.getVersion())){
                    serviceApiMapper.updateServiceVersionByClassName(hsfInfo.getInterfaceName(), hsfInfo.getVersion());
                }
            }
        }
    }




}
