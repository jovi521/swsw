package com.cdyw.swsw.common.domain.entity.log;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数据监控日志类
 *
 * @author jovi
 */
@Data
public class DataMonitorLog implements Serializable {
    private Integer id;

    private LocalDateTime createTime;

    private LocalDateTime dateTime;

    private Integer type;

    private Integer molecular;

    private Integer denominator;

    private Integer status;

    private String msg;
}