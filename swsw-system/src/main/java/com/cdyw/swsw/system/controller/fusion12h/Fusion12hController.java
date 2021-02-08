package com.cdyw.swsw.system.controller.fusion12h;

import com.cdyw.swsw.common.domain.entity.fusion12h.Fusion12hVo;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.fusion12h.Fusion12hService;
import com.vividsolutions.jts.io.ParseException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author liudong
 * @modified jovi
 */
@RestController
@RequestMapping("/fusion12h")
public class Fusion12hController {

    private final Fusion12hService fusion12hService;

    Logger logger = LoggerFactory.getLogger(Fusion12hController.class);

    @Autowired
    public Fusion12hController(Fusion12hService fusion12hService) {
        this.fusion12hService = fusion12hService;
    }

    @GetMapping("/getDataByTypeAndTime")
    public CommonResult<?> getDataByTypeAndTime(@RequestParam("type") String type,
                                                @RequestParam(value = "layer", defaultValue = "0") String layer,
                                                @RequestParam("time") String time, @RequestParam("basetime") String basetime) {
        return fusion12hService.getDataByTypeAndTime(type, layer, basetime, time);
    }

    @GetMapping("/getDetailDataNewest")
    public CommonResult<?> getDetailDataNewest() {
        return fusion12hService.getDetailDataNewest();
    }


    @PostMapping("/correctDataByPolygon")
    public CommonResult<?> correctDataByPolygon(@RequestBody Fusion12hVo fusion12hVo) {
        List<String> lonlat = fusion12hVo.getLonlat();
        String modifyValue = fusion12hVo.getModifyValue();
        String filename = fusion12hVo.getFilename();
        String basetime = fusion12hVo.getBasetime();
        String forecastTime = fusion12hVo.getForecastTime();
        String productType = fusion12hVo.getProductType();
        String layer = fusion12hVo.getLayer();
        try {
            return fusion12hService.correctDataByPolygon(lonlat, modifyValue, filename, basetime, forecastTime, layer, productType);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonResult.failed();
        } catch (ParseException e) {
            e.printStackTrace();
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "previewPointModified", notes = "预览点订正操作")
    @GetMapping("/previewPointModified")
    public CommonResult<?> previewPointModified(@ApiParam(name = "lonLat", value = "经度和纬度（场馆或者格点都一样查询）", required = true)
                                                @RequestParam(value = "lonLat") String lonLat,
                                                @ApiParam(name = "baseTime", value = "起报时间", required = true)
                                                @RequestParam(value = "baseTime") String baseTime) {
        return fusion12hService.previewPointModified(lonLat, baseTime, null);
    }

    @ApiOperation(value = "commitPointModified", notes = "提交点订正操作")
    @GetMapping("/commitPointModified")
    public CommonResult<?> commitPointModified(@ApiParam(name = "lonLat", value = "经度和纬度（场馆或者格点都一样查询）", required = true)
                                               @RequestParam(value = "lonLat") String lonLat,
                                               @ApiParam(name = "baseTime", value = "起报时间", required = true)
                                               @RequestParam(value = "baseTime") String baseTime,
                                               @ApiParam(name = "fusion12hWeatherInformation", value = "天气信息", required = true)
                                               @RequestParam(value = "fusion12hWeatherInformation") String fusion12hWeatherInformation) {
        return fusion12hService.commitPointModified(lonLat, baseTime, fusion12hWeatherInformation);
    }

    /*
    @RequestParam("dataList") List<Map<String, Object>> dataList,
    @RequestParam("filename") String filename,
    @RequestParam("modifyingType") String modifyingType
    */

    /**
     * @PostMapping("/correctData")
     * @ResponseBody public CommonResult<?> correctData(@RequestBody Fusion12hVo fusion12hVo) {
     * String filename = fusion12hVo.getFilename();
     * String modifyingType = fusion12hVo.getModifyingType();
     * List<FusionDataVo> dataList = fusion12hVo.getDataList();
     * return fusion12hService.correctData(dataList, filename, modifyingType);
     * }
     */

    @RequestMapping("/finishCorrect")
    public CommonResult<?> finishCorrect(@RequestParam("basetime") String basetime, @RequestParam("modifyType") String modifyType) {
        return fusion12hService.finishCorrect(basetime, modifyType);
    }

}
