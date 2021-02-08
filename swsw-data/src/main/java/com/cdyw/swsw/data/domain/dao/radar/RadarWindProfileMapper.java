package com.cdyw.swsw.data.domain.dao.radar;

import com.cdyw.swsw.common.domain.entity.radar.RadarWindProfile;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    int deleteByPrimaryKey(String tableName, Integer id);

    /**
     * 插入对象
     *
     * @param tableName        String
     * @param radarWindProfile RadarWindProfile
     * @return int
     */
    int insert(String tableName, RadarWindProfile radarWindProfile);

    /**
     * 根据主键查询对象
     *
     * @param tableName String
     * @param id        id
     * @return RadarWindProfile
     */
    RadarWindProfile selectByPrimaryKey(String tableName, Integer id);

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
    int updateByPrimaryKey(String tableName, RadarWindProfile radarWindProfile);

    /**
     * 根据dFileId和dateTime查询风廓线雷达对象
     *
     * @param tableName String
     * @param dFileId   String
     * @param dateTime  String
     * @return List<RadarWindProfile>
     */
    List<RadarWindProfile> getRadarWinFileByDfileIdAndDate(String tableName, String dFileId, String dateTime);
}