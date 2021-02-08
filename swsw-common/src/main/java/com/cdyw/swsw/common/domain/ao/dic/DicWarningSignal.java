package com.cdyw.swsw.common.domain.ao.dic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@ApiModel(value = "DicWarningSignal", description = "预警信号字典表实体类")
@Data
@Deprecated
public class DicWarningSignal implements Serializable {
    @ApiModelProperty(name = "id", value = "主键")
    private Integer id;
    @ApiModelProperty(name = "signalType", value = "预警类型")
    private Integer signalType;
    @ApiModelProperty(name = "signalColor", value = "预警信号颜色")
    private Integer signalColor;
}