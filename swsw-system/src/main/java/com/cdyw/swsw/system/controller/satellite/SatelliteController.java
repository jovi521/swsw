package com.cdyw.swsw.system.controller.satellite;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.satellite.SatelliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 给前端返回的都是解析后的数据
 *
 * @author jovi
 */
@RestController
@RequestMapping(value = "/satellite")
public class SatelliteController {

    private final SatelliteService satelliteService;

    @Autowired
    public SatelliteController(SatelliteService satelliteService) {
        this.satelliteService = satelliteService;
    }

    /**
     * 先返回所查询到的txt文件转化成json格式文件，拼接一个文件name属性
     *
     * @param type      String
     * @param startTime String
     * @param endTime   String
     * @return CommonResult
     */
    @GetMapping(value = "/getDataParseByTypeAndTime")
    public CommonResult<?> getDataParseByTypeAndTime(@RequestParam("type") String type,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam("endTime") String endTime) throws IOException {
        return satelliteService.getDataParseByTypeAndTime(type, startTime, endTime);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        satelliteService.getFileParseByFileName(type, fileName, response, isOnLine);
    }

    @GetMapping(value = "/getDataParseByParam")
    public CommonResult<?> getDataParseByParam(@RequestParam("type") String type,
                                               @RequestParam("time") String time) throws IOException {
        return satelliteService.getDataParseByParam(type, time);
    }

}
