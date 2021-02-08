package com.cdyw.swsw.data.common.job.radar;

import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.radar.RadarWeatherService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 天气雷达定时器
 *
 * @author jovi
 */
@Slf4j
@Component
public class RadarWeatherJobHandler extends IJobHandler {

    private final RadarWeatherService radarWeatherService;

    private final CommonUrlParam commonUrlParam;

    @Autowired
    public RadarWeatherJobHandler(RadarWeatherService radarWeatherService, CommonUrlParam commonUrlParam) {
        this.radarWeatherService = radarWeatherService;
        this.commonUrlParam = commonUrlParam;
    }

    /**
     * 将被执行的自动站相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "radarWeatherJobHandler", init = "init", destroy = "destroy")
    @Override
    public ReturnT<String> execute(String param) {
        CommonResult<?> surfEleInRegionByTimeRange = radarWeatherService.getRadaFileByTimeRangeAndStaId(commonUrlParam.getDataFormat(), commonUrlParam.getDataCodeRadarWeather(), commonUrlParam.getElementsRadarWeather(), commonUrlParam.getLimitCnt(), commonUrlParam.getOrderBy(), commonUrlParam.getStaIdRadarWeather(), getTimeRange(), commonUrlParam.getStaTypes());
        if (surfEleInRegionByTimeRange.getData() != null) {
            return ReturnT.SUCCESS;
        } else {
            return ReturnT.FAIL;
        }
    }

    private String getTimeRange() {
        String now15minusFormat = DateUtil.format(LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(90), "yyyyMMddHHmmss");
        String nowFormat = DateUtil.format(LocalDateTime.now(ZoneId.of("UTC")), "yyyyMMddHHmmss");
        return "[" + now15minusFormat + "," + nowFormat + "]";
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }
}
