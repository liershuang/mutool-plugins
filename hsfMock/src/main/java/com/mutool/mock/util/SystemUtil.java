package com.mutool.mock.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 19:24<br>
 */
@Slf4j
public class SystemUtil {

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
