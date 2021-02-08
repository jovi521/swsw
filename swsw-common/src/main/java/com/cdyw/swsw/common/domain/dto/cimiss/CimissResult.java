package com.cdyw.swsw.common.domain.dto.cimiss;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Cimiss 获取数据结果集
 *
 * @author jovi
 */
@ToString
@Data
public class CimissResult implements Serializable {

    private String requestTime;

    private String returnCode;

    private String responseTime;

    private Double takeTime;

    private String fieldNames;

    private String returnMessage;

    private Integer colCount;

    private String requestParams;

    private Integer rowCount;

    private List<Map<String, Object>> DS;
}
