package com.mutool.box.plugin.mock.hsfmock.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/2/21 14:59<br>
 */
@Data
public class JarInfo implements Serializable {
    private static final long serialVersionUID = -8660268256063344286L;

    /**
     * 所属jar包名称
     */
    private String jarPath;

    /**
     * 微服务接口列表
     */
    private List<HsfInterfaceInfo> serviceList;
}
