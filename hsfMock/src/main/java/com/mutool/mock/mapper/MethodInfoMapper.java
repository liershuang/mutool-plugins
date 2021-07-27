package com.mutool.mock.mapper;

import com.mutool.mock.bean.dto.MethodMock;
import com.mutool.mock.bean.model.MethodInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述：<br>
 * 作者：les<br>
 * 日期：2021/7/14 18:50<br>
 */
public interface MethodInfoMapper {

    Integer add(MethodInfo methodInfo);

    /**
     * 根据serviceId查询方法列表
     * @param serviceId
     * @return
     */
    List<MethodInfo> queryByServiceId(@Param("serviceId") Integer serviceId);

    List<MethodMock> queryMethodDataByServiceId(@Param("serviceId") Integer serviceId);

    /**
     * 根据类名
     * @param className
     * @return
     */
    List<MethodInfo> queryList(@Param("className") String className);

    /**
     * 根据方法id查询方法mock数据
     * @param methodId
     * @return
     */
    String queryMockDataByMethodId(@Param("methodId")Integer methodId);

    void setMockDataByMethodId(@Param("methodId") Integer methodId, @Param("mockData") String mockData);

    void setMockDataByMethodFullName(@Param("methodFullName") String methodFullName, @Param("mockData") String mockData);

    void deleteByServiceId(Integer serviceId);

    void deleteByMethodId(Integer methodId);

    MethodInfo queryByMethodFullName(@Param("methodFullName") String methodFullName);

    void batchDeleteByServiceId(@Param("serviceIdList") List<Integer> serviceIdList);

    Integer batchDelete(@Param("idList") List<Integer> idList);
}
