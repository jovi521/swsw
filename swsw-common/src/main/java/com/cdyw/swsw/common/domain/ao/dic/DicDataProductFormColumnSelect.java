package com.cdyw.swsw.common.domain.ao.dic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@ApiModel(value = "DicDataProductFormColumnSelect", description = "文档产品的组装表单所需要的列名选项的模板类")
@Data
public class DicDataProductFormColumnSelect implements Serializable {
    @ApiModelProperty(name = "id", value = "主键", notes = "参数", position = 1)
    private String id;
    @ApiModelProperty(name = "columnId", value = "列Id", notes = "参数", position = 2)
    private Integer columnId;
    @ApiModelProperty(name = "valueSelect", value = "列选项", notes = "参数", position = 3)
    private String columnSelect;
    @ApiModelProperty(name = "valueStadiumSelect", value = "体育场馆列选项", notes = "参数", position = 4)
    private String columnStadiumSelect;
    @ApiModelProperty(name = "selectFlag", value = "选择框的类型，1：单选下拉框，2：多选下拉框，3：文本输入框，4：时间选择框", notes = "参数", position = 5)
    private String selectFlag;
}
