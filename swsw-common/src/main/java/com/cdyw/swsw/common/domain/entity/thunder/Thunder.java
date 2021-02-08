package com.cdyw.swsw.common.domain.entity.thunder;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 雷电
 *
 * @author jovi
 */
@Data
public class Thunder implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 资料时间
     */
    private LocalDateTime dateTime;

    /**
     * 经度
     */
    private Float lon;

    /**
     * 纬度
     */
    private Float lat;

    /**
     * 资料观测毫秒
     */
    private String mSecond;

    /**
     * 层次序号
     */
    private Integer layerNum;

    /**
     * 回击最大陡度
     */
    private Float mars3;

    /**
     * 定位误差
     */
    private Float poisErr;

    /**
     * 定位方式
     */
    private Integer poisType;

    /**
     * 电流强度
     */
    private Float litCurrent;

    /**
     * 雷电地理位置信息省
     */
    private String litProv;

    /**
     * 雷电地理位置信息市
     */
    private String litCity;

    /**
     * 雷电地理位置信息县
     */
    private String litCnty;
}