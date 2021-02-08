package com.cdyw.swsw.data.domain.dao.radar;

import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author jovi
 */
@Repository
public interface RadarPhasedArrayMapper {

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
     * 天气雷达 parse 新增：文件信息 + TXT解析后的信息
     *
     * @param tableName     String
     * @param fileEntity    FileEntity
     * @param radarParseTxt CommonRadarParseTxt
     * @return int
     */
    int insertRadarParse(@Param("tableName") String tableName, @Param("fileEntity") FileEntity fileEntity, @Param("radarParseTxt") CommonRadarParseTxt radarParseTxt);

}
