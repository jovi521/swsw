package com.cdyw.swsw.common.domain.entity.menu;

import lombok.Data;

@Data
public class FrontMenu {
    private String name;
    private Integer id;
    private String produceType;
    private Boolean isOpen;
    private String comname;
    private String measure;
    private Integer type;
    private Integer parentCode;
}
