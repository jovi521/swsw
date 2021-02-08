package com.cdyw.swsw.system.controller.grid;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.grid.D3GridFusionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * @author jovi
 */
@RestController
@RequestMapping(value = "/3dGridFusion")
public class D3GridFusionController {

    private final D3GridFusionService d3GridFusionService;

    @Autowired
    public D3GridFusionController(D3GridFusionService d3GridFusionService) {
        this.d3GridFusionService = d3GridFusionService;
    }

    @ApiOperation(value = "getDataParseByTypeAndTime", notes = "先返回所查询到的txt文件转化成json格式文件，拼接一个文件name属性")
    @GetMapping(value = "/getDataParseByTypeAndTime")
    public CommonResult<?> getDataParseByTypeAndTime(@RequestParam("type")
                                                     @ApiParam(required = true, value = "16：IVAP：风场、601：RHU：相对湿度、602：PRS：气压、603：TEM：温度")
                                                     @NotNull(message = "type can not be null")
                                                     @Valid String type,
                                                     @RequestParam("startTime") String startTime,
                                                     @RequestParam("endTime") String endTime,
                                                     @RequestParam("hight") String hight) {
        return d3GridFusionService.getDataParseByTypeAndTime(type, startTime, endTime, hight);
    }

    @GetMapping(value = "/getFileParseByFileName")
    public void getFileParseByFileName(@RequestParam("type") String type, @RequestParam("fileName") String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        d3GridFusionService.getFileParseByFileName(type, fileName, response, isOnLine);
    }

}
