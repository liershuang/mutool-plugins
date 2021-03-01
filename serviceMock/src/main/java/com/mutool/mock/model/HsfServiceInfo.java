package com.mutool.mock.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/3 19:33<br>
 */
@Data
public class HsfServiceInfo implements Serializable {
    private static final long serialVersionUID = -6155581818015290476L;

    /**
     * 接口servce id
     */
    private String id;

    /**
     * 接口全限定
     */
    private String interfaceName;

    /**
     * 接口实现引用
     */
    private String ref;

    /**
     * 版本号
     */
    private String version = "1.0.0";

    /**
     * 超时时间
     */
    private Integer clientTimeout;

}
