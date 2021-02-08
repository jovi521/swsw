package com.cdyw.swsw.system.controller.atstation;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.atstation.AtstationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/atstation")
public class AtstationController {

    private final AtstationService atstationService;

    @Autowired
    public AtstationController(AtstationService atstationService) {
        this.atstationService = atstationService;
    }


    @GetMapping(value = "/getDataByTypeAndTime")
    public CommonResult<?> getDataByTypeAndTime(@RequestParam(value = "type", required = false) String type,
                                                @RequestParam(value = "startTime", required = true) String startTime,
                                                @RequestParam(value = "endTime", required = true) String endTime) {
        return atstationService.getDataByTypeAndTime(type, startTime, endTime);
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
        return atstationService.getDataParseByTypeAndTime(type, startTime, endTime);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("type") String type, @RequestParam("fileName") String fileName, HttpServletRequest request, HttpServletResponse response, boolean isOnLine) throws IOException {
        atstationService.getFileParseByFileName(type, fileName, response, isOnLine);
    }


    @GetMapping(value = "/getPastTimeDate")
    public CommonResult<?> getPastTimeDate(@RequestParam("stationId") String stationId,
                                           @RequestParam("time") Long time) {
        CommonResult<?> pastTimeDate = atstationService.getPastTimeDate(stationId, time);
        return pastTimeDate;
    }


    /**
     * 先返回所查询到的txt文件转化成json格式文件，拼接一个文件name属性
     *
     * @param type      String
     * @param startTime String
     * @param endTime   String
     * @return CommonResult
     */
    /*
    @GetMapping(value = "/getDataParseByTypeAndTime")
    public CommonResult<?> getDataParseByTypeAndTime(@RequestParam("type") String type,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam("endTime") String endTime) throws IOException {
        return satelliteService.getDataParseByTypeAndTime(type, startTime, endTime);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        satelliteService.getFileParseByFileName(fileName, response, isOnLine);
    }
    */


}
