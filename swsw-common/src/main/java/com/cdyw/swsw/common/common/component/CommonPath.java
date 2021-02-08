package com.cdyw.swsw.common.common.component;

import com.cdyw.swsw.common.common.factory.YamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;

/**
 * 调用接口的公共路径，全部从yaml文件获取
 *
 * @author jovi
 */
@ConfigurationProperties(prefix = "common.path")
@PropertySource(value = "classpath:common-path.yml", factory = YamlPropertySourceFactory.class)
@Component
@Data
public class CommonPath implements Serializable {

    private String disk;
    private String commonPath;

    private String baseMonitorPath;
    private String parseMonitorPath;
    private String grapes3kmMonitorPath;
    private String fusion12hMonitorPath;
    private String radarPhasedArrayMonitorPath;
    private String radarWeatherMonitorPath;
    private String radarWindProfileMonitorPath;
    private String ecmwfHrMonitorPath;
    private String swcWarrMonitorPath;
    private String swcWarmMonitorPath;
    private String cldas1kmMonitorPath;
    private String windfieldInversionMonitorPath;
    private String weatherRadarExtrapolationMonitor;
    private String satelliteMonitor;

    private String atstationPath;
    private String atstationParsePath;
    private String atstationStaAll;
    private String atstationTEM;
    private String atstationPRS;
    private String atstationRHU;
    private String atstationWIN;
    private String atstationPRE1H;
    private String atstationPRE10MIN;
    private String atstationVIS;

    private String satellitePath;
    private String satelliteParsePath;
    private String satelliteStaFY2G;
    private String satelliteStaFY4A;
    private String satelliteIR1;
    private String satelliteIR2;
    private String satelliteIR3;
    private String satelliteIR4;
    private String satelliteVIS;
    private String satelliteCHANNEL12;
    private String satelliteCHANNEL10;
    private String satelliteCHANNEL02;

    private String radarWeaPath;
    private String radarWeaParsePath;
    private String radarWeaStaZ9280;
    private String radarWeaEB;
    private String radarWeaET;
    private String radarWeaMAXREF;
    private String radarWeaRZ;
    private String radarWeaVIL;

    private String radarWindProfilePath;
    private String radarWindProfileParsePath;
    private String radarWindProfileStaW;
    private String radarWindProfileSta56189;
    private String radarWindProfileSta56272;
    private String radarWindProfileSta56276;
    private String radarWindProfileSta56285;
    private String radarWindProfileSta56286;
    private String radarWindProfileSta56290;
    private String radarWindProfileSta56296;
    private String radarWindProfileH;
    private String radarWindProfileO;
    private String radarWindProfileR;
    private String radarWindProfileRAD;
    private String radarPhaArrPath;
    private String radarPhaArrParsePath;
    private String radarPhaArrSta1;
    private String radarPhaArrSta2;
    private String radarPhaArrSta3;
    private String radarPhaArrSta4;
    private String radarPhaArrStaMulti;
    private String radarPhaArrStaChongZhou;
    private String radarPhaArrStaTianFu;
    private String radarPhaArrStaXinDu;
    private String radarPhaArrCR;
    private String radarPhaArrMAXREF;
    private String radarPhaArrCRPZ;
    private String radarPhaArrEB;
    private String radarPhaArrEBPZ;
    private String radarPhaArrET;
    private String radarPhaArrETPZ;
    private String radarPhaArrRZ;
    private String radarPhaArrRZPZ;
    private String radarPhaArrVIL;
    private String radarPhaArrVILPZ;

    private String radarExtPath;
    private String radarExtParsePath;
    private String radarExtStaZ9911;
    private String radarExtEXTRACR;
    private String radarExtEXTRARZ;
    private String radarExtEXTRAVIL;

    private String d3GridFusionPath;
    private String d3GridFusionParsePath;
    private String d3GridFusionStaThird;
    private String d3GridFusionD3WF;
    private String d3GridFusionD3TEM;
    private String d3GridFusionD3RHU;
    private String d3GridFusionD3PRS;

    private String thunderPath;
    private String thunderParsePath;

    private String shortForecastPath;
    private String shortForecastParsePath;
    private String shortForecastStaNone;
    private String shortForecastTEM;
    private String shortForecastWINDFIELD;
    private String shortForecastPRS;
    private String shortForecastRAIN;
    private String shortForecastRHU;

    private String cldas1kmPath;
    private String cldas1kmParsePath;
    private String cldas1kmRain1h;
    private String cldas1kmWindfield;
    private String cldas1kmTem;
    private String cldas1kmRhu;
    private String cldas1kmUwin;
    private String cldas1kmVwin;

    private String SWCWARRPath;
    private String SWCWARRParsePath;

    private String SWCWARMPath;
    private String SWCWARMParsePath;
    private String SWCWARMRain12h;
    private String SWCWARMWindField;
    private String SWCWARMTemp;
    private String SWCWARMRhu;
    private String SWCWARMVis;
    private String SWCWARMPres;

    private String grapes3kmPath;
    private String grapes3kmParsePath;

    private String fusion2hPath;
    private String fusion2hParsePath;

    private String fusion12hPath;
    private String fusion12hParsePath;
    private String fusion12hTEM;
    private String fusion12hWINDFIELD;
    private String fusion12hPRS;
    private String fusion12hRAIN;
    private String fusion12hRHU;

    private String weatherRadarExtraplationPath;
    private String weatherRadarExtraplationParsePath;
    private String weatherRadarExtraplationStaZ9280;
    private String weatherRadarExtraplationStaMAXREF;
    private String phasedArrayRadarExtraplationPath;
    private String phasedArrayRadarExtraplationParsePath;
    private String phasedArrayRadarExtraplationStaMultistation;
    private String phasedArrayRadarExtraplationStaMAXREF;

    private String dataProductPath;
    private String dataProductTemplatePath;
    private String dataProductImagePath;
    private String dataProductShortForecast;
    private String dataProductGridForecast;
    private String dataProductLineForecast;
    private String dataProductStadiumWarning;
    private String dataProductDisasterForecast;
    private String dataProductIrregularShort;
    private String dataProductWarningSignal;
    private String dataProductDefaultPath;
    private String dataProductTempPath;
    private String dataProductFinalPath;

    private String windfieldInversionPath;
    private String windfieldInversionParsePath;
    private String windfieldInversionWINDFIELD;

    private String cmdCommandPrefix;
    private String python;
    private String satelliteDownloadScript;
    private String satelliteCreateImgScript;
    private String satelliteChannel02ResolveScript;
    private String satelliteChannel10ResolveScript;
    private String satelliteChannel12ResolveScript;
    private String cldas1kmDownloadScript;
    private String swcwarmDownloadScript;
    private String cldas1kmWindfieldResolveScript;
    private String cldas1kmTemRainResolveScript;
    private String fusion12hResolveScript;
    private String radarExtraplationMAXREFResolveScript;
    private String windfieldInversionResolveScript;
    private String lonlatFusion12hPath;
    private String lonlatArrayFusion12hPath;
    private String lonlatCldas1kmPath;
    private String lonlatWindfieldInversionPath;
    private String colorPrsFusion12hPath;
    private String colorRainFusion12hPath;
    private String colorRhuFusion12hPath;
    private String colorTemFusion12hPath;
    private String weatherRadarExtrapolationBasemap;
    private String wgrib2Path;
    private String mdfsPath;
    private String mdfsIpconfig;

    private String mdfsSatellite;
    private String mdfsSatelliteFY4A;
    private String mdfsSatelliteL1;
    private String mdfsSatelliteChina;
    private String mdfsSatellitec012;
    private String mdfsSatellitec002;
    private String mdfsSatelliteTime;
    private String mdfsSatellitec002Prefix;
    private String mdfsSatellitec012Prefix;
    private String mdfsSatellitePathSufffix;


    private String mdfsCldas1km;
    private String mdfsCldas1kmRain1h;
    private String mdfsCldas1kmWindField;
    private String mdfsCldas1kmTem;
    private String mdfsCldas1kmRhu;
    private Map<String, String> mdfsCldas1kmHeight;
    private String mdfsCldas1kmSuffix;

    private String ecmwfHrPath;
    private String ecmwfHrParsePath;

    private String mdfsSWCWARM;
    private String mdfsSWCWARMRain12h;
    private String mdfsSWCWARMWindfield;
    private String mdfsSWCWARMTemp;
    private String mdfsSWCWARMPres;
    private String mdfsSWCWARMVis;
    private String mdfsSWCWARMRhu;
    private String mdfsSWCWARMSuffix;

}
