package com.cdyw.swsw.data.domain.dao.common;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author jovi
 */
@Repository
public interface ComCreateTableMapper {

    /**
     * 创建通用日志表
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createLog(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建通用文件实体表
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createFileEntity(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建自动站 base 表
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createAtStationBase(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建自动站 parse 表
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createAtStationParse(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建天气雷达和相控阵雷达 parse 表
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createRadarParse(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建卫星 parse 表
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createSatelliteParse(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建0-2小时和0-12小时融合数据表(base 和 parse 暂时一致)
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createFusion(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建cldas1km数据表(parse)
     *
     * @param tableName    表名
     * @param tableComment 表注释
     */
    void createCldas1kmParse(@Param("tableName") String tableName, @Param("tableComment") String tableComment);

    /**
     * 创建 fusion12h_basetime 数据表
     */
    void createFusionBaseTime();

    /**
     * 创建 fusion12h_weather_information 数据表
     */
    void createFusionWeatherInformation();
}
