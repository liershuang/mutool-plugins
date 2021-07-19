package com.mutool.mock.service.impl;

import com.mutool.mock.helper.HsfHelper;
import com.mutool.mock.model.HsfServiceInfo;
import com.mutool.mock.service.HsfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 19:15<br>
 */
@Slf4j
@Service
public class HsfServiceImpl implements HsfService {

    @Autowired
    private HsfHelper hsfHelper;

    @Override
    public List<HsfServiceInfo> registerServcie(String jarPath) {
        //获取jar中配置的hsf接口
        List<HsfServiceInfo> hsfModelList = hsfHelper.resolveServiceList(jarPath);
        log.info("解析jar包中接口完成，接口列表：{}", hsfModelList);
        //发布注册接口
        return hsfHelper.registerService(hsfModelList);
    }



}
