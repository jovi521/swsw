package com.cdyw.swsw.system.service.radar;

import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import com.cdyw.swsw.system.dao.radar.RadarWeatherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jovi
 */
@Slf4j
@Service
public class RadarWeatherService {

    private final TypeEnum TYPE = TypeEnum.TYPE_RADAR_WEATHER;

    private final CommonDataParse commonDataParse;

    private final CommonTable commonTable;

    private final CommonTableName commonTableName;

    private final RadarWeatherMapper radarWeatherMapper;

    @Autowired
    public RadarWeatherService(CommonDataParse commonDataParse, CommonTable commonTable, CommonTableName commonTableName, RadarWeatherMapper radarWeatherMapper) {
        this.commonDataParse = commonDataParse;
        this.commonTable = commonTable;
        this.commonTableName = commonTableName;
        this.radarWeatherMapper = radarWeatherMapper;
    }

    public CommonResult<?> getDataParseByTypeAndTime(String type, String startTime, String endTime) throws IOException {
        String staIdC = commonTable.getStaRadarWeather();
        String t = commonTable.getSufParse();
        String time = DateUtils.getDayStringByTimestamp(Long.parseLong(startTime), "yyyyMMddHHmmss");
        String startTimeFormat = DateUtils.getDayStringByTimestamp(Long.parseLong(startTime), "yyyy/MM/dd HH:mm:ss");
        String endTimeFormat = DateUtils.getDayStringByTimestamp(Long.parseLong(endTime), "yyyy/MM/dd HH:mm:ss");
        // 先查询数据库
        String tableName = commonTableName.getTableNameByParam(TYPE, staIdC, time, t);
        CommonRadarParseTxt radarParseTxt = radarWeatherMapper.getDataParseEntityByTypeAndTime(tableName, type, Long.parseLong(startTime), Long.parseLong(endTime));
        // 找到文件路径
        if (radarParseTxt == null) {
            return CommonResult.failed("未查询到相关信息，起始时间为： " + startTimeFormat + " ，终止时间为： " + endTimeFormat);
        } else {
            return CommonResult.success(radarParseTxt);
        }
    }

    public void getFileParseByFileName(String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        String staIdC = commonTable.getStaRadarWeather();
        commonDataParse.getPngByFileName(TypeEnum.TYPE_RADAR_WEATHER, staIdC, type, fileName, response, isOnLine);
    }

}
