package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 融合12小时天气状况(根据三级产品衍生出来的)枚举类
 *
 * @author jovi
 */
@AllArgsConstructor
@Getter
public enum Fusion12hWeatherStatusEnum {

    /**
     * 总云量小于等于30为晴
     */
    WEA_STATUS_CLOUD_SUN("2510", "晴"),
    /**
     * 总云量介于30-90
     */
    WEA_STATUS_CLOUD_CLOUD("2511", "多云"),
    /**
     * 总云量大于90
     */
    WEA_STATUS_CLOUD_DULL("2512", "阴天"),
    /**
     *
     */
    WEA_STATUS_CLOUD_SMALL_RAIN("2513", "小雨"),
    /**
     *
     */
    WEA_STATUS_CLOUD_MIDDLE_RAIN("2514", "中雨"),
    /**
     *
     */
    WEA_STATUS_CLOUD_LARGER_RAIN("2515", "大雨"),
    /**
     *
     */
    WEA_STATUS_CLOUD_SNOW("2516", "下雪"),
    /**
     *
     */
    WEA_STATUS_WIND_NORTH("2517", "北风"),
    /**
     *
     */
    WEA_STATUS_WIND_SOUTH("2517", "南风"),
    /**
     *
     */
    WEA_STATUS_WIND_WEST("2517", "西风"),
    /**
     *
     */
    WEA_STATUS_WIND_EAST("2517", "东风");

    private final String weatherStatus;

    private final String weatherName;
}
