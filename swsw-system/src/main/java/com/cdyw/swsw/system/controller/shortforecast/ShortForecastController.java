package com.cdyw.swsw.system.controller.shortforecast;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.shortforecast.ShortForecastService;
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
 * @author cdyw
 * @description
 */
@RestController
@RequestMapping("/shortforecast")
public class ShortForecastController {

    @Autowired
    private ShortForecastService shortForecastService;

    @ApiOperation(value = "getMcodeAndLayer", notes = "新增根据时间查询本时间段内mcode取值返回给前端")
    @GetMapping(value = "/getMcodeAndLayer")
    public CommonResult<?> getMcode(@RequestParam(value = "staIdC", required = false) String staIdC, @RequestParam("type") String type,
                                    @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime) {

        return shortForecastService.getMcodeAndLayer(staIdC, type, startTime, endTime);
    }

    @ApiOperation(value = "getDataParseByTypeAndTime", notes = "先返回所查询到的txt文件转化成json格式文件，拼接一个文件name属性")
    @GetMapping(value = "/getDataParseByTypeAndTime")
    public CommonResult<?> getDataParseByTypeAndTime(@RequestParam("type")
                                                     @ApiParam(required = true, value = "901：TEM：温度、204：WINDFIELD：风场") String type,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam(value = "endTime", required = false) String endTime,
                                                     @RequestParam(value = "mcode") String mcode,
                                                     @RequestParam(value = "layer") String layer) throws IOException {
        return shortForecastService.getDataParseByTypeAndTime(type, startTime, endTime, mcode, layer);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        shortForecastService.getFileParseByFileName(type, fileName, response, isOnLine);
    }
}
