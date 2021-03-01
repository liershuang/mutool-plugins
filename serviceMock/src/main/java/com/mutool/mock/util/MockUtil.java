package com.mutool.mock.util;

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
import com.mutool.mock.constants.ConfigConstant;
import com.mutool.mock.constants.MockConstant;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.model.JarInfo;
import com.mutool.mock.model.MethodInfo;
import com.mutool.mock.service.MockConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;

import java.io.File;
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

    /**
     * 下载jar包中pom文件依赖jar包
     * @param jarPath
     */
    public static void downloadMavenJar(String jarPath) {
        log.info("开始下载maven依赖包，jar路径：{}", jarPath);
        String jarDir = StrUtil.sub(jarPath, 0, jarPath.lastIndexOf(File.separator));
        //读取jar中pom文件内容
        List<String> pomFilePath = JarUtil.listFileFromJar(jarPath, i -> i.endsWith("pom.xml"));
        if (CollUtil.isEmpty(pomFilePath)) {
            return;
        }
        String pomContent = JarUtil.readJarFile(jarPath, pomFilePath.get(0));
        File pomFile = new File(jarDir + "/" + RandomUtil.randomNumbers(3) + ".xml");
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
        invoker.setLocalRepositoryDirectory(new File(jarDir));

        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }

        pomFile.delete();
        log.info("下载maven依赖jar包完成");
    }

    /**
     * 添加jar到系统中
     * @param jarDir jar包目录
     */
    public static void addJarToServer(String jarDir) {
        //将jar路径下所有文件加载到内存中
        List<File> jarList = FileUtil.loopFiles(jarDir, i -> i.getName().endsWith(".jar"));
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


}
