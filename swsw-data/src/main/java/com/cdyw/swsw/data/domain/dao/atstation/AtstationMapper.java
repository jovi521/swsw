package com.cdyw.swsw.data.domain.dao.atstation;

import com.cdyw.swsw.common.domain.ao.txt.AtstationParseTxt;
import com.cdyw.swsw.common.domain.entity.atstation.Atstation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface AtstationMapper {

    /**
     * 按时间段、地区检索地面要素数据
     *
     * @return List<Atstation>
     */
    List<Atstation> getSurfEleInRegionByTimeRange();

    /**
     * 按时间段、站号段检索地面数据要素
     *
     * @return List<Atstation>
     */
    List<Atstation> getSurfEleByTimeRangeAndStaIDRange();

    /**
     * 按时间段检索地面数据要素
     *
     * @return List<Atstation>
     */
    List<Atstation> getSurfEleByTimeRange();

    /**
     * 按atsId和dateTime查询
     *
     * @param tableName  String
     * @param stationIdD Integer
     * @param dateTime   String
     * @return List<Atstation>
     */
    Atstation getAtsByAtsIdAndDate(String tableName, Integer stationIdD, String dateTime);

    /**
     * 按id查询
     *
     * @param tableName String
     * @param id        Integer
     * @return List<Atstation>
     */
    List<Atstation> getAtsById(@Param("tableName") String tableName, @Param("id") Integer id);

    /**
     * 调用存储过程新增自动站表
     */
    void createAtStationTableByName();

    /**
     * 创建自动站 base 数据
     *
     * @param tableName String
     * @param atstation Atstation
     * @return 数量
     */
    int insertAtStationBase(@Param("tableName") String tableName, @Param("atstation") Atstation atstation);

    /**
     * 创建自动站 parse 数据
     *
     * @param tableName         String
     * @param atstationParseTxt AtstationParseTxt
     * @return 数量
     */
    int insertAtStationParse(@Param("tableName") String tableName, @Param("atstationParseTxt") AtstationParseTxt atstationParseTxt);

    /**
     * 通过时间、类型和过滤条件查询
     *
     * @param tableName
     * @param datetime
     * @param fieldStr
     * @param typeStr
     * @param filterStr
     * @return
     */
    List<HashMap<String, Object>> getByDatetime(@Param("tableName") String tableName, @Param("datetime") String datetime,
                                                @Param("fieldStr") String fieldStr, @Param("typeStr") String typeStr,
                                                @Param("filterStr") Float filterStr);

}
