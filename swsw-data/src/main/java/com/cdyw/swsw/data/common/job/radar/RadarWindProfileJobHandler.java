package com.cdyw.swsw.data.common.job.radar;

import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.radar.RadarWindProfileService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 风廓线雷达定时器
 *
 * @author jovi
 */
@Slf4j
@Component
public class RadarWindProfileJobHandler extends IJobHandler {

    private final RadarWindProfileService radarWindProfileService;

    private final CommonUrlParam commonUrlParam;

    @Autowired
    public RadarWindProfileJobHandler(RadarWindProfileService radarWindProfileService, CommonUrlParam commonUrlParam) {
        this.radarWindProfileService = radarWindProfileService;
        this.commonUrlParam = commonUrlParam;
    }

    /**
     * 将被执行的风廓线相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "radarWindProfileJobHandler", init = "init", destroy = "destroy")
    @Override
    public ReturnT<String> execute(String param) {
        CommonResult<?> surfEleInRegionByTimeRange = radarWindProfileService.getUparFileByTimeRangeAndStaID(commonUrlParam.getDataFormat(), commonUrlParam.getDataCodeRadarWindProfile(), commonUrlParam.getElementsRadarWindProfile(), commonUrlParam.getLimitCnt(), commonUrlParam.getOrderBy(), commonUrlParam.getStaIdRadarWeather(), commonUrlParam.getEleValueRanges(), getTimeRange(), commonUrlParam.getStaTypes());
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
