package com.cdyw.swsw.common.domain.ao.dic;

import lombok.Data;

/**
 * @author cdyw
 * @description
 */
@Data
public class DicRoad {

    private Integer id;
    private Integer startStation;
    private Integer endStation;
    private String lonlatStop;
    private Integer level;
    private String highlightColor;

}
