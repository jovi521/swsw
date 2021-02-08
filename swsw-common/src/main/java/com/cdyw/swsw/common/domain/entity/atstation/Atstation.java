package com.cdyw.swsw.common.domain.entity.atstation;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 自动站原始数据base的实体类
 *
 * @author jovi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Atstation extends FileEntity {

    /**
     * 资料时间
     */
    private LocalDateTime dateTime;

    /**
     * 站名
     */
    private String stationName;

    /**
     * 区站号(字符)
     */
    private String stationIdC;

    /**
     * 区站号(数字)
     */
    private Integer stationIdD;

    /**
     * 省份
     */
    private String province;

    /**
     * 地市
     */
    private String city;

    /**
     * 区县
     */
    private String cnty;

    /**
     * 行政区代码
     */
    private Integer adminCodeChn;

    /**
     * 经度
     */
    private Float lon;

    /**
     * 纬度
     */
    private Float lat;

    /**
     * 测站高度
     */
    private Float alti;

    /**
     * 测站级别
     */
    private Integer stationLevl;

    /**
     * 地面温度
     */
    private Float gst;

    /**
     * 降水量(/毫米)
     */
    private Float pre;

    /**
     * 过去1小时降水量(/毫米)
     */
    private Float pre1h;

    /**
     * 过去10分钟降水量(/毫米)
     */
    private Float pre10min;

    /**
     * 气压(百帕)
     */
    private Float prs;

    /**
     * 相对湿度(%)
     */
    private Float rhu;

    /**
     * 气温(摄氏度)
     */
    private Float tem;

    /**
     * 最大风速(/米/秒)
     */
    private Float winSmax;

    /**
     * 最大风速的风向(/度)
     */
    private Float winDsmax;

    /**
     * 水平能见度(人工)/米
     */
    private Float vis;

    private Long timestamp;
}
