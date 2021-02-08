package com.cdyw.swsw.common.domain.entity.file;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 所有存储文件的大类所在的数据库表对应的实体类
 *
 * @author jovi
 */
@ApiModel(value = "FileEntity", description = "所有存储文件的大类所在的数据库表对应的实体类")
@Data
public class FileEntity implements Serializable {

    Integer id;

    /**
     * 文件名
     */
    String name;

    /**
     * 文件路径
     */
    String posFile;

    /**
     * 文件大小
     */
    Integer fileSize;

    /**
     * 文件类型
     */
    Integer type;

    /**
     * 文件站号
     */
    String radarcd;

    /**
     * 文件时间
     */
    Long time;

    /**
     * 层数
     */
    Integer layer;

    /**
     * 标识码
     */
    String mcode;

    /**
     * 创建时间
     */
    Long createTime;

    /**
     * 订正状态：0未订正，1面订正，2点订正，3锁定
     */
    Integer modifyType;
}
