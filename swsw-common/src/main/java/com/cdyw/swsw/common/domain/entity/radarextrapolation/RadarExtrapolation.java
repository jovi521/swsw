package com.cdyw.swsw.common.domain.entity.radarextrapolation;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import lombok.Data;

/**
 * 雷达外推实体类
 *
 * @author cdyw
 */
@Data
public class RadarExtrapolation extends FileEntity {

    private Long forecastTime;
    private Double lonMin;
    private Double lonMax;
    private Double latMin;
    private Double latMax;
}
