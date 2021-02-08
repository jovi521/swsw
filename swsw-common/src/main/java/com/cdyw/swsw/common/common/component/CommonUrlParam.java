package com.cdyw.swsw.common.common.component;

import com.cdyw.swsw.common.common.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 调用接口的公共参数，全部从yaml文件获取
 *
 * @author jovi
 */
@ConfigurationProperties(prefix = "common.param")
@PropertySource(value = "classpath:common-url-param.yml", factory = YamlPropertySourceFactory.class)
@Component
@Data
public class CommonUrlParam implements Serializable {

    private String url;
    private String userId1;
    private String pwd1;
    private String userId2;
    private String pwd2;

    private String dataFormat;

    private String dataCodeAtstation;
    private String dataCodeSatellite;
    /**
     * 暂时天气雷达默认资料代码为：标准格式的雷达全体扫基数据[RADA_CHN_DOR_L2_FILE_TAB] （RADA_L2_VOSC_ST）
     */
    private String dataCodeRadarWeather;
    private String dataCodeRadarWindProfile;
    private String dataCodeThunder;
    private String dataCodeCldas1km;
    private String dataCodeGrapes3km;

    private String elementsAtstation;
    private String elementsSatellite;
    private String elementsRadarWeather;
    private String elementsRadarWindProfile;
    private String elementsThunder;
    private String elementsCldas1km;
    private String elementsGrapes3km;

    private String limitCnt;
    private String minStaId;
    private String orderBy;
    private String staLevels;
    private String maxStaId;
    private String eleValueRanges;
    private String adminCodes;
    private String timeRange;
    private String staTypes;
    private String staIdRadarWeather;
    private String minLon;
    private String maxLon;
    private String minLat;
    private String maxLat;

    private String interfaceIdAtsGetSurf;
    private String interfaceIdSatGetSate;
    private String interfaceIdRadWeaGetRada;
    private String interfaceIdRadWinGetRada;
    private String interfaceIdThunderGetRada;
    private String interfaceIdCldas1kmGetNafp;
    private String interfaceIdGrapes3kmGetSevp;

    private String fcstEles;

    private String denominator;
}
