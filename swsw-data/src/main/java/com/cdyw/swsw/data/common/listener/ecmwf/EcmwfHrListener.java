package com.cdyw.swsw.data.common.listener.ecmwf;

import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.domain.service.ecmwf.EcmwfHrService;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * EcmwfHr 原始文件监听器
 *
 * @author jovi
 */
@Component
public class EcmwfHrListener implements FileAlterationListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TypeEnum TYPE = TypeEnum.TYPE_ECMWF_HR;

    private final EcmwfHrService ecmwfHrService;

    @Autowired
    public EcmwfHrListener(EcmwfHrService ecmwfHrService) {
        this.ecmwfHrService = ecmwfHrService;
    }

    @Override
    public void onFileCreate(File fileSource) {
        logger.info(TYPE.getName() + " onFileCreate: " + fileSource);
        String tmpFile = "tmp";
        int size = 0;
        // 进入方法之前必须判断文件是否有效
        if (fileSource.length() != 0 && !fileSource.getName().contains(tmpFile)) {
            size = ecmwfHrService.insertEcmwfHrBase(fileSource);
            logger.info(TYPE.getName() + " onFileCreated: " + size + " 条数据");
        } else {
            logger.info(TYPE.getName() + " 新增无效文件：" + fileSource.getName() + "。请检查数据源是否正确");
        }
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
    }

    @Override
    public void onDirectoryCreate(File directory) {
    }

    @Override
    public void onDirectoryChange(File directory) {
    }

    @Override
    public void onDirectoryDelete(File directory) {
    }

    @Override
    public void onFileChange(File file) {
    }

    @Override
    public void onFileDelete(File file) {
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
    }
}
