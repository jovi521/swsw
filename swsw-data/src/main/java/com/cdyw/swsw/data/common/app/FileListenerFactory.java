package com.cdyw.swsw.data.common.app;


import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.common.listener.cldas1km.Cldas1kmFileListener;
import com.cdyw.swsw.data.common.listener.ecmwf.EcmwfHrListener;
import com.cdyw.swsw.data.common.listener.fusion12h.Fusion12HFileListener;
import com.cdyw.swsw.data.common.listener.grapes3km.Grapes3kmFileListener;
import com.cdyw.swsw.data.common.listener.radar.RadarPhasedArrayListener;
import com.cdyw.swsw.data.common.listener.radar.RadarWeatherListener;
import com.cdyw.swsw.data.common.listener.radar.RadarWindProfileListener;
import com.cdyw.swsw.data.common.listener.swc.SwcWarmListener;
import com.cdyw.swsw.data.common.listener.swc.SwcWarrListener;
import com.cdyw.swsw.data.common.listener.windfieldinversion.WindfieldInversionFileListener;
import com.cdyw.swsw.data.domain.service.cldas_1km.Cldas1kmService;
import com.cdyw.swsw.data.domain.service.ecmwf.EcmwfHrService;
import com.cdyw.swsw.data.domain.service.fusion_12h.Fusion12HService;
import com.cdyw.swsw.data.domain.service.grapes_3km.Grapes3kmService;
import com.cdyw.swsw.data.domain.service.radar.RadarPhasedArrayService;
import com.cdyw.swsw.data.domain.service.radar.RadarWeatherService;
import com.cdyw.swsw.data.domain.service.radar.RadarWindProfileService;
import com.cdyw.swsw.data.domain.service.swc.SwcWarmService;
import com.cdyw.swsw.data.domain.service.swc.SwcWarrService;
import com.cdyw.swsw.data.domain.service.windfieldinversion.WindfieldInversionService;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件监控工厂类
 *
 * @author cdyw
 * @modified jovi
 */

// @Component
public class FileListenerFactory {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 设置轮询间隔
     */
    private final long interval = 10000L;

    private final Grapes3kmService grapes3kmService;

    private final Fusion12HService fusion12HService;

    private final RadarPhasedArrayService radarPhasedArrayService;

    private final RadarWeatherService radarWeatherService;

    private final RadarWindProfileService radarWindProfileService;

    private final EcmwfHrService ecmwfHrService;

    private final SwcWarrService swcWarrService;

    private final SwcWarmService swcWarmService;

    private final Cldas1kmService cldas1kmService;

    private final WindfieldInversionService windfieldInversionService;

    private final CommonPath commonPath;

    @Autowired
    public FileListenerFactory(Grapes3kmService grapes3kmService, Fusion12HService fusion12HService, RadarPhasedArrayService radarPhasedArrayService,
                               RadarWeatherService radarWeatherService, RadarWindProfileService radarWindProfileService, EcmwfHrService ecmwfHrService,
                               SwcWarrService swcWarrService, SwcWarmService swcWarmService, Cldas1kmService cldas1kmService, WindfieldInversionService windfieldInversionService,
                               CommonPath commonPath) {
        this.grapes3kmService = grapes3kmService;
        this.fusion12HService = fusion12HService;
        this.radarPhasedArrayService = radarPhasedArrayService;
        this.radarWeatherService = radarWeatherService;
        this.radarWindProfileService = radarWindProfileService;
        this.ecmwfHrService = ecmwfHrService;
        this.swcWarrService = swcWarrService;
        this.swcWarmService = swcWarmService;
        this.cldas1kmService = cldas1kmService;
        this.windfieldInversionService = windfieldInversionService;
        this.commonPath = commonPath;
    }

    /**
     * GRAPES3KM数据监测
     *
     * @return
     */
    public FileAlterationMonitor getGrapes3kmMonitor() {
        String pathname = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getGrapes3kmMonitorPath();
        // 装配过滤器
        FileAlterationObserver observer = new FileAlterationObserver(new File(pathname), getFileFilter());
        // 向监听者添加监听器，并注入业务服务
        observer.addListener(new Grapes3kmFileListener(grapes3kmService));
        logger.info(TypeEnum.TYPE_GRAPES_3KM.getName() + " 开始监控路径为： " + observer.getDirectory());
        // 返回监听者
        return new FileAlterationMonitor(interval, observer);
    }

    /**
     * 获取相控阵雷达监控数据
     */
    public FileAlterationMonitor getRadarPhaArrMonitor() {
        String pathnameBase = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getRadarPhasedArrayMonitorPath();
        String pathnameParse = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getRadarPhasedArrayMonitorPath();
        FileAlterationObserver observerBase = new FileAlterationObserver(new File(pathnameBase), getFileFilter());
        FileAlterationObserver observerParse = new FileAlterationObserver(new File(pathnameParse), getFileFilter());
        observerBase.addListener(new RadarPhasedArrayListener(radarPhasedArrayService, commonPath));
        observerParse.addListener(new RadarPhasedArrayListener(radarPhasedArrayService, commonPath));
        logger.info(TypeEnum.TYPE_RADAR_PHASED_ARRAY.getName() + " 开始监控路径为： " + observerBase.getDirectory());
        logger.info(TypeEnum.TYPE_RADAR_PHASED_ARRAY.getName() + " 开始监控路径为： " + observerParse.getDirectory());
        return new FileAlterationMonitor(interval, observerBase, observerParse);
    }

    /**
     * 获取天气雷达监控数据
     */
    public FileAlterationMonitor getRadarWeaMonitor() {
        String pathnameBase = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getRadarWeatherMonitorPath();
        String pathnameParse = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getRadarWeatherMonitorPath();
        FileAlterationObserver observerBase = new FileAlterationObserver(new File(pathnameBase), getFileFilter());
        FileAlterationObserver observerParse = new FileAlterationObserver(new File(pathnameParse), getFileFilter());
        observerBase.addListener(new RadarWeatherListener(radarWeatherService, commonPath));
        observerParse.addListener(new RadarWeatherListener(radarWeatherService, commonPath));
        logger.info(TypeEnum.TYPE_RADAR_WEATHER.getName() + " 开始监控路径为： " + observerBase.getDirectory());
        logger.info(TypeEnum.TYPE_RADAR_WEATHER.getName() + " 开始监控路径为： " + observerParse.getDirectory());
        return new FileAlterationMonitor(interval, observerBase, observerParse);
    }

    /**
     * 获取风廓线雷达监控数据
     */
    public FileAlterationMonitor getRadarWinProMonitor() {
        String pathnameBase = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getRadarWindProfileMonitorPath();
        String pathnameParse = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getRadarWindProfileMonitorPath();
        FileAlterationObserver observerBase = new FileAlterationObserver(new File(pathnameBase), getFileFilter());
        FileAlterationObserver observerParse = new FileAlterationObserver(new File(pathnameParse), getFileFilter());
        observerBase.addListener(new RadarWindProfileListener(radarWindProfileService, commonPath));
        observerParse.addListener(new RadarWindProfileListener(radarWindProfileService, commonPath));
        logger.info(TypeEnum.TYPE_RADAR_WIND_PROFILE.getName() + " 开始监控路径为： " + observerBase.getDirectory());
        logger.info(TypeEnum.TYPE_RADAR_WIND_PROFILE.getName() + " 开始监控路径为： " + observerParse.getDirectory());
        return new FileAlterationMonitor(interval, observerBase, observerParse);
    }

    /**
     * 获取 EcmwfHr 监控数据
     */
    public FileAlterationMonitor getEcmwfHrMonitor() {
        String pathnameBase = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getEcmwfHrMonitorPath();
        FileAlterationObserver observerBase = new FileAlterationObserver(new File(pathnameBase), getFileFilter());
        observerBase.addListener(new EcmwfHrListener(ecmwfHrService));
        logger.info(TypeEnum.TYPE_ECMWF_HR.getName() + " 开始监控路径为： " + observerBase.getDirectory());
        return new FileAlterationMonitor(interval, observerBase);
    }

    /**
     * 获取 SwcWarr 监控数据
     */
    public FileAlterationMonitor getSwcWarrMonitor() {
        String pathnameBase = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getSwcWarrMonitorPath();
        FileAlterationObserver observerBase = new FileAlterationObserver(new File(pathnameBase), getFileFilter());
        observerBase.addListener(new SwcWarrListener(swcWarrService));
        logger.info(TypeEnum.TYPE_SWC_WARR.getName() + " 开始监控路径为： " + observerBase.getDirectory());
        return new FileAlterationMonitor(interval, observerBase);
    }

    /**
     * 获取 SwcWarm 监控数据
     */
    public FileAlterationMonitor getSwcWarmMonitor() {
        String pathnameBase = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getSwcWarmMonitorPath();
        FileAlterationObserver observerBase = new FileAlterationObserver(new File(pathnameBase), getFileFilter());
        observerBase.addListener(new SwcWarmListener(swcWarmService));
        logger.info(TypeEnum.TYPE_SWC_WARM.getName() + " 开始监控路径为： " + observerBase.getDirectory());
        return new FileAlterationMonitor(interval, observerBase);
    }

    public FileAlterationMonitor getFusion12hMonitor() {
        String pathname = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getFusion12hMonitorPath();
        FileAlterationObserver observer = new FileAlterationObserver(new File(pathname), getFileFilter());
        observer.addListener(new Fusion12HFileListener(fusion12HService));
        logger.info(TypeEnum.TYPE_FUSION_12H.getName() + " 开始监控路径为： " + observer.getDirectory());
        return new FileAlterationMonitor(interval, observer);
    }

    public FileAlterationMonitor getCldas1kmMonitor() {
        String pathname = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getCldas1kmMonitorPath();
        FileAlterationObserver observer = new FileAlterationObserver(new File(pathname), getFileFilter());
        observer.addListener(new Cldas1kmFileListener(cldas1kmService));
        logger.info(TypeEnum.TYPE_CLDAS_1KM.getName() + " 开始监控路径为： " + observer.getDirectory());
        return new FileAlterationMonitor(interval, observer);
    }

    public FileAlterationMonitor getWindfieldInversionMonitor() {
        String pathname = commonPath.getDisk() + commonPath.getParseMonitorPath() + commonPath.getWindfieldInversionMonitorPath();
        FileAlterationObserver observer = new FileAlterationObserver(new File(pathname), getFileFilter());
        observer.addListener(new WindfieldInversionFileListener(windfieldInversionService));
        logger.info(TypeEnum.TYPE_3D_WINDFIELD_INVERSION + " 开始监控路径为： " + observer.getDirectory());
        return new FileAlterationMonitor(interval, observer);
    }


    private FileFilter getFileFilter() {
        IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
        IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(""));
        return FileFilterUtils.or(directories, files);
    }
}