package com.mutool.mock.mapper;

import com.mutool.mock.bean.model.ServiceApi;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 18:46<br>
 */
public interface ServiceApiMapper {

    List<ServiceApi> queryListByClassNameLike(@Param("className")String className);

    ServiceApi queryByClassName(@Param("className") String className);

    /**
     * 返回自增主键id
     * @param serviceApi
     * @return
     */
    Integer add(ServiceApi serviceApi);

    /**
     * 设置所有接口为离线状态
     * @return
     */
    Integer offlineAllService();

    Integer updateOnlineStatus(@Param("className") String className, @Param("onlineStatus") String onlineStatus);

    Integer updateServiceVersionByClassName(@Param("className") String className, @Param("version") String version);

    Integer updateServiceVersionByServiceId(@Param("serviceId")Integer serviceId, @Param("version")String version);


    void deleteByServiceId(Integer serviceId);

    ServiceApi queryServiceApiById(@Param("serviceId")Integer serviceId);

    Integer batchDelete(@Param("serviceIdList") List<Integer> serviceIdList);

    /**
     * 根据id列表查询列表数据
     * @param idList id列表（可为空，为空时查询所有）
     * @return
     */
    List<ServiceApi> queryServiceList(@Param("idList") List<Integer> idList);
}
