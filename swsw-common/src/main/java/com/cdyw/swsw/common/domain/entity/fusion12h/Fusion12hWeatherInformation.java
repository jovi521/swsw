package com.cdyw.swsw.common.domain.entity.fusion12h;

import lombok.Data;

import java.io.Serializable;

/**
 * 融合12小时（”产品“的一种）天气元素信息
 * 分为0-2h和0-12h短临预报，要素包括“温度、风场、降水、湿度、气压、云量”。数据涉及三种模式数据和雷达外推数据的融合(算法)。
 *
 * @author jovi
 */
@Data
public class Fusion12hWeatherInformation implements Serializable {

    private Integer id;

    /**
     * 天气元素类型
     */
    private String type;
    /**
     * 天气元素类型-值
     */
    private String typeValue;
    /**
     * 天气元素类型-字符
     */
    private String typeChar;
    /**
     * 天气类型：晴天、阴天、雨天（云量）
     */
    private String data;
    /**
     * 预报时次
     */
    private Integer forecastCount;
    /**
     * 预报时间
     */
    private Long forecastTime;
    /**
     * 起报时间
     */
    private Long baseTime;
    /**
     * 经纬度对应的索引（根据此索引找到对应天气元素的值）
     */
    private Integer lonLatIndex;
    /**
     *
     */
    private Boolean isdisable;
}
