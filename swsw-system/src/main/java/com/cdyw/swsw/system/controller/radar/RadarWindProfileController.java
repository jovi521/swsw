package com.cdyw.swsw.system.controller.radar;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.radar.RadarWindProfileService;
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
@RequestMapping(value = "radarWindProfile")
public class RadarWindProfileController {

    private final RadarWindProfileService radarWindProfileService;

    @Autowired
    public RadarWindProfileController(RadarWindProfileService radarWindProfileService) {
        this.radarWindProfileService = radarWindProfileService;
    }

    @ApiOperation(value = "/getStationByRadarType", notes = "根据风廓线雷达类型，查询其所对应的站点编号及其对应的经纬度")
    @GetMapping("/getStationByRadarType")
    public CommonResult<?> getStationByRadarType(@RequestParam("profileRadarType") String profileRadarType) {
        return radarWindProfileService.getStationByRadarType(profileRadarType);
    }

    @ApiOperation(value = "getStationInfo", notes = "根据风廓线雷达类型，查询其所对应的站点编号及其对应的经纬度信息，以及该站点某一高度对应的风速、风向")
    @GetMapping("/getStationInfo")
    public CommonResult<?> getStationInfo(@RequestParam("profileRadarType") String profileRadarType,
                                          @RequestParam("time") Long time,
                                          @RequestParam("timeType") String timeType,
                                          @RequestParam("height") Integer height) {
        return radarWindProfileService.getStationInfo(profileRadarType, time, timeType, height);
    }


    /**
     * 模拟造数据：根据路径将文件读取并写入数据库（仅供后端调用）
     */
    @GetMapping(value = "/insertRadarWindPro")
    public void insertRadarWindPro() {
        radarWindProfileService.insertRadarWindPro();
    }

    @ApiOperation(value = "getNamesByTypeAndTime", notes = "通过时间类型（半小时、一小时等）、起始时间和结束时间查询风廓线的文件名")
    @GetMapping(value = "/getNamesByTypeAndTime")
    public CommonResult<?> getDataByTypeAndTime(@RequestParam("timeType")
                                                @ApiParam(required = true, value = "取值：6、30、60") String timeType,
                                                @ApiParam(required = true, value = "56189, 56272, 56276, 56285, 56286, 56290, 56296") @RequestParam("staIdC") String staIdC,
                                                @RequestParam("startTime") String startTime,
                                                @RequestParam("endTime") String endTime) {
        return radarWindProfileService.getDataByTypeAndTime(timeType, staIdC, startTime, endTime);
    }

    @ApiOperation(value = "getFileByName", notes = "根据第一个接口查询的文件名根据路径匹配找到文件所在位置，并将该文件返回给前端")
    @GetMapping(value = "/getFileByName")
    public void getFileByName(@RequestParam("staIdC") String staIdC, @RequestParam("timeType") String timeType, @RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        radarWindProfileService.getFileByName(staIdC, timeType, fileName, response, isOnLine);
    }
}
