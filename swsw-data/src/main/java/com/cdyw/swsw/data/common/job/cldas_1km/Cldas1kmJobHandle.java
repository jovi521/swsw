package com.cdyw.swsw.data.common.job.cldas_1km;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.cldas_1km.Cldas1kmService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * cldas1km定时器
 *
 * @author liudong
 * @modified jovi
 */
@Component
@Slf4j
public class Cldas1kmJobHandle {

    private final Cldas1kmService cldas1kmService;

    @Autowired
    public Cldas1kmJobHandle(Cldas1kmService cldas1kmService) {
        this.cldas1kmService = cldas1kmService;
    }

    /**
     * 将被执行的cldas1km1Hrain数据采集相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "cldas1km1HrainJobHandler")
    public ReturnT<String> executeCldas1km1Hrain(String param) {
        cldas1kmService.getCldasData("rain1h");
        return ReturnT.SUCCESS;
    }

    /**
     * 将被执行的cldas1km1Hrain数据采集相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "cldas1kmTemJobHandler")
    public ReturnT<String> executeCldas1kmTemJobHandle(String param) {
        cldas1kmService.getCldasData("tem");
        return ReturnT.SUCCESS;
    }

    /**
     * 将被执行的cldas1km1Hrain数据采集相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "cldas1kmRhuJobHandler")
    public ReturnT<String> executecldas1kmRhuJobHandler(String param) {
        cldas1kmService.getCldasData("rhu");
        return ReturnT.SUCCESS;
    }

    /**
     * Cimiss 获取 base 数据
     */
    @XxlJob(value = "cldas1kmJobHandler")
    public ReturnT<String> executeCldas1kmJobHandle(String param) {
        CommonResult<?> cldas1kmBase = cldas1kmService.getCldas1kmBase();
        if (cldas1kmBase != null) {
            new ReturnT<>(cldas1kmBase.toString());
        }
        return ReturnT.FAIL;
    }
}
