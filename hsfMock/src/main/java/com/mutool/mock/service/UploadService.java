package com.mutool.mock.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/5 16:21<br>
 */
public interface UploadService {

    /**
     * 上传文件
     *
     * @param file
     * @return 文件路径
     */
    String fileUpload(MultipartFile file);

    /**
     * 下载jar包文件
     * @param fileName jar文件名
     * @param response
     */
    void downloadJarFile(String fileName, HttpServletResponse response);

    /**
     * 下载文件内容
     * @param fileName 文件名
     * @param data 文件内容
     * @param response
     */
    void downloadFile(String fileName, String data, HttpServletRequest request, HttpServletResponse response);

}
