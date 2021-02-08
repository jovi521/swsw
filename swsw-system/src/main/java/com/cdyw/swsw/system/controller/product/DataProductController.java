package com.cdyw.swsw.system.controller.product;

import com.cdyw.swsw.common.domain.entity.product.DataProduct;
import com.cdyw.swsw.common.domain.entity.product.DataProductContentParam;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.service.product.DataProductService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author jovi
 */
@RestController
@RequestMapping(value = "dataProduct")
public class DataProductController {

    private final DataProductService dataProductService;

    @Autowired
    public DataProductController(DataProductService dataProductService) {
        this.dataProductService = dataProductService;
    }

    @ApiOperation(value = "/getDataByParam", notes = "根据条件查询产品")
    @GetMapping("/getDataByParam")
    public CommonResult<?> getDataByParam(DataProduct dataProduct) {
        return dataProductService.getDataByParam(dataProduct);
    }

    @ApiOperation(value = "/getDataProductDocById", notes = "根据Id查询当前产品的文档")
    @GetMapping("/getDataProductDocById")
    public CommonResult<?> getDataProductDocById(Integer id, HttpServletResponse response, boolean isOnLine) throws IOException, InterruptedException {
        return dataProductService.getDataProductDocById(id, response, isOnLine);
    }

    @ApiOperation(value = "/getDataProductFormById", notes = "根据 dataProductId 查询此产品的表单元素")
    @GetMapping("/getDataProductFormById")
    public CommonResult<?> getDataProductFormById(@ApiParam(name = "id", value = "dataProductId", required = true)
                                                  @RequestParam(value = "id") Integer id) {
        return dataProductService.getDataProductFormById(id);
    }

    @ApiOperation(value = "/preview", notes = "预览当前产品修改后的临时 pdf")
    @PostMapping("/preview")
    public CommonResult<?> preview(@RequestBody DataProductContentParam dataProductContentParam, HttpServletResponse response, boolean isOnLine) throws IOException, InterruptedException {
        return dataProductService.preview(dataProductContentParam, response, isOnLine);
    }

    @ApiOperation(value = "/closePreview", notes = "关闭预览当前产品修改后的临时 pdf ，删除 temp 文件夹下的所有生成文件")
    @GetMapping("/closePreview")
    public CommonResult<?> closePreview(@ApiParam(name = "id", value = "dataProductId", required = true)
                                        @RequestParam(value = "id") Integer id) {
        return dataProductService.closePreview(id);
    }

    @ApiOperation(value = "/publish", notes = "发布当前产品，包括：①产品基础信息、②表单参数、③相关文件存储、④提交至上级审批")
    @GetMapping("/publish")
    public CommonResult<?> publish(DataProductContentParam dataProductContentParam) throws IOException, InterruptedException {
        return dataProductService.publish(dataProductContentParam);
    }

    @ApiOperation(value = "/updateDataProduct", notes = "更新当前产品")
    @GetMapping("/updateDataProduct")
    public CommonResult<?> updateDataProduct(DataProduct dataProduct) {
        return dataProductService.updateDataProduct(dataProduct);
    }

    @ApiOperation(value = "/insert", notes = "创建测试数据")
    @PostMapping("insert")
    public CommonResult<?> insert() throws IOException, InterruptedException {
        return dataProductService.insert();
    }

    @ApiOperation(value = "/getDataProductByParam", notes = "获取产品信息：7种类型产品基础信息、天气信息、历史信息")
    @GetMapping("getDataProductByParam")
    public CommonResult<?> getDataProductByParam(@ApiParam(name = "productType", value = "产品类型(1-7)", required = true)
                                                 @RequestParam(value = "productType") String productType,
                                                 @ApiParam(name = "baseTime", value = "起报时间", required = true)
                                                 @RequestParam(value = "baseTime") String baseTime,
                                                 @ApiParam(name = "lonLat", value = "经度和纬度")
                                                 @RequestParam(value = "lonLat", required = false) String lonLat,
                                                 @ApiParam(name = "flag", value = "0-6H、6-12H，4中按钮的类型，取值依次是1、2。此类型按钮根据不同产品不同选择，如果没有此类型按钮就置空，不管他。")
                                                 @RequestParam(value = "flag", required = false) Integer flag,
                                                 @ApiParam(name = "line", value = "产品3-线路类型：2020年成都马拉松、2021年成都马拉松、2022年成都马拉松")
                                                 @RequestParam(value = "line", required = false) String line) {
        return dataProductService.getDataProductByParam(productType, baseTime, lonLat, flag, line);
    }

    @ApiOperation(value = "/handleData", notes = "根据产品类型-处理产品数据，制作产品")
    @PostMapping("handleData")
    public CommonResult<?> handleData(@ApiParam(name = "data", value = "产品基础信息-天气文本信息-预览、提交、下载标识（1， 2， 3），0-6H合成：4（所有产品”合成“按钮类型默认为都是这个）、6-12H合成：5", required = true)
                                      @RequestBody Object data, HttpServletResponse response) {
        return dataProductService.handleData(data, response);
    }

    @ApiOperation(value = "/upload", notes = "根据产品类型和起报时间-上传产品(doc)：注意此处不光要上传简单的文件，而且要上传产品的-基础发布信息，所以不能用 MultipartFile 接收，采用 HttpServletRequest")
    @PostMapping("upload")
    public CommonResult<?> upload(HttpServletRequest request) {
        return dataProductService.upload(request);
    }
}
