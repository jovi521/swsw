package com.cdyw.swsw.data.domain.dao.satellite;


import com.cdyw.swsw.common.domain.entity.satellite.Satellite;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface SatelliteMapper {

    /**
     * 根据文件标识和资料时间查询卫星资料文件
     *
     * @param tableName String
     * @param dFileId   文件标识
     * @param dateTime  资料时间
     * @return List<Satellite>
     */
    List<Satellite> getSateFileByDfileIdAndDate(String tableName, String dFileId, String dateTime);

    /**
     * 自定义新增
     *
     * @param tableName String
     * @param satellite Satellite
     */
    void insertSatellite(String tableName, Satellite satellite);

    /**
     * 模仿C语言写的解析数据程序，创建解析表，并且往里添加数据
     *
     * @param time         int
     * @param insertValues String
     * @param statinIdC    String
     */
    void createAndInsertSatelliteParseByParam(int time, String insertValues, String statinIdC);

}
