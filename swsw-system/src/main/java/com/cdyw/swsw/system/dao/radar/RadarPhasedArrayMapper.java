package com.cdyw.swsw.system.dao.radar;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface RadarPhasedArrayMapper {

    /**
     * 根据产品类型和时间范围查询产品解析后的结果集
     *
     * @param tableName String
     * @param staIdC    String
     * @param productId String
     * @param startTime Long
     * @param endTime   Long
     * @return List<Map < String, Object>>
     */
    Map<String, Object> getDataParseByTypeAndTime(@Param("tableName") String tableName, @Param("staIdC") String staIdC, @Param("productId") String productId, @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}