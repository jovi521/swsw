package com.cdyw.swsw.common.domain.ao.dic;

import lombok.Data;

import java.io.Serializable;

@Data
public class DicLevel implements Serializable {
    private Integer id;

    private String code;

    private String nameEn;

    private String nameCn;

    private String parentCode;

    private Integer level;

}