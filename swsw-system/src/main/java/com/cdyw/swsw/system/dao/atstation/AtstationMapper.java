package com.cdyw.swsw.system.dao.atstation;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface AtstationMapper {

    /**
     * 根据参数给前端返回数据
     *
     * @param tableName String
     * @param type      String
     * @param startTime String
     * @param endTime   String
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> getDataByTypeAndTime(@Param("tableName") String tableName, @Param("type") String type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据类型和时间戳给前端返回数据
     *
     * @param tableName String
     * @param type      String
     * @param startTime Long
     * @param endTime   Long
     * @return List<Map < String, Object>>
     */
    List<Map<String, Object>> getDataByTypeAndTimestamp(@Param("tableName") String tableName, @Param("type") String type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据产品类型和时间范围查询产品解析后的结果集
     *
     * @param tableName String
     * @param type      String
     * @param startTime Long
     * @param endTime   Long
     * @return List<Map < String, Object>>
     */
    Map<String, Object> getDataParseByTypeAndTime(@Param("tableName") String tableName, @Param("type") String type, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    /**
     * 根据站点，查询时间范围内的产品极值
     *
     * @param table1
     * @param table2
     * @param startTime
     * @param endTime
     * @param stationId
     * @return
     */
    HashMap<String, Object> selectExtremevalue(@Param("table1") String table1, @Param("table2") String table2,
                                               @Param("startTime") Long startTime, @Param("endTime") Long endTime,
                                               @Param("stationId") String stationId);

    /**
     * 根据站点，查询时间范围内的所有产品
     *
     * @param table1
     * @param table2
     * @param startTime
     * @param endTime
     * @param stationId
     * @return
     */
    List<HashMap<String, Object>> selectAllProduct(@Param("table1") String table1, @Param("table2") String table2,
                                                   @Param("startTime") Long startTime, @Param("endTime") Long endTime,
                                                   @Param("stationId") String stationId);

}
