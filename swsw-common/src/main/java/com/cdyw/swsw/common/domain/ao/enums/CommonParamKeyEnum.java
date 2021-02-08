package com.cdyw.swsw.common.domain.ao.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Cimiss获取URL公共参数key枚举类
 *
 * @author jovi
 */
@Getter
@AllArgsConstructor
public enum CommonParamKeyEnum {
    /**
     * 用户名
     */
    PARAM_KEY_USER_ID("userId"),
    /**
     * 密码
     */
    PARAM_KEY_PWD("pwd"),
    /**
     * 返回数据格式（默认json）
     */
    PARAM_KEY_DATA_FORMAT("dataFormat"),
    /**
     * 主接口名（对应一级大类）
     */
    PARAM_KEY_INTERFACE_ID("interfaceId"),
    /**
     * 资料代码（对应三级产品）
     */
    PARAM_KEY_DATA_CODE("dataCode"),
    /**
     * 返回元素
     */
    PARAM_KEY_ELEMENTS("elements"),
    /**
     * 指定元素（目前仅有cldas_1km独有）
     */
    PARAM_KEY_FCST_ELES("fcstEles"),
    /**
     * 数据数量
     */
    PARAM_KEY_LIMIT_CNT("limitCnt"),
    /**
     * 站点编号（对应二级站点）
     */
    PARAM_KEY_STAT_IDS("staIds"),
    /**
     * 时间范围：[yyyyMMddHHmmss,yyyyMMddHHmmss]
     */
    PARAM_KEY_TIME_RANGE("timeRange"),
    /**
     * 行政范围：成都市
     */
    PARAM_KEY_ADMIN_CODES("adminCodes");

    private final String paramKey;
}
