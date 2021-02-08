package com.cdyw.swsw.common.domain.ao.dic;

import lombok.Data;

import java.io.Serializable;

@Data
public class DicTransparency implements Serializable {
    private Integer id;

    private Integer transValue;

    private String productCode;

    private String stationCode;

    private String parentCode;

}