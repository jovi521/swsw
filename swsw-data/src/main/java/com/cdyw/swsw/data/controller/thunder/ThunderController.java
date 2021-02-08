package com.cdyw.swsw.data.controller.thunder;

import cn.shuibo.annotation.Encrypt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.thunder.ThunderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jovi
 */
@RestController
@RequestMapping(value = "/thunder")
public class ThunderController {

    private final ThunderService thunderService;

    @Autowired
    public ThunderController(ThunderService thunderService) {
        this.thunderService = thunderService;
    }

    @Encrypt
    @GetMapping(value = "/getUparLightEleInRectByTimeRange")
    public CommonResult<?> getUparLightEleInRectByTimeRange(@RequestParam("dataFormat") String dataFormat,
                                                            @RequestParam("dataCode") String dataCode,
                                                            @RequestParam("elements") String elements,
                                                            @RequestParam(value = "limitCnt", required = false) String limitCnt,
                                                            @RequestParam(value = "orderBy", required = false) String orderBy,
                                                            @RequestParam(value = "eleValueRanges", required = false) String eleValueRanges,
                                                            @RequestParam(value = "minLon") String minLon,
                                                            @RequestParam(value = "maxLon") String maxLon,
                                                            @RequestParam(value = "minLat") String minLat,
                                                            @RequestParam(value = "maxLat") String maxLat,
                                                            @RequestParam(value = "timeRange") String timeRange) {
        return thunderService.getUparLightEleInRectByTimeRange(dataFormat, dataCode, elements, limitCnt, orderBy, eleValueRanges, minLon, maxLon, minLat, maxLat, timeRange);
    }
}
