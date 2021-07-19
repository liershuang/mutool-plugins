package com.mutool.mock.service;

import com.mutool.mock.bean.model.ServiceApi;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/16 19:56<br>
 */
public interface ServiceApiService {

    Integer addServiceInfo(ServiceApi serviceApi);

    Integer batchDelete(List<Integer> serviceIdList);

}
