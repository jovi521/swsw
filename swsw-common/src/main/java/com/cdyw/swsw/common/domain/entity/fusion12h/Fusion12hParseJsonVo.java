package com.cdyw.swsw.common.domain.entity.fusion12h;

import lombok.Data;

import java.io.Serializable;

/**
 * Fusion12h 解析后的 json 文件实体类
 *
 * @author jovi
 */
@Data
public class Fusion12hParseJsonVo implements Serializable {

    private Integer modifyType;

    private Integer forecastCount;

    private String filename;

    private Double data;

    private Integer lonCount;

    private Double lonMax;

    private Double lonMin;

    private Integer latCount;

    private Double latMax;

    private Double latMin;

    private String time;

    private String productType;

    private Boolean isdisable;
}
