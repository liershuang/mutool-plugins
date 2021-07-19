package com.mutool.mock.service;

import org.springframework.web.multipart.MultipartFile;

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

    void fileDownload(String fileName, HttpServletResponse response);

}
