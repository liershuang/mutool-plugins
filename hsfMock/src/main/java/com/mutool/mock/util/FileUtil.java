package com.mutool.mock.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/26 22:57<br>
 */
public class FileUtil extends cn.hutool.core.io.FileUtil {


    public static String readMultipartFile(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder sb = new StringBuilder();

            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "/n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }
    }

}
