package com.cdyw.swsw.system.dao.satellite;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface SatelliteMapper {

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

    Map<String, Object> getDataParseByParam(@Param("tableName") String tableName, @Param("type") Integer type, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
