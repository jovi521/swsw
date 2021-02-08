package com.cdyw.swsw.system.controller.radar;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.radar.RadarPhasedArrayService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping(value = "/radarPhasedArray")
public class RadarPhasedArrayController {

    private final RadarPhasedArrayService radarPhasedArrayService;

    public RadarPhasedArrayController(RadarPhasedArrayService radarPhasedArrayService) {
        this.radarPhasedArrayService = radarPhasedArrayService;
    }

    /**
     * 先返回所查询到的txt文件转化成json格式文件，拼接一个文件name属性
     *
     * @param type      String
     * @param staIdC    String
     * @param startTime String
     * @param endTime   String
     * @return CommonResult
     */
    @ApiOperation(value = "getDataParseByTypeAndTime", notes = "type根据实际数据结合数据库字典表获取，staIdC为站点编号")
    @GetMapping(value = "/getDataParseByTypeAndTime")
    public CommonResult<?> getDataParseByTypeAndTime(@ApiParam(required = true, value = "00001, 00002, 00003, 00004, multi")
                                                     @RequestParam("staIdC") String staIdC,
                                                     @ApiParam(required = true, value = "00001站点：10:CR:组合反射率； " +
                                                             "00002、00003、00004站点：10:MAXREF:组合反射率； " +
                                                             "multi拼图站点：109:CRPZ:组合反射率。 " +
                                                             "4个单站点：6:ET:回波顶高, 7:EB:回波底高, 25:VIL:垂直液态含水量, 39:RZ:雨强；  " +
                                                             "1个multi拼图站点：121:ETPZ:回波顶高, 122:EBPZ:回波底高, 112:VILPZ:垂直液态含水量, 119:RZPZ:雨强。")
                                                     @RequestParam("type") String type,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam("endTime") String endTime) throws IOException {
        return radarPhasedArrayService.getDataParseByTypeAndTime(staIdC, type, startTime, endTime);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("staIdC") String staIdC, @RequestParam("type") String type,
                                       @RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        radarPhasedArrayService.getFileParseByFileName(staIdC, type, fileName, response, isOnLine);
    }

    @ApiOperation(value = "getBinaryFileByPara", notes = "方式二： 根据数据库获取到ZIP名称，然后找到文件，最后传ZIP解压后的二进制文件给前端")
    @GetMapping(value = "/getBinaryFileByPara")
    public void getBinaryFileByPara(@ApiParam(required = true, value = "00001, 00002, 00003, 00004, multi")
                                    @RequestParam("staIdC") String staIdC,
                                    @ApiParam(required = true, value = "00001站点：10:CR:组合反射率； " +
                                            "00002、00003、00004站点：10:MAXREF:组合反射率； " +
                                            "multi拼图站点：109:CRPZ:组合反射率。 " +
                                            "4个单站点：6:ET:回波顶高, 7:EB:回波底高, 25:VIL:垂直液态含水量, 39:RZ:雨强；  " +
                                            "1个multi拼图站点：121:ETPZ:回波顶高, 122:EBPZ:回波底高, 112:VILPZ:垂直液态含水量, 119:RZPZ:雨强。")
                                    @RequestParam("type") String type,
                                    @RequestParam("startTime") String startTime,
                                    @RequestParam("endTime") String endTime, HttpServletResponse response, boolean isOnLine) throws IOException {
        radarPhasedArrayService.getBinaryFileByPara(staIdC, type, startTime, endTime, response, isOnLine);
    }
}
