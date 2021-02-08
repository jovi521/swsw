package com.cdyw.swsw.common.domain.entity.fusion12h;

import lombok.Data;

import java.io.Serializable;

/**
 * @author jovi
 */
@Data
public class Fusion12hBaseTime implements Serializable {

    private Integer id;

    private Long baseTime;

    private Integer modifyType;
}
