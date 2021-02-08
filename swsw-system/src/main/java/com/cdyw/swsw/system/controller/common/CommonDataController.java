package com.cdyw.swsw.system.controller.common;

import com.cdyw.swsw.common.domain.entity.commondata.ThresholdVo;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.common.CommonDataService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * @author jovi
 */
@RestController
@RequestMapping("common")
public class CommonDataController {

    private final CommonDataService commonDataService;

    public CommonDataController(CommonDataService commonDataService) {
        this.commonDataService = commonDataService;
    }

    /**
     * 数据监控
     *
     * @return CommonResult
     */
    @GetMapping("getDataMonitor")
    public CommonResult<?> getDataMonitor() {
        return commonDataService.getDataMonitor();
    }

    /**
     * 获取检测报警的数据
     *
     * @return CommonResult
     */
    @ApiOperation(value = "getDataMonitorAlarm", notes = "获取监测报警的数据：不传参数，返回三大产品所有数据，前端定时访问")
    @GetMapping("getDataMonitorAlarm")
    public CommonResult<?> getDataMonitorAlarm() {
        return commonDataService.getDataMonitorAlarm();
    }

    /**
     * 获取用户设置的监测预警阈值
     *
     * @return CommonResult
     */
    @ApiOperation(value = "getThreshold", notes = "获取用户设置的监测预警阈值")
    @GetMapping("getThreshold")
    public CommonResult<?> getThreshold(@RequestParam("userId") String userId) {
        return commonDataService.getThreshold(userId);
    }

    /**
     * 修改用户设置的监测预警阈值
     *
     * @param userId
     * @param type
     * @param threshold
     * @param flag
     * @return
     */
//    @ApiOperation(value = "modifyThreshold", notes = "修改用户设置的监测预警阈值")
//    @GetMapping("modifyThreshold")
//    public CommonResult modifyThreshold(@RequestParam("userId") String userId, @RequestParam("type") String type,
//                                        @RequestParam("threshold") String threshold, @RequestParam("flag") String flag) {
//        return commonDataService.modifyThreshold(userId, type, threshold, flag);
//    }

    /**
     * 修改用户设置的监测预警阈值
     * @param threshold
     * @return
     */
    @ApiOperation(value = "modifyThreshold", notes = "修改用户设置的监测预警阈值")
    @PostMapping("modifyThreshold")
    public CommonResult modifyThreshold(@RequestBody ThresholdVo threshold) {
        return commonDataService.modifyThreshold(threshold);
    }

}
