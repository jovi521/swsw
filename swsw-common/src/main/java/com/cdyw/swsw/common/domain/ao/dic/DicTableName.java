package com.cdyw.swsw.common.domain.ao.dic;

import lombok.Data;

import java.io.Serializable;

@Data
public class DicTableName implements Serializable {
    private Integer id;

    private String name;

    private Integer level;

    private String parentCode;

}