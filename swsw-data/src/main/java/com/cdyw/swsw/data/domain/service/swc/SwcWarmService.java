package com.cdyw.swsw.data.domain.service.swc;

import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.common.component.CommonFileName;
import com.cdyw.swsw.data.domain.dao.common.CommonDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * @author jovi
 */
@Slf4j
@Service
public class SwcWarmService {

    private final TypeEnum TYPE = TypeEnum.TYPE_SWC_WARM;

    private final CommonFileName commonFileName;

    @Autowired
    public SwcWarmService(CommonDataMapper commonDataMapper, CommonFileName commonFileName) {
        this.commonFileName = commonFileName;
    }

    public int insertSwcWarmBase(File fileSource) {
        return commonFileName.insertFileEntityBase(TYPE, fileSource);
    }
}
