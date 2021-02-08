package com.cdyw.swsw.system.dao.common;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface CommonDataMapper {

    /**
     * 数据监控-包含分子和分母
     *
     * @param tableName String
     * @return Map
     */
    Map<String, Object> getDataMonitorAtSta(@Param("tableName") String tableName);

    /**
     * 数据监控-仅包含状态
     *
     * @param tableName String
     * @return Map
     */
    Map<String, Object> getDataMonitorSate(@Param("tableName") String tableName);

    /**
     * 数据监控-仅包含状态
     *
     * @param tableName String
     * @return Map
     */
    Map<String, Object> getDataMonitorRadarWea(@Param("tableName") String tableName);

    /**
     * 获取检测报警的数据：阈值以上tem
     *
     * @param tableName       String
     * @param tableNameBefore String
     * @param startTime       String
     * @param endTime         String
     * @return Map
     */
    List<Map<String, Object>> getDataMonitorAlarmMaxTem(@Param("tableNameBefore") String tableNameBefore, @Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取检测报警的数据：阈值以上pre
     *
     * @param tableName       String
     * @param tableNameBefore String
     * @param startTime       String
     * @param endTime         String
     * @return Map
     */
    List<Map<String, Object>> getDataMonitorAlarmMaxPre(@Param("tableNameBefore") String tableNameBefore, @Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取检测报警的数据：阈值以上win
     *
     * @param tableName       String
     * @param tableNameBefore String
     * @param startTime       String
     * @param endTime         String
     * @return Map
     */
    List<Map<String, Object>> getDataMonitorAlarmMaxWin(@Param("tableNameBefore") String tableNameBefore, @Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取检测报警的数据：阈值以下tem
     *
     * @param tableName       String
     * @param tableNameBefore String
     * @param startTime       String
     * @param endTime         String
     * @return Map
     */
    List<Map<String, Object>> getDataMonitorAlarmMinTem(@Param("tableNameBefore") String tableNameBefore, @Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取检测报警的数据：阈值以下pre
     *
     * @param tableName       String
     * @param tableNameBefore String
     * @param startTime       String
     * @param endTime         String
     * @return Map
     */
    List<Map<String, Object>> getDataMonitorAlarmMinPre(@Param("tableNameBefore") String tableNameBefore, @Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 获取检测报警的数据：阈值以下pre
     *
     * @param tableName       String
     * @param tableNameBefore String
     * @param startTime       String
     * @param endTime         String
     * @return Map
     */
    List<Map<String, Object>> getDataMonitorAlarmMinWin(@Param("tableNameBefore") String tableNameBefore, @Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime);


    List<Map<String, Object>> selectAtstationNewest(@Param("tableName") String tableName, @Param("productType") Integer productType, @Param("flag") Integer flag);

}
