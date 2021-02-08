package com.cdyw.swsw.data.domain.dao.radar;

import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.entity.radar.RadarWeather;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface RadarWeatherMapper {

    /**
     * 根据主键id删除对象
     *
     * @param tableName String
     * @param id        id
     * @return int
     */
    int deleteByPrimaryKey(String tableName, Integer id);

    /**
     * 新增
     *
     * @param tableName    String
     * @param radarWeather RadarWeather
     * @return int
     */
    int insert(String tableName, RadarWeather radarWeather);

    /**
     * 根据主键查询对象
     *
     * @param tableName String
     * @param id        id
     * @return RadarWeather
     */
    RadarWeather selectByPrimaryKey(String tableName, Integer id);

    /**
     * 根据文件路径和文件名称查询对象
     *
     * @param tableName String
     * @param path      文件路径
     * @param name      文件名称
     * @return CommonRadarParseTxt
     */
    CommonRadarParseTxt selectParseByPathAndName(@Param("tableName") String tableName, @Param("path") String path, @Param("name") String name);

    /**
     * 查询所有的对象
     *
     * @param tableName String
     * @return List<RadarWeather>
     */
    List<RadarWeather> selectAll(String tableName);

    /**
     * 更新
     *
     * @param tableName    String
     * @param radarWeather radarWeather
     * @return int
     */
    int updateByPrimaryKey(String tableName, RadarWeather radarWeather);

    /**
     * 根据文件唯一标识和资料时间查询天气雷达对象
     *
     * @param tableName String
     * @param dFileId   String
     * @param dateTime  String
     * @return List<RadarWeather>
     */
    List<RadarWeather> getRadarWeaFileByDfileIdAndDate(String tableName, String dFileId, String dateTime);

    /**
     * 天气雷达 base 新增
     *
     * @param tableName  String
     * @param fileEntity FileEntity
     * @return int
     */
    int insertRadarWeatherBase(@Param("tableName") String tableName, @Param("fileEntity") FileEntity fileEntity);

    /**
     * 天气雷达 parse 新增：文件信息 + TXT解析后的信息
     *
     * @param tableName     String
     * @param fileEntity    FileEntity
     * @param radarParseTxt CommonRadarParseTxt
     * @return int
     */
    int insertRadarParse(@Param("tableName") String tableName, @Param("fileEntity") FileEntity fileEntity, @Param("radarParseTxt") CommonRadarParseTxt radarParseTxt);

    /**
     * 模仿C语言写的解析数据程序，创建解析表，并且往里添加数据
     *
     * @param time         int
     * @param insertValues String
     * @param statinIdC    String
     */
    void createAndInsertRadarWeaParseByParam(int time, String insertValues, String statinIdC);

}