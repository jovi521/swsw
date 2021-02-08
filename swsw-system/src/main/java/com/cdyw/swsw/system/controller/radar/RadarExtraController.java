package com.cdyw.swsw.system.controller.radar;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.radar.RadarExtraService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author jovi
 */
@RestController
@RequestMapping(value = "/radarExtra")
public class RadarExtraController {

    private final RadarExtraService radarExtraService;

    @Autowired
    public RadarExtraController(RadarExtraService radarExtraService) {
        this.radarExtraService = radarExtraService;
    }

    @ApiOperation(value = "getMcode", notes = "新增根据时间查询本时间段内mcode取值返回给前端")
    @GetMapping(value = "/getMcode")
    public CommonResult<?> getMcode(@RequestParam(value = "staIdC", required = false) String staIdC, @RequestParam("type") String type,
                                    @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {
        return radarExtraService.getMcode(staIdC, type, startTime, endTime);
    }

    @ApiOperation(value = "getDataParseByTypeAndTime", notes = "先返回所查询到的txt文件转化成json格式文件，拼接一个文件name属性")
    @GetMapping(value = "/getDataParseByTypeAndTime")
    public CommonResult<?> getDataParseByTypeAndTime(@RequestParam("type")
                                                     @ApiParam(required = true, value = "201：EXTRACR：组合反射率、204：EXTRAVIL：垂直液态含水量、203：EXTRARZ：雨强") String type,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam(value = "endTime", required = false) String endTime,
                                                     @RequestParam(value = "mcode", required = false) String mcode) throws IOException {
        return radarExtraService.getDataParseByTypeAndTime(type, startTime, endTime, mcode);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        radarExtraService.getFileParseByFileName(type, fileName, response, isOnLine);
    }

    @GetMapping("/getDataParseByParam")
    public CommonResult<Map<String, Object>> getDataParseByParam(@RequestParam("type") String type,
                                                 @RequestParam("time") String time,
                                                 @RequestParam(value = "layer", defaultValue = "1") String layer){
        return radarExtraService.getDataParseByParam(type, time, layer);
    }
}
