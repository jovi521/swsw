package com.cdyw.swsw.data.domain.service.radar;

import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.common.component.CommonFileName;
import com.cdyw.swsw.data.common.component.CommonRadarParseName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author jovi
 */
@Service
public class RadarPhasedArrayService {

    private final TypeEnum TYPE = TypeEnum.TYPE_RADAR_PHASED_ARRAY;

    private final CommonFileName commonFileName;

    private final CommonRadarParseName commonRadarParseName;

    @Autowired
    public RadarPhasedArrayService(CommonFileName commonFileName, CommonRadarParseName commonRadarParseName) {
        this.commonRadarParseName = commonRadarParseName;
        this.commonFileName = commonFileName;
    }

    public int insertRadarPhasedArrayBase(File fileSource) {
        return commonFileName.insertFileEntityBase(TYPE, fileSource);
    }

    public int insertRadarPhasedArrayParse(File fileSource) {
        return commonRadarParseName.insertRadarParse(TYPE, fileSource);
    }
}
