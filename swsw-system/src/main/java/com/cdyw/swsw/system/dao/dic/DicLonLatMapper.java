package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicLonLat;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DicLonLatMapper {

    /**
     * 通过id删除数据
     *
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入
     *
     * @param record Object
     * @return int
     */
    int insert(DicLonLat record);

    /**
     * 根据id查询数据
     *
     * @param id Integer
     * @return Object
     */
    DicLonLat selectByPrimaryKey(Integer id);

    /**
     * 查询全部
     *
     * @return List
     */
    List<DicLonLat> selectAll();

    /**
     * 根据id更新指定数据
     *
     * @param record Object
     * @return int
     */
    int updateByPrimaryKey(DicLonLat record);


    /**
     * 根据传入的经纬度或关键词获取站点(包括场馆)基础信息
     *
     * @param lon        String
     * @param lat        String
     * @param location   String
     * @param type       String
     * @param parentCode String
     * @return List
     */
    List<DicLonLat> getLonLatByPara(@Param("lon") String lon, @Param("lat") String lat, @Param("location") String location, @Param("type") Integer type, @Param("parentCode") String parentCode);

    /**
     * 根据站点编号和站点类型查询站点信息
     *
     * @param code String
     * @param type Integer
     * @return DicLonLat
     */
    DicLonLat getLonLatByCode(@Param("code") String code, @Param("type") Integer type);

    /**
     * 根据父类型查询站点信息
     *
     * @param parentCode String
     * @return DicLonLat
     */
    List<DicLonLat> getLonLatByParentCode(@Param("parentCode") String parentCode);
}