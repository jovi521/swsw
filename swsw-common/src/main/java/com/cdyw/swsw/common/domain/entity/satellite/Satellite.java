package com.cdyw.swsw.common.domain.entity.satellite;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 卫星
 *
 * @author jovi
 */
@Data
public class Satellite implements Serializable {

    private Integer id;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件格式
     */
    private String format;

    /**
     * 文件大小
     */
    private Float fileSize;

    /**
     * 存储路径
     */
    private String fileUrl;

    /**
     * 录入时间
     */
    private LocalDateTime createTime;

    /**
     * 资料时间
     */
    private LocalDateTime dateTime;

    /**
     * 文件生成时间
     */
    private String fileTime;

    /**
     * 数据级别
     */
    private String dataLevl;

    /**
     * 编报(加工)中心
     */
    private String bulCenter;

    /**
     * 卫星名称
     */
    private String sateName;

    /**
     * 卫星仪器名称
     */
    private String sateSensor;

    /**
     * 数据区域范围
     */
    private String dataArea;
    /**
     * 产品标识
     */
    private String prodId;

    /**
     * 卫星仪器通道名称
     */
    private String sateSensorChanl;

    /**
     * 投影方式
     */
    private String projType;

    /**
     * 空间分辨率
     */
    private String spacDpi;

    /**
     * 存储状态
     */
    private Integer dFileSaveHierarchy;

    /**
     * 数据来源
     */
    private String dSourceId;

    /**
     * 文件标识
     */
    private String dFileId;

}
