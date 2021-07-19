package com.mutool.mock.bean.model;

import lombok.Data;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 18:38<br>
 */
@Data
public class ServiceApi {

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

}
