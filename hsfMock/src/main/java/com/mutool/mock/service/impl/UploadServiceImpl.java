package com.mutool.mock.service.impl;

import cn.hutool.core.io.FileUtil;
import com.mutool.mock.model.HsfConfigProperties;
import com.mutool.mock.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/5 16:25<br>
 */
@Slf4j
@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private HsfConfigProperties hsfConfigProperties;

    @Override
    public String fileUpload(MultipartFile file) {
        String fileUploadPath = hsfConfigProperties.getJarPath();
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("未选择需上传的文件");
        }
        System.out.println(file.getName());
        if (!file.getOriginalFilename().endsWith(".jar")) {
            throw new RuntimeException("不支持的文件类型");
        }
        if (!FileUtil.exist(fileUploadPath)) {
            FileUtil.mkdir(fileUploadPath);
        }

        File fileUpload = new File(fileUploadPath, file.getOriginalFilename());
        if (fileUpload.exists()) {
            log.warn("文件已存在，文件：{}", fileUpload.getPath());
            return fileUpload.getPath();
        }
        try {
            file.transferTo(fileUpload);
        } catch (IOException e) {
            throw new RuntimeException("上传文件到异常：" + e.toString());
        }
        log.info("文件上传完成，文件路径：{}", fileUpload.getPath());
        return fileUpload.getPath();
    }

    @Override
    public void downloadJarFile(String fileName, HttpServletResponse response) {
        File file = new File(hsfConfigProperties.getJarPath() + fileName);
        if (!file.exists()) {
            throw new RuntimeException(fileName + "文件不存在");
        }
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);

        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            log.error("文件下载异常", e);
        }
    }

    /**
     * 下载文件内容
     *
     * @param fileName 文件名
     * @param data     文件内容
     * @param response
     */
    @Override
    public void downloadFile(String fileName, String data, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/force-download");
        response.addHeader("Content-Disposition", "attachment;fileName=" + encodeFileName(fileName, request));

        byte[] buffer = new byte[1024];
        try (InputStream is =new ByteArrayInputStream(data.getBytes("UTF-8"));
             BufferedInputStream bis = new BufferedInputStream(is)) {
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            log.error("文件下载异常", e);
        }
    }

    /**
     * 根据不同浏览器相应编码处理
     *
     * @param fileName
     * @param request
     * @return
     */
    private static String encodeFileName(String fileName, HttpServletRequest request) {
        String codedFilename = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            // ie浏览器及Edge浏览器
            boolean isIe = (null != agent && -1 != agent.indexOf("MSIE"))
                    || (null != agent && -1 != agent.indexOf("Trident"))
                    || (null != agent && -1 != agent.indexOf("Edge"));
            if (isIe) {
                codedFilename = java.net.URLEncoder.encode(fileName, "UTF-8");
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {
                // 火狐,Chrome等浏览器
                codedFilename = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
        } catch (Exception e) {
            log.error("编码文件名失败", e);
        }
        return codedFilename;
    }
}
