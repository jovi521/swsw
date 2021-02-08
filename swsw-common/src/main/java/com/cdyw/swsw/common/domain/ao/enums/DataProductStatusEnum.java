package com.cdyw.swsw.common.domain.ao.enums;

import lombok.Getter;

/**
 * 产品（文档）12种类型枚举类
 *
 * @author jovi
 */
@Getter
public enum DataProductStatusEnum {
    /**
     * 未发布
     */
    STATUS_SWSW_NO_PUBLISH(0),
    /**
     * 已发布
     */
    STATUS_SWSW_PUBLISHED(1),
    /**
     * 审批中
     */
    STATUS_SWSW_APPROVING(2),
    /**
     * 已驳回
     */
    STATUS_SWSW_REJECT(3);

    private final Integer status;

    DataProductStatusEnum(Integer status) {
        this.status = status;
    }
}
