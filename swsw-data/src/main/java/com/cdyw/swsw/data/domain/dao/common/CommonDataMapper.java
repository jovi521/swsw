package com.cdyw.swsw.data.domain.dao.common;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.common.domain.entity.radarextrapolation.RadarExtrapolation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommonDataMapper {
    /**
     * 通用新增文件方法
     *
     * @param dataMap   参数
     * @param tableName 表名
     * @return 表的数量
     */
    int insertCommonData(@Param("dataMap") Map<String, Object> dataMap, @Param("tableName") String tableName);

    /**
     * 通用新增文件方法
     *
     * @param fileEntity 文件
     * @param tableName  表名
     * @return 表的数量
     */
    int insertCommonInfo(@Param("fileEntity") FileEntity fileEntity, @Param("tableName") String tableName);

    /**
     * 通用新增文件方法
     *
     * @param tableName 表名
     * @return 表的数量
     */
    int insertFusion(@Param("fileEntity") FileEntity fileEntity, @Param("createTime") Long createTime,
                     @Param("forecastTime") Long forecastTime, @Param("tableName") String tableName);

    int insertSatelliteData(@Param("dataMap") Map<String, Object> dataMap, @Param("tableName") String tableName);

    int insertCldas1kmData(@Param("dataMap") Map<String, Object> dataMap, @Param("tableName") String tableName);

    int insertWeatherRadarExtrapolation(@Param("radarExtrapolation") RadarExtrapolation radarExtrapolation,
                                        @Param("tableName") String tableName);

    int insertCommonLog(@Param("log") DataMonitorLog log, @Param("tableName") String tableName);

    List<Map<String, Object>> selectByName(@Param("name") String name, @Param("tableName") String tableName);

    int insertSatellite(@Param("dataMap") Map<String, Object> dataMap, @Param("tableName") String tableName);
}
