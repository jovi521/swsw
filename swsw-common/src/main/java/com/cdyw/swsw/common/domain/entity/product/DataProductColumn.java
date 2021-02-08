package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductColumn", description = "连接 dataProduct 和 column 类的中间类")
@Data
public class DataProductColumn implements Serializable {
    @ApiModelProperty(name = "id", value = "主键", notes = "参数", position = 1)
    private Integer id;
    @ApiModelProperty(name = "dataProductId", value = "dataProduct 的 id", notes = "参数", position = 2)
    private Integer dataProductId;
    @ApiModelProperty(name = "columnId", value = "column 的 id", notes = "参数", position = 3)
    private String columnIds;
}
