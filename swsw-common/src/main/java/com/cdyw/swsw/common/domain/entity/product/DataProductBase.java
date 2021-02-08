package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductBase", description = "产品-基础信息实体类")
@Data
public class DataProductBase {
    @ApiModelProperty(name = "id", value = "主键")
    private Integer id;
    @ApiModelProperty(name = "productId", value = "产品id")
    private Integer productId;
    @ApiModelProperty(name = "productType", value = "产品类型(1-7)")
    private Integer productType;
    @ApiModelProperty(name = "createTime", value = "创建时间")
    private Long createTime;
    @ApiModelProperty(name = "baseTime", value = "起报时间")
    private Long baseTime;
    @ApiModelProperty(name = "publishTime", value = "发布时间")
    private Long publishTime;
    @ApiModelProperty(name = "publishDept", value = "发布单位")
    private String publishDept;
    @ApiModelProperty(name = "forecaster", value = "预报员")
    private String forecaster;
    @ApiModelProperty(name = "signer", value = "签发人")
    private String signer;
    @ApiModelProperty(name = "year", value = "年份")
    private Integer year;
    @ApiModelProperty(name = "number", value = "期数")
    private Integer number;
    @ApiModelProperty(name = "subtitle", value = "副标题-Type2")
    private String subtitle;
    @ApiModelProperty(name = "line", value = "线路选择-Type3-'2020年成都马拉松,2021年成都马拉松,2022年成都马拉松'")
    private String line;
    @ApiModelProperty(name = "stadium", value = "场馆名-Type4")
    private String stadium;
    @ApiModelProperty(name = "hours", value = "预报时次-Type5")
    private String hours;
}