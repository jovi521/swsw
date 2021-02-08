package com.cdyw.swsw.common.domain.ao.txt;


import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自动站解析后的txt文件内容
 *
 * @author jovi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AtstationParseTxt extends FileEntity {
    private String lonMin;
    private String lonMax;
    private String latMin;
    private String latMax;
    private String startTime;
    private String fileDir;
    private String fileName;
}
