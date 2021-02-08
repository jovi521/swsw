package com.cdyw.swsw.data.controller.atstation;

import cn.shuibo.annotation.Encrypt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.atstation.AtstationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jovi
 */
@RestController
@RequestMapping(value = "/atstation")
public class AtstationController {

    private final AtstationService atstationService;

    @Autowired
    public AtstationController(AtstationService atstationService) {
        this.atstationService = atstationService;
    }

    @Encrypt
    @GetMapping(value = "/getSurfEleInRegionByTimeRange")
    public CommonResult<?> getSurfEleInRegionByTimeRange(@RequestParam("dataFormat") String dataFormat,
                                                         @RequestParam("dataCode") String dataCode,
                                                         @RequestParam("elements") String elements,
                                                         @RequestParam(value = "limitCnt", required = false) String limitCnt,
                                                         @RequestParam(value = "orderBy", required = false) String orderBy,
                                                         @RequestParam(value = "staLevels", required = false) String staLevels,
                                                         @RequestParam(value = "eleValueRanges", required = false) String eleValueRanges,
                                                         @RequestParam(value = "adminCodes") String adminCodes,
                                                         @RequestParam(value = "timeRange") String timeRange,
                                                         @RequestParam(value = "staTypes", required = false) String staTypes) {
        return atstationService.getSurfEleInRegionByTimeRange(dataFormat, dataCode, elements, limitCnt, orderBy, staLevels, eleValueRanges, adminCodes, timeRange, staTypes);
    }

    @Encrypt
    @GetMapping(value = "/getSurfEleByTimeRangeAndStaIDRange")
    public CommonResult<?> getSurfEleByTimeRangeAndStaIDRange(@RequestParam("dataFormat") String dataFormat,
                                                              @RequestParam("dataCode") String dataCode,
                                                              @RequestParam("elements") String elements,
                                                              @RequestParam(value = "limitCnt", required = false) String limitCnt,
                                                              @RequestParam(value = "orderBy", required = false) String orderBy,
                                                              @RequestParam(value = "minStaId") String minStaId,
                                                              @RequestParam(value = "eleValueRanges", required = false) String eleValueRanges,
                                                              @RequestParam(value = "maxStaId") String maxStaId,
                                                              @RequestParam(value = "timeRange") String timeRange,
                                                              @RequestParam(value = "staTypes", required = false) String staTypes) {
        return atstationService.getSurfEleByTimeRangeAndStaIDRange(dataFormat, dataCode, elements, limitCnt, orderBy, minStaId, eleValueRanges, maxStaId, timeRange, staTypes);
    }

    @Encrypt
    @GetMapping(value = "/getSurfEleByTimeRange")
    public CommonResult<?> getSurfEleByTimeRange(@RequestParam("dataFormat") String dataFormat,
                                                 @RequestParam("dataCode") String dataCode,
                                                 @RequestParam("elements") String elements,
                                                 @RequestParam(value = "limitCnt", required = false) String limitCnt,
                                                 @RequestParam(value = "orderBy", required = false) String orderBy,
                                                 @RequestParam(value = "staLevels", required = false) String staLevels,
                                                 @RequestParam(value = "eleValueRanges", required = false) String eleValueRanges,
                                                 @RequestParam(value = "adminCodes") String adminCodes,
                                                 @RequestParam(value = "timeRange") String timeRange,
                                                 @RequestParam(value = "staTypes", required = false) String staTypes) {
        return atstationService.getSurfEleByTimeRange(dataFormat, dataCode, elements, limitCnt, orderBy, staLevels, eleValueRanges, adminCodes, timeRange, staTypes);
    }

}
