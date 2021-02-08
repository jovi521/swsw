package com.cdyw.swsw.common.domain.ao.enums;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductContentParamEnum", description = "生成数据时，指定文档产品概况的默认参数值")
@Getter
public enum DataProductContentParamEnum {
    /**
     * 发布时间
     */
    PARA_PUBLISH_TIME("${paraPublishTime}", "yyyy年MM月dd日HH时mm分"),
    /**
     * 灾害类型
     */
    PARA_DISASTER_TYPE("${paraDisasterType}", "暴雨"),
    /**
     * 预警等级
     */
    PARA_WARNING_LEVEL("${paraWarningLevel}", "红色"),
    /**
     * 灾害类型2
     */
    PARA_DISASTER_TYPE2("${paraDisasterType2}", "大风"),
    /**
     * 预警等级2
     */
    PARA_WARNING_LEVEL2("${paraWarningLevel2}", "红色"),
    /**
     * 已影响区域
     */
    PARA_AFFECTED_AREA("${paraAffectedArea}", "双流区"),
    /**
     * 已出现天气
     */
    PARA_APPEARED_WEATHER("${paraAppearedWeather}", "大风"),
    /**
     * 将出现时间
     */
    PARA_APPEARING_TIME("${paraAppearingTime}", "2小时"),
    /**
     * 将影响区域
     */
    PARA_AFFECTING_AREA("${paraAffectingArea}", "龙泉驿区"),
    /**
     * 灾害现象1
     */
    PARA_DISASTER_PHENOMENON1("${paraDisasterPhenomenon1}", "强降水活动，雨量20-30ml"),
    /**
     * 灾害现象2
     */
    PARA_DISASTER_PHENOMENON2("${paraDisasterPhenomenon2}", "阵风6-8级"),
    /**
     * 已影响体育场馆
     */
    PARA_AFFECTED_STADIUM("${paraAffectedStadium}", "龙泉驿大运会主场馆"),
    /**
     * 已影响体育项目： ①火炬传递运动预报 ②铁人三项赛事预报 ③天气预报
     */
    PARA_AFFECTED_EVENT("${paraAffectedEvent}", "天气"),
    /**
     * 预警开始时间
     */
    PARA_WARNING_START_TIME("${paraWarningStartTime}", "yyyy年MM月dd日HH时mm分"),
    /**
     * 预警结束时间
     */
    PARA_WARNING_END_TIME("${paraWarningEndTime}", "yyyy年MM月dd日HH时mm分"),
    /**
     * 发布单位
     */
    PARA_PUBLISH_DEPT("${paraPublishDept}", "成都市气象台"),
    /**
     * 报送单位
     */
    PARA_SEND_DEPT("${paraSendDept}", "成都市气象局"),
    /**
     * 预报员
     */
    PARA_FORECASTER("${paraForecaster}", "forecaster1"),
    /**
     * 审批人
     */
    PARA_APPROVER("${paraApprover}", "approver1"),
    /**
     * 签发人
     */
    PARA_SIGNER("${paraSigner}", "signer1"),
    /**
     * 期数
     */
    PARA_NUMBER("${paraNumber}", "1");

    private final String key;

    private final String value;

    DataProductContentParamEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
