package com.cdyw.swsw.data.domain.service.ecmwf;

import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.common.component.CommonFileName;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author jovi
 */
@Slf4j
@Service
public class EcmwfHrService {

    private final TypeEnum TYPE = TypeEnum.TYPE_ECMWF_HR;

    private final CommonFileName commonFileName;

    @Autowired
    public EcmwfHrService(CommonFileName commonFileName) {
        this.commonFileName = commonFileName;
    }

    public int insertEcmwfHrBase(File fileSource) {
        return commonFileName.insertFileEntityBase(TYPE, fileSource);
    }
}
