package com.cdyw.swsw.common.domain.entity.parse;

import lombok.Data;

import java.io.Serializable;

/**
 * 解析实体类
 *
 * @author cdyw
 */
@Data
public class Parse implements Serializable {

    private Integer id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件路径
     */
    private String pos_file;

    /**
     * 文件大小
     */
    private Integer file_size;

    /**
     * 文件图片
     */
    private String pos_picture;

    /**
     * 文件类型
     */
    private Integer type;

    /**
     * 文件站号
     */
    private String radarcd;

    /**
     * 文件时间
     */
    private Integer time;

    private Integer layer;

    private Integer scan_mode;

    private String mcode;

    private Integer rain_type;

}
