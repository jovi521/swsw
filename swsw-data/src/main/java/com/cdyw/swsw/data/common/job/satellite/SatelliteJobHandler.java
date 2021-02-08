package com.cdyw.swsw.data.common.job.satellite;

import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.satellite.SatelliteService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 卫星定时器
 *
 * @author jovi
 */
@Slf4j
@Component
public class SatelliteJobHandler {

    private final SatelliteService satelliteService;

    private final CommonUrlParam commonUrlParam;

    @Autowired
    public SatelliteJobHandler(SatelliteService satelliteService, CommonUrlParam commonUrlParam) {
        this.satelliteService = satelliteService;
        this.commonUrlParam = commonUrlParam;
    }

    /**
     * 将被执行的自动站相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "satelliteJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> execute(String param) {
        CommonResult<?> surfEleInRegionByTimeRange = satelliteService.getSateFileByTimeRange(commonUrlParam.getDataFormat(), commonUrlParam.getDataCodeSatellite(), commonUrlParam.getElementsSatellite(), commonUrlParam.getLimitCnt(), commonUrlParam.getOrderBy(), commonUrlParam.getEleValueRanges(), getTimeRange());
        if (surfEleInRegionByTimeRange.getData() != null) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    /**
     * 将被执行的卫星通道12数据采集相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "satelliteChannel12JobHandler")
    public ReturnT<String> executeSatelliteChannel12(String param) {
        satelliteService.getSatelliteData("c012");
        return ReturnT.SUCCESS;
    }

    /**
     * 将被执行的卫星通道12数据采集相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "satelliteChannel02JobHandler")
    public ReturnT<String> executeSatelliteChannel02(String param) {
        satelliteService.getSatelliteData("c002");
        return ReturnT.SUCCESS;
    }


    private String getTimeRange() {
        String now15minusFormat = DateUtil.format(LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(90), "yyyyMMddHHmmss");
        String nowFormat = DateUtil.format(LocalDateTime.now(ZoneId.of("UTC")), "yyyyMMddHHmmss");
        return "[" + now15minusFormat + "," + nowFormat + "]";
    }

    public void init() {
    }

    public void destroy() {
    }
}
