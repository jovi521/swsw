package com.cdyw.swsw.common.domain.entity.fusion12h;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@Data
public class Fusion12hNewestFile implements Serializable {

    private Integer id;

    private Long baseTime;

    private Integer forecastCount;

    private Long forecastTime;

    private String name;

    private Integer type;

    private Integer layer;
}
