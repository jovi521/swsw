package com.cdyw.swsw.system.controller.menu;

import com.cdyw.swsw.common.domain.entity.menu.Diymode;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.menu.SysDiymodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/diymode")
public class SysDiymodeController {

    @Autowired
    private SysDiymodeService sysDiymodeService;

    @GetMapping("/getDiymode")
    @ResponseBody
    public CommonResult<?> getDiymode(@RequestParam(value = "userId", required = false) Integer userId) {
        return sysDiymodeService.getDiymode(userId);
    }

    @PostMapping("/saveDiymode")
    @ResponseBody
    public CommonResult<?> saveDiymode(Diymode diymode, String userId) {
        return sysDiymodeService.saveDiymode(diymode, userId);
    }
}
