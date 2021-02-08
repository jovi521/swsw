package com.cdyw.swsw.system.dao.shortforecast;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ShortForecastMapper {


    /**
     * 根据站点编号、产品类型、时间段查询mcode
     *
     * @param tableParseName String
     * @param type           String
     * @param startTime      String
     * @param endTime        String
     * @return List
     */
    List<String> getMcode(@Param("tableParseName") String tableParseName, @Param("type") String type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据站点编号、产品类型、时间段查询mcode
     *
     * @param tableParseName String
     * @param type           String
     * @param startTime      String
     * @param endTime        String
     * @return List
     */
    List<Integer> getLayer(@Param("tableParseName") String tableParseName, @Param("type") String type, @Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据产品类型和时间范围查询产品解析后的结果集
     *
     * @param tableName String
     * @param type      String
     * @param startTime Long
     * @param endTime   Long
     * @param mcode     String
     * @param layer     Integer
     * @return Map<String, Object>
     */
    Map<String, Object> getDataParseByTypeAndTime(@Param("tableName") String tableName, @Param("type") String type,
                                                  @Param("startTime") Long startTime, @Param("endTime") Long endTime,
                                                  @Param("mcode") String mcode, @Param("layer") Integer layer);


}
