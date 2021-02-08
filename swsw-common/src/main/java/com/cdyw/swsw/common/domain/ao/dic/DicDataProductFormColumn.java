package com.cdyw.swsw.common.domain.ao.dic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@ApiModel(value = "DicDataProductFormColumn", description = "文档产品的组装表单所需要的列名的模板类")
@Data
public class DicDataProductFormColumn implements Serializable {
    @ApiModelProperty(name = "id", value = "主键", notes = "参数", position = 1)
    private String id;
    @ApiModelProperty(name = "columnKey", value = "列对应的key", notes = "参数", position = 2)
    private String columnKey;
    @ApiModelProperty(name = "columnName", value = "列名", notes = "参数", position = 3)
    private String columnName;
    @ApiModelProperty(name = "dataProductType", value = "dataProduct 的 type", notes = "参数", position = 4)
    private String dataProductType;
}
