package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductFormBuild", description = "组装 form 表单的交互类")
@Data
public class DataProductFormBuild implements Serializable {
    @ApiModelProperty(name = "id", value = "主键", notes = "参数", position = 1)
    private String id;
    @ApiModelProperty(name = "column", value = "包括 columnKey columnName等信息", notes = "一个dp含有一个dp_form含有多个column", position = 2)
    private Map<String, Object> column;
}
