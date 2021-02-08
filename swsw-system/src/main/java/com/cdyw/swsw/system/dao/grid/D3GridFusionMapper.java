package com.cdyw.swsw.system.dao.grid;

import com.cdyw.swsw.common.domain.entity.grid.D3GridFusion;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface D3GridFusionMapper {

    /**
     * 根据产品类型和时间范围查询产品解析后的结果集
     *
     * @param tableName String
     * @param type      String
     * @param startTime Long
     * @param endTime   Long
     * @param hight     String
     * @return Map<String, Object>
     */
    Map<String, Object> getDataParseByTypeAndTime(@Param("tableName") String tableName, @Param("type") String type, @Param("startTime") Long startTime, @Param("endTime") Long endTime, @Param("hight") String hight);

    /**
     * 插入对象
     *
     * @param tableName    String
     * @param d3GridFusion D3GridFusion
     * @return int
     */
    int insert(@Param("tableName") String tableName, @Param("d3GridFusion") D3GridFusion d3GridFusion);
}