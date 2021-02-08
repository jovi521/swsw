package com.cdyw.swsw.data.common.listener.radar;

import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.domain.service.radar.RadarWindProfileService;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 天气雷达原始文件监听器
 *
 * @author jovi
 */
@Component
public class RadarWindProfileListener implements FileAlterationListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RadarWindProfileService radarWindProfileService;

    private final CommonPath commonPath;

    /**
     * 因为Filter和Listener加载顺序优先于spring容器初始化实例，所以使用@Autowired肯定为null
     */
    @Autowired
    public RadarWindProfileListener(RadarWindProfileService radarWindProfileService, CommonPath commonPath) {
        this.radarWindProfileService = radarWindProfileService;
        this.commonPath = commonPath;
    }

    @Override
    public void onFileCreate(File fileSource) {
        logger.info(TypeEnum.TYPE_RADAR_WIND_PROFILE.getName() + " onFileCreate: " + fileSource);
        String tmpFile = "tmp";
        int size = 0;
        // 进入方法之前必须判断文件是否有效
        if (fileSource.length() != 0 && !fileSource.getName().contains(tmpFile)) {
            if (fileSource.getPath().contains(commonPath.getBaseMonitorPath().replace("/", ""))) {
                size = radarWindProfileService.insertRadarWindProfileBase(fileSource);
            } else if (fileSource.getPath().contains(commonPath.getParseMonitorPath().replace("/", ""))) {
                size = radarWindProfileService.insertRadarWindProfileParse(fileSource);
            }
            logger.info(TypeEnum.TYPE_RADAR_WIND_PROFILE.getName() + " onFileCreated: " + size + " 条数据");
        } else {
            logger.info(TypeEnum.TYPE_RADAR_WIND_PROFILE.getName() + " 新增无效文件：" + fileSource.getName() + "。请检查数据源是否正确");
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
