package com.cdyw.swsw.system.controller.cldas1km;

import com.cdyw.swsw.common.domain.entity.cldas1km.Cldas1kmVo;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.cldas1km.Cldas1kmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/cldas1km")
public class Cldas1kmController {

    @Autowired
    private Cldas1kmService cldas1kmService;

    @PostMapping("/getDataByParam")
    @ResponseBody
    public CommonResult<?> getDataByParam(@RequestBody Cldas1kmVo cldas1kmVo) {
        List<String> productList = cldas1kmVo.getProductList();
        String time = cldas1kmVo.getTime();
        return cldas1kmService.getDataByParam(productList, time);
    }

}
