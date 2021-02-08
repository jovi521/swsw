package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author jovi
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {

    // 正常
    NORMAL(1),

    // 超时
    TIMEOUT(0),

    // 缺失
    MISSING(-1);

    private final Integer value;
}
