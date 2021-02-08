package com.cdyw.swsw.data.common.job.atstation;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.atstation.AtstationService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 自动站定时器
 *
 * @author jovi
 */
@Slf4j
@Component
public class AtstationJobHandler extends IJobHandler {

    private final AtstationService atstationService;

    @Autowired
    public AtstationJobHandler(AtstationService atstationService) {
        this.atstationService = atstationService;
    }

    /**
     * 将被执行的自动站相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "atstationJobHandler", init = "init", destroy = "destroy")
    @Override
    public ReturnT<String> execute(String param) {
        // 注意：此处使用国际时区，比本地时间少将近8个小时
        CommonResult<?> atStationBase = atstationService.getAtStationBase();
        if (atStationBase != null) {
            new ReturnT<>(atStationBase.toString());
        }
        return ReturnT.FAIL;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }
}
