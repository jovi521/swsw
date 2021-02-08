package com.cdyw.swsw.common.domain.entity.fusion12h;

import lombok.Data;

import java.util.List;

/**
 * fusion12h接收数据包装类
 */
@Data
public class Fusion12hVo {

    private String filename;
    private String modifyValue;
    private List<String> lonlat;
    private String basetime;
    private String forecastCount;
    private String forecastTime;
    private String layer;
    private String productType;

}