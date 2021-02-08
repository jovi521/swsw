package com.cdyw.swsw.system.controller.forecastcheck;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.forecastcheck.ForecastCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cdyw
 * @description
 */
@RestController
@RequestMapping("/forecastCheck")
public class ForecastCheckController {

    @Autowired
    private ForecastCheckService forecastCheckService;

    @RequestMapping("/getDataByParam")
    public CommonResult<?> getDataByParam(@RequestParam("categoryId") String categoryId, @RequestParam("evaluateId") String evaluateId,
                                          @RequestParam("productId") String productId, @RequestParam("startTime") Long startTime,
                                          @RequestParam("endTime") Long endTime, @RequestParam("timeCount") Integer timeCount,
                                          @RequestParam("threshold") Float threshold) {
        return forecastCheckService.getDataByParam(categoryId, evaluateId, productId, startTime, endTime, timeCount, threshold);
    }


}
