package com.cdyw.swsw.data.controller.radar;

import cn.shuibo.annotation.Encrypt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.radar.RadarWindProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Encrypt
    @GetMapping(value = "/getUparFileByTimeRangeAndStaID")
    public CommonResult<?> getUparFileByTimeRangeAndStaID(@RequestParam("dataFormat") String dataFormat,
                                                          @RequestParam("dataCode") String dataCode,
                                                          @RequestParam("elements") String elements,
                                                          @RequestParam(value = "limitCnt", required = false) String limitCnt,
                                                          @RequestParam(value = "orderBy", required = false) String orderBy,
                                                          @RequestParam("staIds") String staIds,
                                                          @RequestParam(value = "eleValueRanges", required = false) String eleValueRanges,
                                                          @RequestParam(value = "timeRange") String timeRange,
                                                          @RequestParam(value = "staTypes", required = false) String staTypes) {
        return radarWindProfileService.getUparFileByTimeRangeAndStaID(dataFormat, dataCode, elements, limitCnt, orderBy, staIds, eleValueRanges, timeRange, staTypes);
    }
}
