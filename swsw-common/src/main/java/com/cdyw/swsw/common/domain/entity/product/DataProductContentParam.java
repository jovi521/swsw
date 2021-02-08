package com.cdyw.swsw.common.domain.entity.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductContentParam", description = "①创建数据时候生成的用来拼接滚动显示的 content 的各个参数 ②发布页面负责替换 word 文档里面 content 的各个参数")
@Data
public class DataProductContentParam implements Serializable {
    @ApiModelProperty(name = "id", value = "主键", notes = "参数", position = 1)
    private Integer id;
    @ApiModelProperty(name = "dataProductId", value = "dataProduct的 id", notes = "参数", position = 2)
    private Integer dataProductId;
    @ApiModelProperty(name = "paraPublishTime", value = "发布时间", notes = "参数", position = 3)
    private String paraPublishTime;
    @ApiModelProperty(name = "paraDisasterType", value = "灾害类型", notes = "参数", position = 4)
    private String paraDisasterType;
    @ApiModelProperty(name = "paraWarningLevel", value = "预警等级", notes = "参数", position = 5)
    private String paraWarningLevel;
    @ApiModelProperty(name = "paraDisasterType2", value = "灾害类型2", notes = "参数", position = 22)
    private String paraDisasterType2;
    @ApiModelProperty(name = "paraWarningLevel2", value = "预警等级2", notes = "参数", position = 23)
    private String paraWarningLevel2;
    @ApiModelProperty(name = "paraAffectedArea", value = "已影响区域", notes = "参数", position = 6)
    private String paraAffectedArea;
    @ApiModelProperty(name = "paraAppearedWeather", value = "已出现天气", notes = "参数", position = 7)
    private String paraAppearedWeather;
    @ApiModelProperty(name = "paraAppearingTime", value = "将出现时间", notes = "参数", position = 8)
    private String paraAppearingTime;
    @ApiModelProperty(name = "paraAffectingArea", value = "将影响区域", notes = "参数", position = 9)
    private String paraAffectingArea;
    @ApiModelProperty(name = "paraDisasterPhenomenon1", value = "灾害现象1", notes = "参数", position = 10)
    private String paraDisasterPhenomenon1;
    @ApiModelProperty(name = "paraDisasterPhenomenon2", value = "灾害现象2", notes = "参数", position = 11)
    private String paraDisasterPhenomenon2;
    @ApiModelProperty(name = "paraAffectedStadium", value = "已影响体育场馆", notes = "参数", position = 12)
    private String paraAffectedStadium;
    @ApiModelProperty(name = "paraAffectedEvent", value = "已影响体育项目", notes = "参数", position = 13)
    private String paraAffectedEvent;
    @ApiModelProperty(name = "paraWarningStartTime", value = "预警开始时间", notes = "参数", position = 14)
    private String paraWarningStartTime;
    @ApiModelProperty(name = "paraWarningEndTime", value = "预警结束时间", notes = "参数", position = 15)
    private String paraWarningEndTime;
    @ApiModelProperty(name = "paraPublishDept", value = "发布单位", notes = "参数", position = 16)
    private String paraPublishDept;
    @ApiModelProperty(name = "paraSendDept", value = "报送单位", notes = "参数", position = 17)
    private String paraSendDept;
    @ApiModelProperty(name = "paraForecaster", value = "预报员", notes = "参数", position = 18)
    private String paraForecaster;
    @ApiModelProperty(name = "paraApprover", value = "审批人", notes = "参数", position = 19)
    private String paraApprover;
    @ApiModelProperty(name = "paraSigner", value = "签发人", notes = "参数", position = 20)
    private String paraSigner;
    @ApiModelProperty(name = "paraNumber", value = "期数", notes = "参数", position = 21)
    private Integer paraNumber;
}
