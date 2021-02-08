package com.cdyw.swsw.common.domain.entity.lonlat;

import lombok.Data;

/**
 * 专门用来存储体育场馆经纬度
 *
 * @author jovi
 */
@Data
public class LonLatStadium {

    private Integer id;

    private Float lon;

    private Float lat;

    private String stadium;
}
