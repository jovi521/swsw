package com.cdyw.swsw.data.controller.radar;

import cn.shuibo.annotation.Encrypt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.radar.RadarWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jovi
 */
@RestController
@RequestMapping(value = "/radarWeather")
public class RadarWeatherController {

    private final RadarWeatherService radarWeatherService;

    @Autowired
    public RadarWeatherController(RadarWeatherService radarWeatherService) {
        this.radarWeatherService = radarWeatherService;
    }

    @Encrypt
    @GetMapping(value = "/getRadaFileByTimeRangeAndStaId")
    public CommonResult<?> getRadaFileByTimeRangeAndStaId(@RequestParam("dataFormat") String dataFormat,
                                                          @RequestParam("dataCode") String dataCode,
                                                          @RequestParam("elements") String elements,
                                                          @RequestParam(value = "limitCnt", required = false) String limitCnt,
                                                          @RequestParam(value = "orderBy", required = false) String orderBy,
                                                          @RequestParam("staIds") String staIds,
                                                          @RequestParam("timeRange") String timeRange,
                                                          @RequestParam(value = "staTypes", required = false) String staTypes) {
        return radarWeatherService.getRadaFileByTimeRangeAndStaId(dataFormat, dataCode, elements, limitCnt, orderBy, staIds, timeRange, staTypes);
    }

    @GetMapping(value = "/createAndInsertRadarWeaParseByParam")
    public CommonResult<?> createAndInsertRadarWeaParseByParam(@RequestParam("time") int time,
                                                               @RequestParam("insertValues") String insertValues,
                                                               @RequestParam("statinIdC") String statinIdC) {
        return radarWeatherService.createAndInsertRadarWeaParseByParam(time, insertValues, statinIdC);
    }

}
