package com.cdyw.swsw.system.controller.systemuser;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.user.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author cdyw
 * @description
 */
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;


    @RequestMapping("/getUserByDepartmentId")
    @ResponseBody
    public CommonResult<?> getUserByDepartmentId(@RequestParam(value = "departmentId", defaultValue = "1") Integer departmentId) {
        Map<String, Object> resultMap = sysUserService.getUserByDepartmentId(departmentId);
        return CommonResult.success(resultMap);
    }
}
