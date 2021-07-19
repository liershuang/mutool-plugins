package com.mutool.mock.service;

import com.mutool.mock.model.HsfServiceInfo;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 19:12<br>
 */
public interface HsfService {

    /**
     * 注册hsf接口到注册中心
     * @param jarPath
     * @return 注册成功的接口列表
     */
    List<HsfServiceInfo> registerServcie(String jarPath);



}
