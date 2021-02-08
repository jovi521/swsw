package com.cdyw.swsw.system.dao.radarextrapolation;

import com.cdyw.swsw.common.domain.entity.radarextrapolation.RadarExtrapolation;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RadarExtraplationMapper {

    List<RadarExtrapolation> selectDataByParam(@Param("tableName") String tableName,
                                               @Param("productType") Integer productType,
                                               @Param("time") Long time);

}
