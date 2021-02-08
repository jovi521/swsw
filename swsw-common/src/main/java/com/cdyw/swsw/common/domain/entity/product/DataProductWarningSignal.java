package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductWarningSignal", description = "产品文档和预警信号中间表实体类")
@Data
@Deprecated
public class DataProductWarningSignal implements Serializable {
    @ApiModelProperty(name = "id", value = "主键")
    private Integer id;
    @ApiModelProperty(name = "dataProductId", value = "DataProduct主键Id")
    private Integer dataProductId;
    @ApiModelProperty(name = "warningSignalId", value = "DicWarningSignal主键Id")
    private Integer warningSignalId;
}