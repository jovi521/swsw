package com.cdyw.swsw.system.dao.fusion12h;

import com.cdyw.swsw.common.domain.entity.fusion12h.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface Fusion12hMapper {

    Map<String, Object> selectNewestTime(@Param("tableName") String tableName);

//    Map<String, Object> selectModifiedNewest(@Param("tableName") String tableName, @Param("startTime") Long startTime,
//                                             @Param("endTime") Long endTime, @Param("type") Integer type,
//                                             @Param("layer") Integer layer, @Param("modifyType") Integer modifyType);

    Map<String, Object> selectModifiedNewest(@Param("type") Integer type, @Param("layer") Integer layer,
                                             @Param("baseTime") Long baseTime, @Param("forecastTime") Long forecastTime);

    Map<String, Object> selectUnmodifiedNewest(@Param("tableName") String tableName, @Param("startTime") Long startTime,
                                               @Param("endTime") Long endTime, @Param("type") Integer type,
                                               @Param("layer") Integer layer, @Param("modifyType") Integer modifyType);

    Fusion12h selectByFilename(@Param("tableName") String tableName, @Param("filename") String filename);

    List<Map<String, Object>> selectDetailInfoNewest();

    Map<String, Object> selectBasetimeNewest();

    Map<String, Object> selectByBasetimeModified(@Param("baseTime") Long baseTime);

    int selectModifytype(@Param("baseTime") Long baseTime);

    int updateNewestfile(@Param("baseTime") Long baseTime, @Param("forecastTime") Long forecastTime,
                         @Param("type") Integer type, @Param("layer") Integer layer, @Param("filename") String filename);

    /**
     * 更新 newstfile
     *
     * @param fusion12hNewestFile Fusion12hNewestFile
     * @return size
     */
    int updateNewestFileByEntity(@Param("fusion12hNewestFile") Fusion12hNewestFile fusion12hNewestFile);

    int updateModifytype(@Param("baseTime") Long baseTime, @Param("forecastCount") Integer forecastCount,
                         @Param("type") Integer type, @Param("layer") Integer layer, @Param("modifyType") Integer modifyType);

    /**
     * 更新 Fusion12hModified
     *
     * @param fusion12hModified Fusion12hModified
     * @return size
     */
    int updateModifiedByEntity(@Param("fusion12hModified") Fusion12hModified fusion12hModified);

    int insertFusion12h(@Param("fusion12h") Fusion12h fusion12h, @Param("tablename") String tablename);

    int updateFusion12h(@Param("fusion12h") Fusion12h fusion12h, @Param("tablename") String tablename);

    int updateBasetime(@Param("baseTime") Long baseTime, @Param("modifyType") Integer modifyType);

    /**
     * 根据时刻查询整体的订正状态
     *
     * @param baseTime 起报时间
     * @return Fusion12hBaseTime
     */
    Fusion12hBaseTime selectBaseTimeByBaseTime(@Param("baseTime") Long baseTime);

    /**
     * 根据时间范围直接查询文件
     *
     * @param baseTime 起报时间
     * @return Fusion12hBaseTime
     */
    List<Fusion12hNewestFile> selectNewestFileByTimeRange(@Param("baseTime") Long baseTime);

    /**
     * 根据时刻查询订正状态
     *
     * @param baseTime 起报时间
     * @return Fusion12hModified
     */
    List<Fusion12hModified> selectModifiedByBaseTime(@Param("baseTime") Long baseTime);

    /**
     * 根据条件查询订正状态
     *
     * @param baseTime      起报时间
     * @param type          要素类型
     * @param forecastCount 预报次数
     * @return Fusion12hModified
     */
    List<Fusion12hModified> selectModifiedByPara(@Param("baseTime") Long baseTime, @Param("type") String type, @Param("forecastCount") Integer forecastCount);

    /**
     * 根据时刻查询订正文件
     *
     * @param baseTime 起报时间
     * @return Fusion12hNewestFile
     */
    List<Fusion12hNewestFile> selectNewestFileByBaseTime(@Param("baseTime") Long baseTime);

    /**
     * 根据预报时间、要素类型（高度默认为0）查询整体的订正文件
     *
     * @param forecastTime 预报时间
     * @param type         要素类型
     * @return List<Fusion12hNewestFile>
     */
    List<Fusion12hNewestFile> selectNewestFileByForecastTimeAndType(@Param("forecastTime") Long forecastTime, @Param("type") String type);

    /**
     * 新增 Fusion12hBaseTime
     *
     * @param fusion12hBaseTime Fusion12hBaseTime
     * @return size
     */
    int insertFusion12hBaseTime(@Param("fusion12hBaseTime") Fusion12hBaseTime fusion12hBaseTime);

    /**
     * 根据预报时间查询天气信息
     *
     * @param forecastTime 预报时间
     * @return List<Fusion12hWeatherInformation>
     */
    Fusion12hWeatherInformation selectWeatherInformationByForecastTime(@Param("forecastTime") Long forecastTime);

    /**
     * 新增天气信息
     *
     * @param weatherInformation weatherInformation
     * @return size
     */
    int insertWeatherInformation(@Param("weatherInformation") Fusion12hWeatherInformation weatherInformation);

    /**
     * 根据预报次数、预报时间查询天气信息
     *
     * @param forecastTime  预报时间
     * @param forecastCount 预报次数
     * @return List<Fusion12hWeatherInformation>
     */
    Fusion12hWeatherInformation selectWeatherInformationByParam(@Param("forecastTime") Long forecastTime, @Param("forecastCount") Integer forecastCount, @Param("baseTime") Long baseTime, @Param("type") Integer type, @Param("lonLatIndex") Integer lonLatIndex);

    /**
     * 更新天气情况
     *
     * @param fusion12hWeatherInformation Fusion12hWeatherInformation
     * @return size
     */
    int updateWeatherInformation(@Param("fusion12hWeatherInformation") Fusion12hWeatherInformation fusion12hWeatherInformation);

    /**
     * 根据预报时间、要素类型（高度默认为0）查询解析类
     *
     * @param tableName    表名
     * @param forecastTime 预报时间
     * @param type         要素类型
     * @return Fusion12h
     */
    List<Fusion12h> selectByTimeAndType(@Param("tableName") String tableName, @Param("forecastTime") Long forecastTime, @Param("type") String type);

}
