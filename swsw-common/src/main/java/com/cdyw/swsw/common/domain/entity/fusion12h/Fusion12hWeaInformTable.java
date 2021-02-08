package com.cdyw.swsw.common.domain.entity.fusion12h;

import lombok.Data;

import java.io.Serializable;

/**
 * 融合12小时（”产品“的一种）天气元素信息
 * 产品制作-页面表格显示要的实体类，将很多个产品要素根据预报时间整合成一个时刻所有要素的实体类
 * 分为0-2h和0-12h短临预报，要素包括“温度、风场、降水、湿度、气压、云量”。数据涉及三种模式数据和雷达外推数据的融合(算法)。
 *
 * @author jovi
 */
@Data
public class Fusion12hWeaInformTable implements Serializable {
    /**
     * 温度 元素类型-值
     */
    private String typeTemValue;
    /**
     * 湿度 元素类型-值
     */
    private String typeRhuValue;
    /**
     * 降水 元素类型-值
     */
    private String typeRainValue;
    /**
     * 预报时间
     */
    private String forecastTime;
}
