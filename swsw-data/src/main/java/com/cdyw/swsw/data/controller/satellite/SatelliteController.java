package com.cdyw.swsw.data.controller.satellite;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.satellite.SatelliteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
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
     * 指定时间的卫星资料文件检索接口
     */
    @GetMapping(value = "/getSateFileByTimeRange")
    public CommonResult<?> getSateFileByTimeRange(@RequestParam("dataFormat") String dataFormat,
                                                  @RequestParam("dataCode") String dataCode,
                                                  @RequestParam("elements") String elements,
                                                  @RequestParam(value = "limitCnt", required = false) String limitCnt,
                                                  @RequestParam(value = "orderBy", required = false) String orderBy,
                                                  @RequestParam(value = "eleValueRanges", required = false) String eleValueRanges,
                                                  @RequestParam(value = "timeRange") String timeRange) {
        return satelliteService.getSateFileByTimeRange(dataFormat, dataCode, elements, limitCnt, orderBy, eleValueRanges, timeRange);
    }

    @GetMapping(value = "/createAndInsertSatelliteParseByParam")
    public CommonResult<?> createAndInsertSatelliteParseByParam(@RequestParam("time") int time,
                                                                @RequestParam("insertValues") String insertValues,
                                                                @RequestParam("statinIdC") String statinIdC) {
        return satelliteService.createAndInsertSatelliteParseByParam(time, insertValues, statinIdC);
    }

}
