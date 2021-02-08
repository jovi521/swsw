package com.cdyw.swsw.system.controller.radar;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.radar.RadarWeatherService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

    @ApiOperation(value = "getDataParseByTypeAndTime", notes = "先返回所查询到的txt文件转化成json格式文件，拼接一个文件name属性")
    @GetMapping(value = "/getDataParseByTypeAndTime")
    public CommonResult<?> getDataParseByTypeAndTime(@RequestParam("type")
                                                     @ApiParam(required = true, value = "6：ET：回波顶高、7：EB：回波底高、10：MAXREF：组合反射率、25：VIL：垂直液态含水量、39：RZ：雨强") String type,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam("endTime") String endTime) throws IOException {
        return radarWeatherService.getDataParseByTypeAndTime(type, startTime, endTime);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        radarWeatherService.getFileParseByFileName(type, fileName, response, isOnLine);
    }

}
