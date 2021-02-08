package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 12大类的枚举类
 *
 * @author jovi
 */
@Getter
@AllArgsConstructor
public enum TypeEnum {

    // S波段天气雷达
    TYPE_RADAR_WEATHER(1, "RADAR_WEATHER"),

    // X波段双偏振有源相控阵雷达
    TYPE_RADAR_PHASED_ARRAY(2, "RADAR_PHASED_ARRAY"),

    // 风廓线雷达
    TYPE_RADAR_WIND_PROFILE(3, "RADAR_WIND_PROFILE"),

    // 拉曼温廓线雷达
    TYPE_RADAR_RAMANWIN_PROFILE(4, "RADAR_RAMANWIN_PROFILE"),

    // 激光测风雷达
    TYPE_RADAR_WIND_LASER(5, "RADAR_WIND_LASER"),

    // 自动站
    TYPE_AT_STATION(6, "AT_STATION"),

    // 微波辐射计
    TYPE_MICRO_RADIOMETER(7, "MICRO_RADIOMETER"),

    // 卫星资料
    TYPE_SATELLITE(8, "SATELLITE"),

    // 雷电
    TYPE_THUNDER(9, "THUNDER"),

    //  SWC模式数据
    TYPE_SWC(10, "SWC"),

    // GRAPES-Meso等模式资料
    TYPE_GRAPES(11, "GRAPES"),

    // 其他特种观测资料
    TYPE_OTHER(12, "OTHER"),

    // 雷达外推
    TYPE_RADAR_EXT(13, "RADAR_EXT"),

    // 三维格点融合
    TYPE_3D_GRID_FUSION(14, "3D_GRID_FUSION"),

    // 短临预报
    TYPE_SHORT_FORECAST(15, "SHORT_FORECAST"),

    TYPE_MODE_DATA(16, "MODE_DATA"),

    TYPE_CORRECTION_DATA(17, "CORRECTION_DATA"),

    TYPE_WARNING_SIGNAL_AREA(18, "WARNING_SIGNAL_AREA"),

    TYPE_CLDAS_1KM(19, "CLDAS_1KM"),

    TYPE_ECMWF_HR(20, "ECMWF_HR"),

    TYPE_GRAPES_3KM(21, "GRAPES_3KM"),

    TYPE_SWC_WARM(22, "SWC_WARM"),

    TYPE_SWC_WARR(23, "SWC_WARR"),

    // 0-2小时融合
    TYPE_FUSION_2H(24, "FUSION_2H"),

    // 0-12小时融合
    TYPE_FUSION_12H(25, "FUSION_12H"),

    // 天气雷达外推
    TYPE_WEATHER_RADAR_EXTRAPOLATION(26, "WEATHER_RADAR_EXTRAPOLATION"),

    // 相控阵雷达外推
    TYPE_PHASEDARRAY_RADAR_EXTRAPOLATION(27, "PHASEDARRAY_RADAR_EXTRAPOLATION"),

    TYPE_3D_WINDFIELD_INVERSION(28, "3D_WINDFIELD_INVERSION");

    private final Integer type;

    private final String name;

}
