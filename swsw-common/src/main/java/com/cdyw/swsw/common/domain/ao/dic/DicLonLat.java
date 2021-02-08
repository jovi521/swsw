package com.cdyw.swsw.common.domain.ao.dic;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@Data
public class DicLonLat implements Serializable {
    private Integer id;

    private Double lon;

    private Double lat;

    private String code;

    private String parentCode;

    private String location;

    private Integer type;

}