package com.cdyw.swsw.data.domain.dao.fusion;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface FusionMapper {

    /**
     * 通过表名和时刻获取融合数据源的文件信息
     *
     * @param tableName String
     * @param time      String
     * @return FileEntity
     */
    FileEntity getFusionsByParam(@Param("tableName") String tableName, @Param("time") String time);

    int insertBasetime(@Param("basetime") Long basetime, @Param("modifyType") Integer modifyType);

    int insertModified(@Param("basetime") Long basetime, @Param("type") Integer type,
                       @Param("forecastCount") Integer forecastCount, @Param("modifyType") Integer modifyType);

    List<Map<String, Object>> selectDetailInfoNewest();

    Map<String, Object> selectNewestfile(@Param("basetime") Long basetime, @Param("type") Integer type,
                                         @Param("forecastCount") Integer forecastCount, @Param("layer") Integer layer);

    Map<String, Object> selectModified(@Param("basetime") Long basetime, @Param("type") Integer type,
                                       @Param("forecastCount") Integer forecastCount, @Param("layer") Integer layer);

    int insertNewestfile(@Param("basetime") Long basetime, @Param("type") Integer type,
                         @Param("forecastCount") Integer forecastCount, @Param("forecastTime") Long forecastTime,
                         @Param("name") String name, @Param("layer") Integer layer);

    List<Map<String, Object>> selectNewestWeather();

    List<Map<String, Object>> selectWeather(@Param("basetime") Long basetime, @Param("type") Integer type,
                                            @Param("forecastCount") Integer forecastCount);

    int insertWeather(@Param("map") Map<String, Object> dataMap);


}
