package com.cdyw.swsw.common.domain.entity.fusion12h;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@Data
public class Fusion12hModified implements Serializable {

    private Integer id;

    private Long baseTime;

    private Integer type;

    private Integer forecastCount;

    private Integer modifyType;

    private Integer layer;
}
