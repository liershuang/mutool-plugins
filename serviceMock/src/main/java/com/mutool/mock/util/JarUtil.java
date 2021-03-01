package com.mutool.mock.util;

import cn.hutool.core.io.IoUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/1 13:59<br>
 */
public class JarUtil {

    /**
     * 加载jar包到系统
     *
     * @param jarFile
     */
    public static void addJarClassToSystem(File jarFile) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            // 设置方法的访问权限
            method.setAccessible(true);
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
            URL url = jarFile.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取jar包内文件内容
     *
     * @param jarPath
     * @param filePath
     * @return
     */
    public static String readJarFile(String jarPath, String filePath) {
        BufferedReader fileReader = null;
        try {
            if (filePath.startsWith("/")) {
                filePath = filePath.replaceFirst("/", "");
            }
            JarFile jarFile = new JarFile(jarPath);
            JarEntry jarEntry = jarFile.getJarEntry(filePath);
            InputStream in = jarFile.getInputStream(jarEntry);
            fileReader = IoUtil.getUtf8Reader(in);
            return IoUtil.read(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * 遍历目录及子目录下jar包中class列表
     *
     * @param path
     * @return
     */
    public static List<String> listJarClassFilePath(String path) {
        List<String> classList = new ArrayList<String>();
        try {
            listJarClassFilePath(path, classList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }

    /**
     * 遍历扫描目录或者文件(.jar后缀)
     *
     * @param path
     * @throws IOException
     */
    private static void listJarClassFilePath(String path, List<String> classList) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.getName().endsWith(".jar")) {
            classList.addAll(listFileFromJar(file.getCanonicalPath(), (name) -> name.endsWith(".class")));
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                String ph = f.getCanonicalPath();
                listJarClassFilePath(ph, classList);
            }
        }
    }

    /**
     * 读取jar包中文件列表
     *
     * @param jarPath    jar包路径
     * @param fileFilter 文件过滤function，参数为文件名，返回结果为是否过滤
     * @return 文件名列表
     * @throws IOException
     */
    public static List<String> listFileFromJar(String jarPath, Function<String, Boolean> fileFilter) {
        List<String> jarClassNameList = new ArrayList<String>();
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            e.printStackTrace();
            return jarClassNameList;
        }
        Enumeration<JarEntry> enumeration = jarFile.entries();
        String name;
        while (enumeration.hasMoreElements()) {
            JarEntry entry = enumeration.nextElement();
            name = entry.getName();
            if (fileFilter.apply(name)) {
                jarClassNameList.add(name);
            }
        }
        return jarClassNameList;
    }

    public static void main(String[] args) throws IOException {
        String jarPath = "/Users/connie/Public/maven_repository/com/mutool/box/plugin/mock/hsf-mock/0.0.1-SNAPSHOT/hsf-mock-0.0.1-SNAPSHOT.jar";
        System.out.println(listFileFromJar(jarPath, (name) -> name.endsWith(".class")));

        /*String hsf = readJarFile(jarPath, "/hsf-provider-beans.xml");
        System.out.println(hsf);
        Document hsfxml = XmlUtil.readXML(hsf);

        List<Element> eles = XmlUtil.getElements(hsfxml.getDocumentElement(), "hsf:provider");
        for(Element ele : eles){
            System.out.println(ele.getAttribute("interface"));
        }*/
    }

}
