package com.cdyw.swsw.common.domain.ao.enums;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

/**
 * @author jovi
 */
@Getter
@ApiModel(value = "WarningSignalEnum", description = "预警信号枚举类")
@Deprecated
public enum WarningSignalEnum {
    @ApiModelProperty(name = "WS_WIND", value = "1")
    WS_WIND(1),
    @ApiModelProperty(name = "WS_THUNDER", value = "2")
    WS_THUNDER(2),
    @ApiModelProperty(name = "WS_RAINSTORM", value = "3")
    WS_RAINSTORM(3),
    @ApiModelProperty(name = "WS_HIGHTEMP", value = "4")
    WS_HIGHTEMP(4),
    @ApiModelProperty(name = "WS_HAIL", value = "5")
    WS_HAIL(5),
    @ApiModelProperty(name = "WS_WIND_BLUE", value = "11")
    WS_WIND_BLUE(11),
    @ApiModelProperty(name = "WS_WIND_YELLOW", value = "12")
    WS_WIND_YELLOW(12),
    @ApiModelProperty(name = "WS_WIND_ORANGE", value = "13")
    WS_WIND_ORANGE(13),
    @ApiModelProperty(name = "WS_WIND_RED", value = "14")
    WS_WIND_RED(14),
    @ApiModelProperty(name = "WS_THUNDER_YELLOW", value = "22")
    WS_THUNDER_YELLOW(22),
    @ApiModelProperty(name = "WS_THUNDER_ORANGE", value = "23")
    WS_THUNDER_ORANGE(23),
    @ApiModelProperty(name = "WS_THUNDER_RED", value = "24")
    WS_THUNDER_RED(24),
    @ApiModelProperty(name = "WS_RAINSTORM_BLUE", value = "31")
    WS_RAINSTORM_BLUE(31),
    @ApiModelProperty(name = "WS_RAINSTORM_YELLOW", value = "32")
    WS_RAINSTORM_YELLOW(32),
    @ApiModelProperty(name = "WS_RAINSTORM_ORANGE", value = "33")
    WS_RAINSTORM_ORANGE(33),
    @ApiModelProperty(name = "WS_RAINSTORM_RED", value = "34")
    WS_RAINSTORM_RED(34),
    @ApiModelProperty(name = "WS_HIGHTEMP_YELLOW", value = "42")
    WS_HIGHTEMP_YELLOW(42),
    @ApiModelProperty(name = "WS_HIGHTEMP_ORANGE", value = "43")
    WS_HIGHTEMP_ORANGE(43),
    @ApiModelProperty(name = "WS_HIGHTEMP_RED", value = "44")
    WS_HIGHTEMP_RED(44),
    @ApiModelProperty(name = "WS_HAIL_ORANGE", value = "53")
    WS_HAIL_ORANGE(53),
    @ApiModelProperty(name = "WS_HAIL_RED", value = "54")
    WS_HAIL_RED(54);

    private final Integer value;

    WarningSignalEnum(Integer value) {
        this.value = value;
    }
}
