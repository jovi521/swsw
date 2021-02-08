package com.cdyw.swsw.common.domain.ao.txt;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 短临预报解析后的实体类
 *
 * @author cdyw
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShortForecastParseTxt extends FileEntity {
    private String lonMin;
    private String lonMax;
    private String latMin;
    private String latMax;
    private String forecastcount;
    private String fileDir;
    private String fileName;
}
