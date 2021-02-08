package com.cdyw.swsw.common.domain.ao.txt;


import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卫星FY4A解析后的txt文件内容
 *
 * @author jovi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SatelliteFY4AParseTxt extends FileEntity {
    private String lonMin;
    private String lonMax;
    private String latMin;
    private String latMax;
    private String startTime;
    private String endTime;
    private String fileDir;
    private String fileName;
}
