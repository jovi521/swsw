package com.cdyw.swsw.system.controller.windfieldinversion;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.windfieldinversion.WindfieldInversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/windfieldInversion")
public class WindfieldInversionController {

    private final WindfieldInversionService windfieldInversionService;

    @Autowired
    public WindfieldInversionController(WindfieldInversionService windfieldInversionService) {
        this.windfieldInversionService = windfieldInversionService;
    }


    @GetMapping(value = "/getDataByTypeAndTime")
    public CommonResult<?> getDataByTypeAndTime(@RequestParam(value = "type") String type,
                                                @RequestParam(value = "time") String time,
                                                @RequestParam(value = "layer", required = false) String layer) {
        return windfieldInversionService.getDataByTypeAndTime(type, time, layer);
    }


}
