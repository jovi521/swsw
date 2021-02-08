package com.cdyw.swsw.system.dao.forecastcheck;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface ForecastCheckMapper {


    List<HashMap<String, Object>> selectByThresholdAndTimeperiod(@Param("tablename1") String tablename1,
                                                                 @Param("tablename2") String tablename2,
                                                                 @Param("timeList") List<Long> timeList,
                                                                 @Param("threshold") Float threshold);
}
