package com.cdyw.swsw.common.domain.dto.ds;

import lombok.Data;

import java.io.Serializable;

/**
 * Cimiss 获取数据结果集
 *
 * @author jovi
 */
@Data
public class CimissDsResult implements Serializable {

    String fileName;

    String format;

    Integer fileSize;

    String fileUrl;

    String dateTime;

    String dSourceId;
}
