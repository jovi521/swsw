package com.cdyw.swsw.system.dao.radar;

import com.cdyw.swsw.common.domain.entity.radar.RadarWindProfile;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface RadarWindProfileMapper {

    /**
     * 根据主键删除指定对象
     *
     * @param tableName String
     * @param id        id
     * @return int
     */
    int deleteByPrimaryKey(@Param("tableName") String tableName, @Param("id") Integer id);

    /**
     * 插入对象
     *
     * @param tableName        String
     * @param radarWindProfile RadarWindProfile
     * @return int
     */
    int insert(@Param("tableName") String tableName, @Param("radarWindProfile") RadarWindProfile radarWindProfile);

    /**
     * 根据主键查询对象
     *
     * @param tableName String
     * @param id        id
     * @return RadarWindProfile
     */
    RadarWindProfile selectByPrimaryKey(@Param("tableName") String tableName, @Param("id") Integer id);

    /**
     * 查询所有
     *
     * @param tableName String
     * @return List<RadarWindProfile>
     */
    List<RadarWindProfile> selectAll(String tableName);

    /**
     * 更新
     *
     * @param tableName        String
     * @param radarWindProfile RadarWindProfile
     * @return int
     */
    int updateByPrimaryKey(@Param("tableName") String tableName, @Param("radarWindProfile") RadarWindProfile radarWindProfile);

    /**
     * 根据dFileId和dateTime查询风廓线雷达对象
     *
     * @param tableName String
     * @param dFileId   String
     * @param dateTime  String
     * @return List<RadarWindProfile>
     */
    List<RadarWindProfile> getRadarWinFileByDfileIdAndDate(@Param("tableName") String tableName, @Param("dFileId") String dFileId, @Param("dateTime") String dateTime);

    /**
     * 根据起始时间和结束时间查询风廓线雷达的文件路径和文件名
     *
     * @param tableName String
     * @param startTime String
     * @param endTime   String
     * @param timeType  String
     * @return List
     */
    List<Map<String, Object>> getRadarWinFileByTime(@Param("tableName") String tableName, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("timeType") String timeType);

    /**
     * 根据起始时间(时间戳)和结束时间(时间戳)查询风廓线雷达的文件路径和文件名
     *
     * @param tableName String
     * @param startTime Long
     * @param endTime   Long
     * @param timeType  String
     * @return List
     */
    List<Map<String, Object>> getRadarWinFileByTimestamp(@Param("tableName") String tableName, @Param("timeType") String timeType, @Param("startTime") Long startTime, @Param("endTime") Long endTime);

}