package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductWeaGridForecast", description = "产品-天气信息-精细化网格预报-线路格点预报-实体类")
@Data
public class DataProductWeaGridForecast {
    @ApiModelProperty(name = "id", value = "主键")
    private Integer id;
    @ApiModelProperty(name = "productId", value = "产品id")
    private Integer productId;
    @ApiModelProperty(name = "text", value = "文本")
    private String text;
}
