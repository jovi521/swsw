package com.cdyw.swsw.common.domain.ao.enums;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

/**
 * @author jovi
 */
@ApiModel(value = "DataProductColumnKeyEnum", description = "存放dataProduct中列对应的key")
@Getter
@Deprecated
public enum DataProductColumnKeyEnum {
    /**
     * PARAM1
     */
    PARAM1("param1"),
    /**
     * PARAM2
     */
    PARAM2("param2"),
    /**
     * PARAM3
     */
    PARAM3("param3"),
    /**
     * PARAM8
     */
    PARAM8("param8"),
    /**
     * PARAM_5
     */
    PARAM5("param5"),
    /**
     * PARAM6
     */
    PARAM6("param6"),
    /**
     * PARAM7
     */
    PARAM7("param7"),
    /**
     * PARAM10
     */
    PARAM10("param10"),
    /**
     * PARAM13
     */
    PARAM13("param13"),
    /**
     * PARAM18
     */
    PARAM18("param18"),
    /**
     * PARAM19
     */
    PARAM19("param19"),
    /**
     * PARAM20
     */
    PARAM20("param20"),
    /**
     * PARAM21
     */
    PARAM21("param21"),
    /**
     * PARAM22
     */
    PARAM22("param22"),
    /**
     * PARAM23
     */
    PARAM23("param23"),
    /**
     * PARAM24
     */
    PARAM24("param24");

    private final String param;

    DataProductColumnKeyEnum(String param) {
        this.param = param;
    }
}
