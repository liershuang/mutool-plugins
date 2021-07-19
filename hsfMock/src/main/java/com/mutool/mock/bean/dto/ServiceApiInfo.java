package com.mutool.mock.bean.dto;

import com.mutool.mock.bean.model.MethodInfo;
import lombok.Data;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 19:05<br>
 */
@Data
public class ServiceApiInfo {

    private Integer id;
    /**
     * 类class路径
     */
    private String className;


    private String version;

    /**
     * 在线状态，1：在线，0：离线
     */
    private String onlineStatus;

    List<MethodInfo> methodList;

}
