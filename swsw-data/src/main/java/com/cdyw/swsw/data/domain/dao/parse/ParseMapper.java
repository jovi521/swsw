package com.cdyw.swsw.data.domain.dao.parse;


import com.cdyw.swsw.common.domain.entity.parse.ModeDataTSRainParse;
import com.cdyw.swsw.common.domain.entity.parse.Parse;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParseMapper {

    /**
     * 插入解析后的数据
     *
     * @param tableName 表名
     * @param parse     解析数据
     * @return
     */
    Integer insertParse(@Param("tableName") String tableName, @Param("parse") Parse parse);

    Integer insertModeDataTSRainParse(@Param("tableName") String tableName, @Param("parse") ModeDataTSRainParse parse);


    /**
     * 插入解析后的数据
     *
     * @param tableName 表名
     * @param parse     解析数据
     * @return
     */
    Integer insertShortforecastParse(@Param("tableName") String tableName, @Param("parse") Parse parse, @Param("shortForecastCount") Integer shortForecastCount);
}
