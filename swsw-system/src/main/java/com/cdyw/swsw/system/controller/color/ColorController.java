package com.cdyw.swsw.system.controller.color;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.color.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/color")
public class ColorController {

    @Autowired
    private ColorService colorService;

    /**
     * 根据产品类型查询对应的色标
     *
     * @param productType
     * @return
     */
    @GetMapping(value = "/getColorByProductType")
    public CommonResult<?> getColorByProductType(@RequestParam(value = "productType", required = false) String productType,
                                                 @RequestParam(value = "parentCode", required = true) String parentCode) {
        return colorService.getColorByProductType(productType, parentCode);
    }


}
