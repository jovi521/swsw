package com.cdyw.swsw.data.controller.fusion;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.service.fusion.FusionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 0-2小时融合对外接口
 *
 * @author jovi
 */
@Api(tags = {"融合数据统一接口"})
@RestController
@RequestMapping(value = "/fusion")
public class FusionController {

    private final FusionService fusionService;

    @Autowired
    public FusionController(FusionService fusionService) {
        this.fusionService = fusionService;
    }

    @ApiOperation(value = "getFusionsByParam", notes = "通过参数查询融合数据所需要的数据源")
    @GetMapping("getFusionsByParam")
    public CommonResult<?> getFusionsByParam(@ApiParam(name = "type", value = "类型（GRAPES_3KM ? SWC_WARM ? SWC_WARR）之一", required = true) String type,
                                             @ApiParam(name = "time", value = "时刻（以秒为单位的时间戳）", required = true) String time) {
        return fusionService.getFusionsByParam(type, time);
    }
}
