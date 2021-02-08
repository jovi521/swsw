package com.cdyw.swsw.common.domain.entity.api;

import lombok.Data;

@Data
public class SysApi {

    private Integer apiId;
    private String apiName;
    private String apiUrl;
    private String apiMethod;
    private String pid;
    private String description;
}
