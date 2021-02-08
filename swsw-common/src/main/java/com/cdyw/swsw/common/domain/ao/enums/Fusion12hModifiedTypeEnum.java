package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 融合12小时数据-修改状态枚举类
 *
 * @author jovi
 */
@AllArgsConstructor
@Getter
public enum Fusion12hModifiedTypeEnum {
    /**
     * 未订正
     */
    TYPE_UN_MODIFIED(0),
    /**
     * 面订正
     */
    TYPE_POLYGON_MODIFIED(1),
    /**
     * 点订正
     */
    TYPE_POINT_MODIFIED(2),
    /**
     * 锁定订正
     */
    TYPE_LOCKED_MODIFIED(3);

    private final Integer type;
}
