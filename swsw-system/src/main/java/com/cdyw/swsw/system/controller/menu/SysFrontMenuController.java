package com.cdyw.swsw.system.controller.menu;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.menu.SysFrontMenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/frontMenu")
public class SysFrontMenuController {


    @Autowired
    private SysFrontMenuService sysFrontMenuService;

    @ApiOperation(value = "getFrontMenu", notes = "查询展示前台菜单页面")
    @GetMapping("/getFrontMenu")
    @ResponseBody
    public CommonResult<?> getFrontMenu() {
        return sysFrontMenuService.getFrontMenu();
    }
}
