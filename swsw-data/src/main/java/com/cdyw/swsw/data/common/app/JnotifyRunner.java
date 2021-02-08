package com.cdyw.swsw.data.common.app;

import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.data.common.notify.cldas1km.Cldas1kmNotifyClass;
import com.cdyw.swsw.data.common.notify.ecmwf.EcmwfHrNotifyClass;
import com.cdyw.swsw.data.common.notify.fusion12h.Fusion12HNotifyClass;
import com.cdyw.swsw.data.common.notify.grapes3km.Grapes3kmNotifyClass;
import com.cdyw.swsw.data.common.notify.radar.*;
import com.cdyw.swsw.data.common.notify.radarextrapolation.RadarExtrapolationNotifyClass;
import com.cdyw.swsw.data.common.notify.satellite.SatelliteNotifyClass;
import com.cdyw.swsw.data.common.notify.swc.SwcWarmNotifyClass;
import com.cdyw.swsw.data.common.notify.swc.SwcWarrNotifyClass;
import com.cdyw.swsw.data.common.notify.windfieldinversion.WindfieldInversionNotifyClass;
import com.cdyw.swsw.data.domain.service.cldas_1km.Cldas1kmService;
import com.cdyw.swsw.data.domain.service.ecmwf.EcmwfHrService;
import com.cdyw.swsw.data.domain.service.fusion_12h.Fusion12HService;
import com.cdyw.swsw.data.domain.service.grapes_3km.Grapes3kmService;
import com.cdyw.swsw.data.domain.service.radar.RadarPhasedArrayService;
import com.cdyw.swsw.data.domain.service.radar.RadarWeatherService;
import com.cdyw.swsw.data.domain.service.radar.RadarWindProfileService;
import com.cdyw.swsw.data.domain.service.radarextrapolation.RadarExtraplationService;
import com.cdyw.swsw.data.domain.service.satellite.SatelliteService;
import com.cdyw.swsw.data.domain.service.swc.SwcWarmService;
import com.cdyw.swsw.data.domain.service.swc.SwcWarrService;
import com.cdyw.swsw.data.domain.service.windfieldinversion.WindfieldInversionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author liudong
 */
@Component
public class JnotifyRunner implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Fusion12HService fusion12HService;
    private final Cldas1kmService cldas1kmService;
    private final WindfieldInversionService windfieldInversionService;
    private final SwcWarmService swcWarmService;
    private final SwcWarrService swcWarrService;
    private final EcmwfHrService ecmwfHrService;
    private final Grapes3kmService grapes3kmService;
    private final RadarPhasedArrayService radarPhasedArrayService;
    private final RadarWeatherService radarWeatherService;
    private final RadarWindProfileService radarWindProfileService;
    private final RadarExtraplationService radarExtraplationService;
    private final SatelliteService satelliteService;
    private final CommonPath commonPath;

    @Autowired
    public JnotifyRunner(Fusion12HService fusion12HService, Cldas1kmService cldas1kmService,
                         WindfieldInversionService windfieldInversionService,
                         SwcWarmService swcWarmService,
                         SwcWarrService swcWarrService,
                         EcmwfHrService ecmwfHrService,
                         Grapes3kmService grapes3kmService,
                         RadarPhasedArrayService radarPhasedArrayService,
                         RadarWeatherService radarWeatherService,
                         RadarWindProfileService radarWindProfileService,
                         RadarExtraplationService radarExtraplationService,
                         SatelliteService satelliteService,
                         CommonPath commonPath) {
        this.fusion12HService = fusion12HService;
        this.cldas1kmService = cldas1kmService;
        this.windfieldInversionService = windfieldInversionService;
        this.swcWarmService = swcWarmService;
        this.swcWarrService = swcWarrService;
        this.ecmwfHrService = ecmwfHrService;
        this.grapes3kmService = grapes3kmService;
        this.radarPhasedArrayService = radarPhasedArrayService;
        this.radarWeatherService = radarWeatherService;
        this.radarWindProfileService = radarWindProfileService;
        this.radarExtraplationService = radarExtraplationService;
        this.satelliteService = satelliteService;
        this.commonPath = commonPath;
    }


    @Override
    public void run(ApplicationArguments args) {
        String sourceGrapes3kmFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getGrapes3kmMonitorPath();
        String sourceRadarPhasedArrayBaseFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getRadarPhasedArrayMonitorPath();
        String sourceRadarPhasedArrayParseFilepath = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getRadarPhasedArrayMonitorPath();
        String sourceRadarWeatherBaseFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getRadarWeatherMonitorPath();
        String sourceRadarWeatherParseFilepath = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getRadarWeatherMonitorPath();
        String sourceRadarWindProfileBaseFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getRadarWindProfileMonitorPath();
        String sourceRadarWindProfileParseFilepath = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getRadarWindProfileMonitorPath();
        String sourceEcmwfHrFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getEcmwfHrMonitorPath();
        String sourceSwcWarrFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getSwcWarrMonitorPath();
        String sourceSwcWarmFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getSwcWarmMonitorPath();
        String sourceFusion12hFilepath = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getFusion12hMonitorPath();
        String sourceCldas1kmFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getCldas1kmMonitorPath();
        String sourceWindfieldInversionFilepath = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getWindfieldInversionMonitorPath();
        String sourceWeatherRadarExtrapolationFilepath = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getWeatherRadarExtrapolationMonitor();
        String sourceSatelliteFilepath = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getSatelliteMonitor();

        Grapes3kmNotifyClass grapes3kmNotifyClass = new Grapes3kmNotifyClass(sourceGrapes3kmFilepath, grapes3kmService);
        RadarPhasedArrayBaseNotifyClass radarPhasedArrayBaseNotifyClass = new RadarPhasedArrayBaseNotifyClass(sourceRadarPhasedArrayBaseFilepath, radarPhasedArrayService);
        RadarPhasedArrayParseNotifyClass radarPhasedArrayParseNotifyClass = new RadarPhasedArrayParseNotifyClass(sourceRadarPhasedArrayParseFilepath, radarPhasedArrayService);
        RadarWeatherBaseNotifyClass radarWeatherBaseNotifyClass = new RadarWeatherBaseNotifyClass(sourceRadarWeatherBaseFilepath, radarWeatherService);
        RadarWeatherParseNotifyClass radarWeatherParseNotifyClass = new RadarWeatherParseNotifyClass(sourceRadarWeatherParseFilepath, radarWeatherService);
        RadarWindProfileBaseNotifyClass radarWindProfileBaseNotifyClass = new RadarWindProfileBaseNotifyClass(sourceRadarWindProfileBaseFilepath, radarWindProfileService);
        RadarWindProfileParseNotifyClass radarWindProfileParseNotifyClass = new RadarWindProfileParseNotifyClass(sourceRadarWindProfileParseFilepath, radarWindProfileService);
        EcmwfHrNotifyClass ecmwfHrNotifyClass = new EcmwfHrNotifyClass(sourceEcmwfHrFilepath, ecmwfHrService);
        SwcWarrNotifyClass swcWarrNotifyClass = new SwcWarrNotifyClass(sourceSwcWarrFilepath, swcWarrService);
        SwcWarmNotifyClass swcWarmNotifyClass = new SwcWarmNotifyClass(sourceSwcWarmFilepath, swcWarmService);
        Fusion12HNotifyClass fusion12HNotifyClass = new Fusion12HNotifyClass(sourceFusion12hFilepath, fusion12HService);
        Cldas1kmNotifyClass cldas1kmNotifyClass = new Cldas1kmNotifyClass(sourceCldas1kmFilepath, cldas1kmService);
        WindfieldInversionNotifyClass windfieldInversionNotifyClass = new WindfieldInversionNotifyClass(sourceWindfieldInversionFilepath, windfieldInversionService);
        RadarExtrapolationNotifyClass radarExtrapolationNotifyClass = new RadarExtrapolationNotifyClass(sourceWeatherRadarExtrapolationFilepath, radarExtraplationService);
        SatelliteNotifyClass satelliteNotifyClass = new SatelliteNotifyClass(sourceSatelliteFilepath, satelliteService);
        // 开启文件监听线程
        try {
            grapes3kmNotifyClass.start();
            radarPhasedArrayBaseNotifyClass.start();
            radarPhasedArrayParseNotifyClass.start();
            radarWeatherBaseNotifyClass.start();
            radarWeatherParseNotifyClass.start();
            radarWindProfileBaseNotifyClass.start();
            radarWindProfileParseNotifyClass.start();
            ecmwfHrNotifyClass.start();
            swcWarrNotifyClass.start();
            swcWarmNotifyClass.start();
            fusion12HNotifyClass.start();
            cldas1kmNotifyClass.start();
            windfieldInversionNotifyClass.start();
            radarExtrapolationNotifyClass.start();
            satelliteNotifyClass.start();
        } catch (Exception e) {
            logger.error("jnotify监听出现异常。。");
        }

    }
}
