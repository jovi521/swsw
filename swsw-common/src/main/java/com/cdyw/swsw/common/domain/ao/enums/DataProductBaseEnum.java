package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 产品（文档）基础信息默认值枚举类
 *
 * @author jovi
 */
@AllArgsConstructor
@Getter
public enum DataProductBaseEnum {

    /**
     * 副标题
     */
    PRODUCT_BASE_SUBTITLE("精细化场馆未来12小时天气预报"),
    /**
     * 发布单位
     */
    PRODUCT_BASE_PUBLISH_DEPT("成都市气象局"),
    /**
     * 预报员
     */
    PRODUCT_BASE_FORECASTER("张涛"),
    /**
     * 签发人
     */
    PRODUCT_BASE_SIGNER("张涛"),
    /**
     * 时次
     */
    PRODUCT_BASE_HOURS("14:00"),
    /**
     * 期数
     */
    PRODUCT_BASE_NUMBER("1"),
    /**
     * 线路1
     */
    PRODUCT_BASE_LINE1("2020年成都马拉松"),
    /**
     * 线路2
     */
    PRODUCT_BASE_LINE2("2021年成都马拉松"),
    /**
     * 线路3
     */
    PRODUCT_BASE_LINE3("2022年成都马拉松"),
    /**
     * 线路格点1
     */
    PRODUCT_BASE_LINE_LON_LAT1("{'lon':103.9747,'lat':30.5781}"),
    /**
     * 线路格点2
     */
    PRODUCT_BASE_LINE_LON_LAT2("{'lon':103.7672,'lat':31.2106}"),
    /**
     * 线路格点3
     */
    PRODUCT_BASE_LINE_LON_LAT3("{'lon':104.0503,'lat':30.5306}"),
    /**
     * 线路格点4
     */
    PRODUCT_BASE_LINE_LON_LAT4("{'lon':104.6283,'lat':30.7317}"),
    /**
     * 线路格点5
     */
    PRODUCT_BASE_LINE_LON_LAT5("{'lon':103.7700,'lat':30.7581}"),
    /**
     * 天气情况
     */
    PRODUCT_WEA_TEXT("晴天");

    private final String defaultValue;
}
