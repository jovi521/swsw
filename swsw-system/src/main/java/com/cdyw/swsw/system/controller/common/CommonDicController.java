package com.cdyw.swsw.system.controller.common;

import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.common.CommonDicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jovi
 */
@Api(value = "CommonDicController")
@RestController
@RequestMapping("dic")
public class CommonDicController {

    private final CommonDicService commonDicService;

    public CommonDicController(CommonDicService commonDicService) {
        this.commonDicService = commonDicService;
    }

    @ApiOperation(value = "getLonLatByPara", notes = "根据页面传来的经度(lon)和纬度(lat)返回json，新增支持地点名查询，也可以查询所有站点类型")
    @GetMapping("getLonLatByPara")
    public CommonResult<?> getLonLatByPara(@RequestParam(value = "lon", required = false) String lon,
                                           @RequestParam(value = "lat", required = false) String lat,
                                           @RequestParam(value = "location", required = false) String location,
                                           @RequestParam(value = "type", required = false, defaultValue = "1") Integer type,
                                           @RequestParam(value = "parentCode", required = false) String parentCode) {
        return commonDicService.getLonLatByPara(lon, lat, location, type, parentCode);
    }


    @ApiOperation(value = "getAllTrans", notes = "返回所有透明度list-map")
    @GetMapping("getAllTrans")
    public CommonResult<?> getAllTrans() {
        return commonDicService.getAllTrans();
    }

    @ApiOperation(value = "getAllHeight", notes = "返回所有的高度(未来可能不局限于风场)")
    @GetMapping("/getAllHeight")
    public CommonResult<?> getAllHight(@RequestParam(value = "productCode", required = false) String productCode,
                                       @RequestParam(value = "parentCode", required = false) String parentCode) {
        return commonDicService.getAllHeight(productCode, parentCode);
    }

    @ApiOperation(value = "getRoadByStation", notes = "查询两个场馆(站点之间的路线)")
    @GetMapping("/getRoadByStation")
    public CommonResult<?> getRoadByStation(@RequestParam(value = "startStation", required = false) Integer startStation,
                                            @RequestParam(value = "endStation", required = false) Integer endStation) {
        return commonDicService.getRoadByStation(startStation, endStation);
    }

    @GetMapping("/getLonlatGrid")
    public void getLonlatGrid(@RequestParam("productType") String productType, HttpServletResponse response) throws IOException {
        commonDicService.getLonlatGrid(productType, response);
    }

    @GetMapping("/getColorFile")
    public CommonResult<?> getColorFile(@RequestParam("parentCode") String parentCode,
                                        @RequestParam(value = "productCode", required = false) String productCode, HttpServletResponse response) throws IOException {
        return commonDicService.getColorFile(parentCode, productCode, response);
    }
}
