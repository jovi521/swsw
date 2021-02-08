package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductWeaGridForecast", description = "产品-天气信息-常规短临预报实体类")
@Data
public class DataProductWeaShortForecast {
    @ApiModelProperty(name = "id", value = "主键")
    private Integer id;
    @ApiModelProperty(name = "productId", value = "产品id")
    private Integer productId;
    @ApiModelProperty(name = "temMin6", value = "0-6H温度Min")
    private Float temMin6;
    @ApiModelProperty(name = "temMin12", value = "6-12H温度Min")
    private Float temMin12;
    @ApiModelProperty(name = "temMax6", value = "0-6H温度Max")
    private Float temMax6;
    @ApiModelProperty(name = "temMax12", value = "6-12H温度Max")
    private Float temMax12;
    @ApiModelProperty(name = "winSpeedMin6", value = "0-6H风速Min")
    private Float winSpeedMin6;
    @ApiModelProperty(name = "winSpeedMin12", value = "6-12H风速Min")
    private Float winSpeedMin12;
    @ApiModelProperty(name = "winSpeedMax6", value = "0-6H风速Max")
    private Float winSpeedMax6;
    @ApiModelProperty(name = "winSpeedMax12", value = "6-12H风速Max")
    private Float winSpeedMax12;
    @ApiModelProperty(name = "winDirection6", value = "0-6H风向")
    private String winDirection6;
    @ApiModelProperty(name = "winDirection12", value = "6-12H风向")
    private String winDirection12;
    @ApiModelProperty(name = "text6", value = "0-6H天气文本")
    private String text6;
    @ApiModelProperty(name = "text12", value = "6-12H天气文本")
    private String text12;
    @ApiModelProperty(name = "textFusion6", value = "0-6H合成文本")
    private String textFusion6;
    @ApiModelProperty(name = "textFusion12", value = "6-12H合成文本")
    private String textFusion12;
}
