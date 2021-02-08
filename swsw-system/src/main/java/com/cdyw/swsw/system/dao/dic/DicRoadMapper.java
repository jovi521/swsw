package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicRoad;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cdyw
 */
@Repository
public interface DicRoadMapper {

    /**
     * 插入数据
     *
     * @param road
     */
    void insertRoad(DicRoad road);

    /**
     * 通过起始和结束站点查询
     *
     * @param startStation
     * @param endStation
     * @return
     */
    List<DicRoad> selectRoad(@Param("startStation") Integer startStation, @Param("endStation") Integer endStation);

}
