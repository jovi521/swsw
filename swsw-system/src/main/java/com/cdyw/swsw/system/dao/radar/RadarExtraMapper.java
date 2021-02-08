package com.cdyw.swsw.system.dao.radar;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface RadarExtraMapper {

    /**
     * 根据产品类型和时间范围查询产品解析后的结果集
     *
     * @param tableName String
     * @param type      String
     * @param startTime String
     * @param endTime   String
     * @param mcode     String
     * @return Map<String, Object>
     */
    Map<String, Object> getDataParseByTypeAndTime(@Param("tableName") String tableName, @Param("type") String type, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("mcode") String mcode);

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
     * 查询最近的起报时间
     * @param tableName
     * @param tableNameOneMonthAgo
     * @return
     */
    Map<String, Object> selectMaxTime(@Param("tableName") String tableName, @Param("tableNameOneMonthAgo") String tableNameOneMonthAgo);

    Map<String, Object> selectByParam(@Param("tableName") String tableName, @Param("time") Long time, @Param("type") Integer type,
                                      @Param("layer") Integer layer, @Param("mcode") String mcode);
}