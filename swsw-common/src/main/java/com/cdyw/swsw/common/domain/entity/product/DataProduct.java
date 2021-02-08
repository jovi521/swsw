package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author jovi
 */
@ApiModel(value = "DataProduct", description = "产品文档实体类")
@Data
public class DataProduct implements Serializable {
    @ApiModelProperty(name = "id", value = "主键", position = 1)
    private Integer id;
    @ApiModelProperty(name = "productType", value = "产品文档类型(1-12)", position = 2)
    private Integer productType;
    @ApiModelProperty(name = "productContent", value = "产品文档概要", position = 3)
    private String productContent;
    @ApiModelProperty(name = "productPath", value = "产品文档路径", position = 4)
    private String productPath;
    @ApiModelProperty(name = "createTime", value = "生成时间", position = 5)
    private Long createTime;
    @ApiModelProperty(name = "publishStatus", value = "0-未发布；1-已发布；2-审批中；3-驳回", position = 6)
    private Integer publishStatus;
    @ApiModelProperty(name = "paraWarningLevels", value = "预警等级（由于产品11可能出现多种灾害类型和预警等级，所以为了统一，使用集合存储所有产品的预警等级）", position = 7)
    private List<String> paraWarningLevels;
}