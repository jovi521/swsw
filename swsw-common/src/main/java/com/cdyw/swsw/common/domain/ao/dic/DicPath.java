package com.cdyw.swsw.common.domain.ao.dic;

import lombok.Data;

import java.io.Serializable;

@Data
public class DicPath implements Serializable {
    private Integer id;

    private String path;

    private Integer level;

    private String parentCode;

}