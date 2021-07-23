package com.mutool.mock.service.impl;

import cn.hutool.core.io.FileUtil;
import com.mutool.mock.model.HsfConfigProperties;
import com.mutool.mock.service.UploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public void fileDownload(String fileName, HttpServletResponse response) {
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
}
