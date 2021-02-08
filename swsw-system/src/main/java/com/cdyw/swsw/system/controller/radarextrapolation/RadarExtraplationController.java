package com.cdyw.swsw.system.controller.radarextrapolation;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.radarextrapolation.RadarExtraplationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/radarExtraplation")
public class RadarExtraplationController {

    @Autowired
    private RadarExtraplationService radarExtraplationService;

    @RequestMapping("/getDataByParam")
    @ResponseBody
    public CommonResult<?> getDataByParam(@RequestParam("parentCode") String parentCode,
                                          @RequestParam("stationId") String stationId,
                                          @RequestParam("productType") String productType,
                                          @RequestParam("time") String time) {
        return radarExtraplationService.getDataByParam(parentCode, stationId, productType, time);
    }


}
