package com.mutool.box.plugin.mock.hsfmock.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.mutool.box.plugin.mock.hsfmock.constants.ConfigConstant;
import com.mutool.box.plugin.mock.hsfmock.constants.MockConstant;
import com.mutool.box.plugin.mock.hsfmock.model.HsfInterfaceInfo;
import com.mutool.box.plugin.mock.hsfmock.model.JarInfo;
import com.mutool.box.plugin.mock.hsfmock.model.MethodInfo;
import com.mutool.box.plugin.mock.hsfmock.service.MockBaseInfoService;
import com.mutool.box.plugin.mock.hsfmock.util.ClassUtil;
import com.mutool.box.plugin.mock.hsfmock.util.JarUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述：mock帮助类<br>
 * 作者：les<br>
 * 日期：2021/2/4 13:37<br>
 */
@Slf4j
public final class MockUtil {

    private static MockBaseInfoService mockBaseInfoService = null;

    static {
        mockBaseInfoService = SpringUtil.getBean(MockBaseInfoService.class);
    }

    /**
     * 获取方法mock数据文件名
     *
     * @param methodFullName
     * @return
     */
    public static String getMethodMockFilePath(String methodFullName) {
        return mockBaseInfoService.getMockDataPath() + "methodData/" + methodFullName + ".json";
    }

    public static JarInfo getJarInfoByService(String serviceName) {
        return getJarInfoList().stream().filter(i -> {
            if (CollUtil.isEmpty(i.getServiceList())) {
                return false;
            }
            HsfInterfaceInfo targetJarInfo = i.getServiceList().stream().filter(service -> serviceName.equals(service.getInterfaceName())).findFirst().orElse(null);
            if (targetJarInfo != null) {
                return true;
            }
            return false;
        }).findFirst().orElse(null);
    }

    /**
     * 保存类mock数据
     *
     * @param jarInfo
     */
    public static void saveServiceMockData(JarInfo jarInfo) {
        if (jarInfo == null) {
            return;
        }
        //合并接口信息
        mergerJarService(jarInfo);
        //插入前先删除
        deleteJarInfo(jarInfo.getJarPath());
        //追加信息到文件
        appendJarInfo(jarInfo);

        List<HsfInterfaceInfo> serviceList = jarInfo.getServiceList();
        if (CollUtil.isEmpty(serviceList)) {
            return;
        }
        //保存方法信息到文件
        for (HsfInterfaceInfo hsfInterfaceInfo : serviceList) {
            try {
                saveMethodMockData(hsfInterfaceInfo, false);
            } catch (ClassNotFoundException e) {
                log.error("保存方法数据异常，类信息：{}", hsfInterfaceInfo, e);
            }
        }
    }

    /**
     * 删除指定jar包信息（全量读取文件内容剔除指定内容后全量保存）
     *
     * @param jarPath
     */
    @Deprecated
    public static void deleteJarInfo(String jarPath) {
        List<JarInfo> jarInfoList = getJarInfoList();
        jarInfoList.removeIf(i -> jarPath.equals(i.getJarPath()));
        saveAllJarInfo(jarInfoList);
    }

    public static JarInfo mergerJarService(JarInfo jarInfo) {
        JarInfo existJarInfo = getJarInfoList().stream().filter(i -> i.getJarPath().equals(jarInfo.getJarPath())).findFirst().orElse(null);
        if (existJarInfo == null) {
            return jarInfo;
        }
        Map<String, HsfInterfaceInfo> existServiceMap = existJarInfo.getServiceList()
                .stream().collect(Collectors.toMap(i -> i.getInterfaceName(), i -> i));
        jarInfo.getServiceList().forEach(i -> {
            HsfInterfaceInfo existService = existServiceMap.get(i.getInterfaceName());
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

    public static void appendJarInfo(JarInfo jarInfo) {
        List<JarInfo> jarInfoList = getJarInfoList();
        jarInfoList.add(jarInfo);
        saveAllJarInfo(jarInfoList);
    }

    private static void saveAllJarInfo(List<JarInfo> jarInfoList) {
        JSON jarJSON = JSONUtil.parse(jarInfoList, JSONConfig.create().setIgnoreNullValue(false));
        String classDataFile = mockBaseInfoService.getMockDataPath() + MockConstant.CLASS_DATA_FILENAME;
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(jarJSON), classDataFile);
    }

    public static JarInfo getJarInfoList(String jarPath) {
        List<JarInfo> jarInfoList = getJarInfoList();
        return jarInfoList.stream().filter(i -> jarPath.equals(i.getJarPath())).findFirst().orElse(null);
    }

    /**
     * 获取类mock数据
     *
     * @return
     */
    public static List<JarInfo> getJarInfoList() {
        String classDataFile = mockBaseInfoService.getMockDataPath() + MockConstant.CLASS_DATA_FILENAME;
        if (!FileUtil.exist(classDataFile)) {
            return Collections.EMPTY_LIST;
        }
        String classJson = FileUtil.readUtf8String(classDataFile);
        return JSONUtil.toBean(classJson, new TypeReference<List<JarInfo>>() {
        }, false);
    }

    /**
     * 保存方法信息到本地文件
     *
     * @param hsfInterfaceInfo
     * @param coverFlag
     * @throws ClassNotFoundException
     */
    public static void saveMethodMockData(HsfInterfaceInfo hsfInterfaceInfo, boolean coverFlag) throws ClassNotFoundException {
        List<MethodInfo> classMethodList = ClassUtil.getPublicMethods(hsfInterfaceInfo.getInterfaceName());
        if (CollUtil.isEmpty(classMethodList)) {
            return;
        }
        classMethodList.forEach(i -> {
            try {
                String dataFileName = getMethodMockFilePath(i.getMethodFullName());
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
    public static String getMethodMockData(String methodFullName) {
        String dataFileName = getMethodMockFilePath(methodFullName);
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
    public static String turnReturnClassToJson(Class clazz) throws IllegalAccessException, InstantiationException {
        Object returnObj = clazz.newInstance();
        if (!StrUtil.isBlank(String.valueOf(returnObj))) {
            //转json，设置空值不忽略
            JSON returnJSON = JSONUtil.parse(returnObj, JSONConfig.create().setIgnoreNullValue(false));
            return JSONUtil.toJsonStr(returnJSON);
        }
        return "";
    }

    /**
     * 下载jar包中pom文件依赖jar包
     *
     * @param jarPath
     */
    public static void downloadMavenJar(String jarPath) {
        log.info("开始下载maven依赖包");
        //读取jar中pom文件内容
        List<String> pomFilePath = JarUtil.listFileFromJar(jarPath, i -> i.endsWith("pom.xml"));
        if (CollUtil.isEmpty(pomFilePath)) {
            return;
        }
        String pomContent = JarUtil.readJarFile(jarPath, pomFilePath.get(0));
        File pomFile = new File(mockBaseInfoService.getJarDirPath() + "/" + RandomUtil.randomNumbers(3) + ".xml");
        FileUtil.writeUtf8String(pomContent, pomFile);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pomFile);
        request.setGoals(Collections.singletonList("compile"));
        //设置settings文件
        String settingsFilePath = ConfigUtil.get(ConfigConstant.KEY_SETTINGS_FILE_PATH);
        if (StrUtil.isNotBlank(settingsFilePath)) {
            request.setUserSettingsFile(new File(settingsFilePath));
        }
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(SystemUtil.get("MAVEN_HOME")));
        invoker.setLocalRepositoryDirectory(new File(mockBaseInfoService.getJarDirPath()));

        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }

        pomFile.delete();
        log.info("下载maven依赖jar包完成");
    }


}
