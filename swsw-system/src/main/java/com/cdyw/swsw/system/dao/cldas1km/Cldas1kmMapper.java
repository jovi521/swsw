package com.cdyw.swsw.system.dao.cldas1km;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface Cldas1kmMapper {

    List<Map<String, Object>> selectByParam(@Param("tableName") String tableName, @Param("productType") Integer[] productType,
                                            @Param("startTime") Long startTime, @Param("endTime") Long endTime);
}
