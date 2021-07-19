package com.mutool.mock.helper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.mutool.mock.model.HsfConfigProperties;
import com.mutool.mock.constants.MockConstant;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.model.JarInfo;
import com.mutool.mock.model.MethodMsg;
import com.mutool.mock.proxy.MockInvoker;
import com.mutool.mock.service.MockConfigService;
import com.mutool.mock.util.ClassUtil;
import com.mutool.mock.util.JarUtil;
import com.taobao.hsf.app.api.util.HSFApiProviderBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述：hsf帮助类<br>
 * 作者：les<br>
 * 日期：2021/2/20 10:06<br>
 */
@Slf4j
@Component
public class HsfHelper {

    @Autowired
    private MockConfigService<HsfConfigProperties> mockConfigService;

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
            try {
                hsfApiProviderBean.init();
            } catch (Exception e) {
                log.error("接口发布异常，接口为：{}", hsfServiceInfo.getInterfaceName(), e);
                e.printStackTrace();
            }
            succeessServiceList.add(hsfServiceInfo);
        }
        log.info("注册接口服务完成，注册成功接口：{}", succeessServiceList);
        return succeessServiceList;
    }

    public List<HsfServiceInfo> save(List<HsfServiceInfo> hsfModelList) {
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
            HSFApiProviderBean hsfApiProviderBean = new HSFApiProviderBean();
            hsfApiProviderBean.setServiceInterface(hsfServiceInfo.getInterfaceName());
            hsfApiProviderBean.setServiceVersion(hsfServiceInfo.getVersion());
            hsfApiProviderBean.setServiceGroup("HSF");
            //生成代理实现类
            Object serviceProxy = MockInvoker.getInstance(clazz);
            hsfApiProviderBean.setTarget(serviceProxy);
            // 初始化Provider Bean，发布服务
            try {
                hsfApiProviderBean.init();
            } catch (Exception e) {
                log.error("接口发布异常，接口为：{}", hsfServiceInfo.getInterfaceName(), e);
                e.printStackTrace();
            }
            succeessServiceList.add(hsfServiceInfo);
        }
        return succeessServiceList;
    }

    public JarInfo<HsfServiceInfo> getJarInfoByService(String serviceName) {
        return getJarInfoList().stream().filter(i -> {
            if (CollUtil.isEmpty(i.getServiceList())) {
                return false;
            }
            HsfServiceInfo targetJarInfo = i.getServiceList().stream().filter(service -> serviceName.equals(service.getInterfaceName())).findFirst().orElse(null);
            if (targetJarInfo != null) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }

    /**
     * 保存类mock数据
     * @param jarInfo
     */
    public void saveServiceMockData(JarInfo jarInfo) {
        if (jarInfo == null) {
            return;
        }
        //合并接口信息
        mergerJarService(jarInfo);
        //插入前先删除
        deleteJarInfo(jarInfo.getJarPath());
        //追加信息到文件
        appendJarInfo(jarInfo);

        List<HsfServiceInfo> serviceList = jarInfo.getServiceList();
        if (CollUtil.isEmpty(serviceList)) {
            return;
        }
        //保存方法信息到文件
        for (HsfServiceInfo hsfServiceInfo : serviceList) {
            try {
                saveMethodMockData(hsfServiceInfo, false);
            } catch (ClassNotFoundException e) {
                log.error("保存方法数据异常，类信息：{}", hsfServiceInfo, e);
            }
        }
    }

    /**
     * 删除指定jar包信息（全量读取文件内容剔除指定内容后全量保存）
     *
     * @param jarPath
     */
    public void deleteJarInfo(String jarPath) {
        List<JarInfo<HsfServiceInfo>> jarInfoList = getJarInfoList();
        jarInfoList.removeIf(i -> jarPath.equals(i.getJarPath()));
        saveAllJarInfo(jarInfoList);
    }

    public JarInfo mergerJarService(JarInfo<HsfServiceInfo> jarInfo) {
        JarInfo<HsfServiceInfo> existJarInfo = getJarInfoList().stream()
                .filter(i -> i.getJarPath().equals(jarInfo.getJarPath())).findFirst().orElse(null);
        if (existJarInfo == null) {
            return jarInfo;
        }
        Map<String, HsfServiceInfo> existServiceMap = existJarInfo.getServiceList()
                .stream().collect(Collectors.toMap(i -> i.getInterfaceName(), i -> i));
        jarInfo.getServiceList().forEach(i -> {
            HsfServiceInfo existService = existServiceMap.get(i.getInterfaceName());
            if ((StrUtil.isBlank(i.getVersion()) || "1.0.0".equals(i.getVersion()))
                    && StrUtil.isNotBlank(existService.getVersion())) {
                i.setVersion(existService.getVersion());
            }
            if (i.getClientTimeout() == null && existService.getClientTimeout() != null) {
                i.setClientTimeout(existService.getClientTimeout());
            }
        });

        return null;
    }

    public void appendJarInfo(JarInfo jarInfo) {
        List<JarInfo<HsfServiceInfo>> jarInfoList = getJarInfoList();
        jarInfoList.add(jarInfo);
        saveAllJarInfo(jarInfoList);
    }

    private void saveAllJarInfo(List<JarInfo<HsfServiceInfo>> jarInfoList) {
        JSON jarJSON = JSONUtil.parse(jarInfoList, JSONConfig.create().setIgnoreNullValue(false));
        String classDataFile = mockConfigService.getConfig().getDataPath() + MockConstant.CLASS_DATA_FILENAME;
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(jarJSON), classDataFile);
    }

    public JarInfo<HsfServiceInfo> getJarInfoList(String jarPath) {
        List<JarInfo<HsfServiceInfo>> jarInfoList = getJarInfoList();
        return jarInfoList.stream().filter(i -> jarPath.equals(i.getJarPath())).findFirst().orElse(null);
    }

    /**
     * 获取类mock数据
     *
     * @return
     */
    public List<JarInfo<HsfServiceInfo>> getJarInfoList() {
        String classDataFile = mockConfigService.getConfig().getDataPath() + MockConstant.CLASS_DATA_FILENAME;
        if (!FileUtil.exist(classDataFile)) {
            return Collections.EMPTY_LIST;
        }
        String classJson = FileUtil.readUtf8String(classDataFile);
        return JSONUtil.toBean(classJson, new TypeReference<List<JarInfo<HsfServiceInfo>>>() {
        }, false);
    }

    /**
     * 保存方法信息到本地文件
     *
     * @param hsfInterfaceInfo
     * @param coverFlag
     * @throws ClassNotFoundException
     */
    public void saveMethodMockData(HsfServiceInfo hsfInterfaceInfo, boolean coverFlag) throws ClassNotFoundException {
        List<MethodMsg> classMethodList = ClassUtil.getPublicMethods(hsfInterfaceInfo.getInterfaceName());
        if (CollUtil.isEmpty(classMethodList)) {
            return;
        }
        classMethodList.forEach(i -> {
            try {
                String dataFileName = mockConfigService.getMethodMockDataFilePath(i.getMethodFullName());
                //覆盖则先删除文件再新建
                if (coverFlag) {
                    FileUtil.del(dataFileName);
                }
                boolean dataExist = FileUtil.exist(dataFileName);
                //数据不存在或要求覆盖则设置方法初始mock数据
                if (!dataExist || coverFlag) {
                    //方法参数转为json，保存到文件
                    if ("void".equals(i.getReturnClass())) {
                        return;
                    }
//                    String returnClassJson = turnReturnClassToJson(Class.forName(i.getReturnClass()));
                    String returnClassJson = "";
                    FileUtil.writeUtf8String(returnClassJson, dataFileName);
                }
            } catch (Exception e) {
                log.error("保存方法数据异常，方法：{}", i, e);
            }
        });
    }

    /**
     * 获取方法mock数据
     *
     * @param methodFullName
     * @return
     */
    public String getMethodMockData(String methodFullName) {
        String dataFileName = mockConfigService.getMethodMockDataFilePath(methodFullName);
        if (!FileUtil.exist(dataFileName)) {
            return "";
        }
        return FileUtil.readUtf8String(dataFileName);
    }

    /**
     * class对象转json字符串
     *
     * @param clazz
     * @return
     */
    //问题解决：返回class对象为list时无法实例化
    public String turnReturnClassToJson(Class clazz) throws IllegalAccessException, InstantiationException {
        Object returnObj = clazz.newInstance();
        if (!StrUtil.isBlank(String.valueOf(returnObj))) {
            //转json，设置空值不忽略
            JSON returnJSON = JSONUtil.parse(returnObj, JSONConfig.create().setIgnoreNullValue(false));
            return JSONUtil.toJsonStr(returnJSON);
        }
        return "";
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
