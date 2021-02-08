package com.cdyw.swsw.common.domain.ao.dic;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@ApiModel(value = "DicDataProduct", description = "文档产品的模板类")
@Data
public class DicDataProductContent implements Serializable {
    @ApiModelProperty(name = "id", value = "主键", position = 1)
    private Integer id;
    @ApiModelProperty(name = "type", value = "文档产品类型", position = 2)
    private String type;
    @ApiModelProperty(name = "content", value = "文档产品概览模板", position = 3)
    private String content;
}

