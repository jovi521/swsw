package com.cdyw.swsw.common.domain.entity.parse;

import lombok.Data;

/**
 * @author cdyw
 * @description
 */
@Data
public class ModeDataTSRainParse {


    private Integer id;
    private Float tsValue;
    private String initTime;
    private Long timestamp;
    private Integer fcstHour;
    private Float threshold;
}
