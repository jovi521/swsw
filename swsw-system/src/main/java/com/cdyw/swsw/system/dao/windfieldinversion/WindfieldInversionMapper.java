package com.cdyw.swsw.system.dao.windfieldinversion;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WindfieldInversionMapper {

    List<Map<String, Object>> selectByTypeAndTimeAndLayer(@Param("type") Integer type, @Param("endTime") Long endTime,
                                                          @Param("startTime") Long startTime, @Param("layer") Integer layer,
                                                          @Param("tableName") String tablename);

}
