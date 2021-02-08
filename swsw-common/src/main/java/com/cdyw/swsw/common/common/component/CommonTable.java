package com.cdyw.swsw.common.common.component;

import com.cdyw.swsw.common.common.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 调用表相关的公共路径，全部从yaml文件获取
 *
 * @author jovi
 */
@ConfigurationProperties(prefix = "common.table")
@PropertySource(value = "classpath:common-table.yml", factory = YamlPropertySourceFactory.class)
@Component
@Data
public class CommonTable implements Serializable {
    private String link;

    private String log;
    private String pre;
    private String sufBase;
    private String sufParse;

    private String typeAtStation;
    private String staAtStation;

    private String typeSatellite;
    private String staSatelliteFy2g;
    private String staSatelliteFy4a;

    private String typeRadarWeather;
    private String staRadarWeather;

    private String typeRadarWindProfile;
    private String staRadarWindprofile56189;
    private String staRadarWindprofile56272;
    private String staRadarWindprofile56276;
    private String staRadarWindprofile56285;
    private String staRadarWindprofile56286;
    private String staRadarWindprofile56290;
    private String staRadarWindprofile56296;

    private String typeRadarPhasedArray;
    private String staRadarPhasedArray1;
    private String staRadarPhasedArray2;
    private String staRadarPhasedArray3;
    private String staRadarPhasedArray4;
    private String staRadarPhasedArray;
    private String staRadarPhasedArrayChongzhou;
    private String staRadarPhasedArrayTianfu;
    private String staRadarPhasedArrayXindu;

    private String typeRadarExt;
    private String staRadarExt;

    private String type3dGridFusion;
    private String sta3dGridFusion;

    private String typeThunder;
    private String staThunder;

    private String typeShortForecast;
    private String staShortForecast;

    private String typeForecastCheckRain;
    private String typeForecastCheckTem;
    private String typeForecastCheckWinddirect;
    private String typeForecastCheckWindspeed;
    private String staForecastCheckTs;
    private String staForecastCheckSkill;
    private String staForecastCheckMissingreport;
    private String staForecastCheckNoreport;

    private String typeCldas1km;
    private String typeEcmwfHr;
    private String typeGrapes3km;
    private String typeSwcWarm;
    private String typeSwcWarr;
    private String typeFusion2h;
    private String typeFusion12h;
    private String typeWeatherRadarExtrapolation;
    private String staWeatherRadarExtrapolation;
    private String typePhasedarrayRadarExtrapolation;
    private String staPhasedarrayRadarExtrapolation;
    private String type3dWindfieldInversion;
}
