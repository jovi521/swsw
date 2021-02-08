package com.cdyw.swsw.common.domain.entity.grid;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 雷达外推
 *
 * @author jovi
 */
@Data
public class D3GridFusion implements Serializable {

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
     * 区站号(字符)
     */
    private String stationIdC;

    /**
     * 雷达型号
     */
    private String radaModel;

    /**
     * 产品类型标识符
     */
    private String prodTypeRada;

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

    /**
     * 仰角
     */
    private Float elev;

}
