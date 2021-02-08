package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 产品（文档）12种类型枚举类
 *
 * @author jovi
 */
@AllArgsConstructor
@Getter
public enum DataProductTypeEnum {
    /**
     * SWSW-0-2小时-短历时暴雨/强降水警报
     */
    TYPE_SWSW_2_1(1, "短历时暴雨/强降水警报"),
    /**
     * SWSW-0-2小时-强雷电警报
     */
    TYPE_SWSW_2_2(2, "强雷电警报"),
    /**
     * SWSW-0-2小时-雷暴大风警报
     */
    TYPE_SWSW_2_3(3, "雷暴大风警报"),
    /**
     * SWSW-0-2小时-冰雹警报
     */
    TYPE_SWSW_2_4(4, "冰雹警报"),
    /**
     * SWSW-0-2小时-开闭幕式预报
     */
    TYPE_SWSW_2_5(5, "开闭幕式预报"),
    /**
     * SWSW-0-2小时-火炬传递
     */
    TYPE_SWSW_2_6(6, "火炬传递"),
    /**
     * SWSW-0-2小时-特种项目气象临近预报
     */
    TYPE_SWSW_2_7(7, "特种项目气象临近预报"),
    /**
     * SWSW-0-2小时-临近天气预报
     */
    TYPE_SWSW_2_8(8, "临近天气预报"),
    /**
     * SWSW-0-12小时-0-12h赛事逐小时预报产品
     */
    TYPE_SWSW_12_9(9, "0-12h赛事逐小时预报产品"),
    /**
     * SWSW-0-12小时-0-6h逐小时短临天气
     */
    TYPE_SWSW_12_10(10, "0-6h逐小时短临天气"),
    /**
     * SWSW-0-12小时-强天气预警信号产品
     */
    TYPE_SWSW_12_11(11, "强天气预警信号产品"),
    /**
     * SWSW-0-12小时-其他特殊预报
     */
    TYPE_SWSW_12_12(12, "其他特殊预报"),
    /**
     * 常规短临预报
     */
    PRODUCT_TYPE_SHORT_FORECAST(1, "常规短临预报"),
    /**
     * 精细化网格预报
     */
    PRODUCT_TYPE_GRID_FORECAST(2, "精细化网格预报"),
    /**
     * 线路预报
     */
    PRODUCT_TYPE_LINE_FORECAST(3, "线路预报"),
    /**
     * 赛事场馆警报
     */
    PRODUCT_TYPE_STADIUM_WARNING(4, "赛事场馆警报"),
    /**
     * 灾害性短临
     */
    PRODUCT_TYPE_DISASTER_FORECAST(5, "灾害性短临"),
    /**
     * 不定时短临
     */
    PRODUCT_TYPE_IRREGULAR_SHORT(6, "不定时短临"),
    /**
     * 预警信号
     */
    PRODUCT_TYPE_WARNING_SIGNAL(7, "预警信号");

    private final Integer type;

    private final String typeName;
}
