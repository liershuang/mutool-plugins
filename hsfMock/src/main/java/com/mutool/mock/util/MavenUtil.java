package com.mutool.mock.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.system.SystemUtil;
import com.mutool.commonweb.domain.settings.util.SysConfigUtil;
import com.mutool.mock.constants.ConfigConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 19:23<br>
 */
@Slf4j
public class MavenUtil {

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
        String defaultSettingsFilePath = SystemUtil.getUserInfo().getHomeDir()+"/.m2/settings.xml";
        String settingsFilePath = SysConfigUtil.getConfigByKey(ConfigConstant.KEY_SETTINGS_FILE_PATH, defaultSettingsFilePath);
        if (StrUtil.isNotBlank(settingsFilePath)) {
            request.setUserSettingsFile(new File(settingsFilePath));
        }
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(SystemUtil.get("MAVEN_HOME")));
        invoker.setLocalRepositoryDirectory(new File(jarDir));

        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            log.error("pom依赖下载出错，请检查日志或到系统配置中配置settings文件地址，" +
                    "key为："+ConfigConstant.KEY_SETTINGS_FILE_PATH, e);
            //throw new BizException("pom依赖下载出错，请检查日志或到系统配置中配置settings文件地址，" +
            //        "key为："+ConfigConstant.KEY_SETTINGS_FILE_PATH);
        }
        pomFile.delete();
        log.info("下载maven依赖jar包完成");
    }

}
