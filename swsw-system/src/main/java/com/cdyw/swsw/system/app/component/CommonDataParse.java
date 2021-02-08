package com.cdyw.swsw.system.app.component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.dic.DicLonLat;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.AtstationParseTxt;
import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import com.cdyw.swsw.common.domain.ao.txt.SatelliteFY4AParseTxt;
import com.cdyw.swsw.common.domain.ao.txt.ShortForecastParseTxt;
import com.cdyw.swsw.system.dao.atstation.AtstationMapper;
import com.cdyw.swsw.system.dao.dic.DicColorMapper;
import com.cdyw.swsw.system.dao.dic.DicLonLatMapper;
import com.cdyw.swsw.system.dao.fusion12h.Fusion12hMapper;
import com.cdyw.swsw.system.dao.grid.D3GridFusionMapper;
import com.cdyw.swsw.system.dao.radar.RadarExtraMapper;
import com.cdyw.swsw.system.dao.radar.RadarPhasedArrayMapper;
import com.cdyw.swsw.system.dao.radar.RadarWeatherMapper;
import com.cdyw.swsw.system.dao.radar.RadarWindProfileMapper;
import com.cdyw.swsw.system.dao.satellite.SatelliteMapper;
import com.cdyw.swsw.system.dao.shortforecast.ShortForecastMapper;
import com.cdyw.swsw.system.vo.common.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author jovi
 */
@Component
public class CommonDataParse {

    Logger logger = LoggerFactory.getLogger(CommonDataParse.class);

    private final RadarWeatherMapper radarWeatherMapper;

    private final SatelliteMapper satelliteMapper;

    private final RadarWindProfileMapper radarWindProfileMapper;

    private final RadarPhasedArrayMapper radarPhasedArrayMapper;

    private final AtstationMapper atstationMapper;

    private final RadarExtraMapper radarExtraMapper;

    private final D3GridFusionMapper d3GridFusionMapper;

    private final DicLonLatMapper dicLonLatMapper;

    private final DicColorMapper dicColorMapper;

    private final ShortForecastMapper shortForecastMapper;

    private final Fusion12hMapper fusion12hMapper;

    private final CommonPath commonPath;

    private final CommonTable commonTable;

    private final CommonTableName commonTableName;

    @Autowired
    public CommonDataParse(RadarWeatherMapper radarWeatherMapper, SatelliteMapper satelliteMapper,
                           RadarWindProfileMapper radarWindProfileMapper, RadarPhasedArrayMapper radarPhasedArrayMapper,
                           AtstationMapper atstationMapper, RadarExtraMapper radarExtraMapper,
                           D3GridFusionMapper d3GridFusionMapper, DicLonLatMapper dicLonLatMapper,
                           DicColorMapper dicColorMapper, ShortForecastMapper shortForecastMapper,
                           Fusion12hMapper fusion12hMapper, CommonTable commonTable,
                           CommonPath commonPath, CommonTableName commonTableName) {
        this.radarWeatherMapper = radarWeatherMapper;
        this.satelliteMapper = satelliteMapper;
        this.radarWindProfileMapper = radarWindProfileMapper;
        this.radarPhasedArrayMapper = radarPhasedArrayMapper;
        this.atstationMapper = atstationMapper;
        this.radarExtraMapper = radarExtraMapper;
        this.d3GridFusionMapper = d3GridFusionMapper;
        this.dicColorMapper = dicColorMapper;
        this.dicLonLatMapper = dicLonLatMapper;
        this.shortForecastMapper = shortForecastMapper;
        this.fusion12hMapper = fusion12hMapper;
        this.commonPath = commonPath;
        this.commonTable = commonTable;
        this.commonTableName = commonTableName;
    }

    /**
     * 根据产品类型查询对应的色标值
     *
     * @param productType
     * @return
     */
    public List<HashMap<String, Object>> getColorByProductType(String productType, String parentCode) {
        Assert.notNull(parentCode, "param parentCode is null");
        List<HashMap<String, Object>> resultList = dicColorMapper.selectByProcutType(productType, parentCode);
        return resultList;
    }


    /**
     * 获取廓线雷达(风廓线雷达、问廓线雷达。。。)的站点信息
     *
     * @param profileRadarType String
     * @return List
     */
    public List<Map<String, Object>> getStationByRadarType(String profileRadarType) {
        Assert.notNull(profileRadarType, "param profileRadarType is null");
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<DicLonLat> lonlatList = dicLonLatMapper.getLonLatByParentCode(profileRadarType);
        for (DicLonLat dicLonlat : lonlatList) {
            Map<String, Object> map = new HashMap<>();
            map.put("stationName", dicLonlat.getCode());
            map.put("lon", dicLonlat.getLon());
            map.put("lat", dicLonlat.getLat());
            resultList.add(map);
        }
        return resultList;
    }

    /**
     * 专门用来获取自动站解析后的TXT文件内容，最后组装成 AtstationParseTxt 的json文件发送给前端
     */
    public AtstationParseTxt getAtstationTxtByPara(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime) throws FileNotFoundException {
        AtstationParseTxt atstationParseTxt = new AtstationParseTxt();
        String name = parseFileToString(typeEnum, staIdC, type, startTime, endTime, null, null);
        List<String> list = parseFileToList(typeEnum, staIdC, type, name);
        // 遍历集合
        for (String s : list) {
            String sBefore = StrUtil.subBefore(s, ":", false);
            String sAfter = StrUtil.subAfter(s, ":", false);
            if ("latmin".equalsIgnoreCase(sBefore)) {
                atstationParseTxt.setLatMin(sAfter);
            } else if ("latmax".equalsIgnoreCase(sBefore)) {
                atstationParseTxt.setLatMax(sAfter);
            } else if ("lonmin".equalsIgnoreCase(sBefore)) {
                atstationParseTxt.setLonMin(sAfter);
            } else if ("lonmax".equalsIgnoreCase(sBefore)) {
                atstationParseTxt.setLonMax(sAfter);
            } else if ("starttime".equalsIgnoreCase(sBefore)) {
                atstationParseTxt.setStartTime(sAfter);
            }
        }
        // 将文件名也传参数过去
        atstationParseTxt.setFileName(StrUtil.subBefore(name, ".", true));
        return atstationParseTxt;
    }

    /**
     * 专门用来获取卫星解析后的TXT文件内容，最后组装成 SatelliteFY4AParseTxt 的json文件发送给前端
     */
    public SatelliteFY4AParseTxt getSatelliteTxtByPara(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime) throws FileNotFoundException {
        SatelliteFY4AParseTxt satelliteFY4AParseTxt = new SatelliteFY4AParseTxt();
        String name = parseFileToString(typeEnum, staIdC, type, startTime, endTime, null, null);
        List<String> list = parseFileToList(typeEnum, staIdC, type, name);
        // 遍历集合
        for (String s : list) {
            String sBefore = StrUtil.subBefore(s, ":", false);
            String sAfter = StrUtil.subAfter(s, ":", false);
            if ("latmin".equalsIgnoreCase(sBefore)) {
                satelliteFY4AParseTxt.setLatMin(sAfter);
            } else if ("latmax".equalsIgnoreCase(sBefore)) {
                satelliteFY4AParseTxt.setLatMax(sAfter);
            } else if ("lonmin".equalsIgnoreCase(sBefore)) {
                satelliteFY4AParseTxt.setLonMin(sAfter);
            } else if ("lonmax".equalsIgnoreCase(sBefore)) {
                satelliteFY4AParseTxt.setLonMax(sAfter);
            } else if ("starttime".equalsIgnoreCase(sBefore)) {
                satelliteFY4AParseTxt.setStartTime(sAfter);
            } else if ("endtime".equalsIgnoreCase(sBefore)) {
                satelliteFY4AParseTxt.setEndTime(sAfter);
            }
        }
        // 将文件名也传参数过去
        satelliteFY4AParseTxt.setFileName(StrUtil.subBefore(name, ".", true));
        return satelliteFY4AParseTxt;
    }

    /**
     * 专门用来获取天气雷达、相控阵雷达解析后的TXT文件内容，最后组装成 CommonRadarParseTxt 的json文件发送给前端
     */
    public CommonRadarParseTxt getTxtByPara(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime) throws FileNotFoundException {
        String name = parseFileToString(typeEnum, staIdC, type, startTime, endTime, null, null);
        List<String> list = parseFileToList(typeEnum, staIdC, type, name);
        return getCommonRadarParseTxtByPara(name, list);
    }

    /**
     * 专门用来获取雷达外推解析后的TXT文件内容，最后组装成 CommonRadarParseTxt 的json文件发送给前端
     */
    public CommonRadarParseTxt getRadarExtTxtByPara(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime, String mcode) throws FileNotFoundException {
        String name = parseFileToString(typeEnum, staIdC, type, startTime, endTime, mcode, null);
        List<String> list = parseFileToList(typeEnum, staIdC, type, name);
        return getCommonRadarParseTxtByPara(name, list);
    }

    /**
     * 专门用来获取短临预报解析后的TXT文件内容，最后组装成 ShortForecastParseTxt 的json文件发送给前端
     */
    public ShortForecastParseTxt getShortForecastTxtByPara(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime, String mcode, String layer) throws FileNotFoundException {
        String name = parseFileToString(typeEnum, staIdC, type, startTime, endTime, mcode, layer);
        List<String> list = parseFileToList(typeEnum, staIdC, type, name);
        return getShortForecastParseTxtByPara(name, list);
    }

    private ShortForecastParseTxt getShortForecastParseTxtByPara(String name, List<String> list) {
        ShortForecastParseTxt TXT = new ShortForecastParseTxt();
        // 遍历集合
        for (String s : list) {
            String sBefore = StrUtil.subBefore(s, ":", false);
            String sAfter = StrUtil.subAfter(s, ":", false);
            if ("latmin".equalsIgnoreCase(sBefore)) {
                TXT.setLatMin(sAfter);
            } else if ("latmax".equalsIgnoreCase(sBefore)) {
                TXT.setLatMax(sAfter);
            } else if ("lonmin".equalsIgnoreCase(sBefore)) {
                TXT.setLonMin(sAfter);
            } else if ("lonmax".equalsIgnoreCase(sBefore)) {
                TXT.setLonMax(sAfter);
            } else if ("forecastcount".equalsIgnoreCase(sBefore)) {
                TXT.setForecastcount(sAfter);
            } else if ("layer".equalsIgnoreCase(sBefore)) {
                TXT.setLayer(Integer.parseInt(sAfter));
            } else if ("time".equalsIgnoreCase(sBefore)) {
                TXT.setTime(Long.parseLong(sAfter));
            } else if ("filedir".equalsIgnoreCase(sBefore)) {
                TXT.setFileDir(sAfter);
            } else if ("filename".equalsIgnoreCase(sBefore)) {
                TXT.setFileName(sAfter);
            }
        }
        return TXT;
    }

    private CommonRadarParseTxt getCommonRadarParseTxtByPara(String name, List<String> list) {
        CommonRadarParseTxt TXT = new CommonRadarParseTxt();
        // 遍历集合
        for (String s : list) {
            String sBefore = StrUtil.subBefore(s, ":", false);
            String sAfter = StrUtil.subAfter(s, ":", false);
            if ("ProductID".equalsIgnoreCase(sBefore)) {
                TXT.setProductID(sAfter);
            } else if ("ProductName".equalsIgnoreCase(sBefore)) {
                TXT.setProductName(sAfter);
            } else if ("StartTime".equalsIgnoreCase(sBefore)) {
                TXT.setStartTime(sAfter);
            } else if ("EndTime".equalsIgnoreCase(sBefore)) {
                TXT.setEndTime(sAfter);
            } else if ("StationNo".equalsIgnoreCase(sBefore)) {
                TXT.setStationNo(sAfter);
            } else if ("StationName".equalsIgnoreCase(sBefore)) {
                TXT.setStationName(sAfter);
            } else if ("RadarCoor".equalsIgnoreCase(sBefore)) {
                TXT.setRadarCoor(sAfter);
            } else if ("MaxDis".equalsIgnoreCase(sBefore)) {
                TXT.setMaxDis(sAfter);
            } else if ("Height".equalsIgnoreCase(sBefore)) {
                TXT.setHeight(sAfter);
            } else if ("Projection".equalsIgnoreCase(sBefore)) {
                TXT.setProjection(sAfter);
            } else if ("LeftBottomCoor".equalsIgnoreCase(sBefore)) {
                TXT.setLeftBottomCoor(sAfter);
            } else if ("RightTopCoor".equalsIgnoreCase(sBefore)) {
                TXT.setRightTopCoor(sAfter);
            } else if ("WidthHeight".equalsIgnoreCase(sBefore)) {
                TXT.setWidthHeight(sAfter);
            } else if ("RadarPos".equalsIgnoreCase(sBefore)) {
                TXT.setRadarPos(sAfter);
            }
        }
        // 将文件名也传参数过去
        TXT.setFileName(StrUtil.subBefore(name, ".", true));
        return TXT;
    }

    /**
     * 气象站专用返回PNG
     */
    public void getAtstationPngByFileName(TypeEnum typeEnum, String staId, String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        Map<String, Object> pathMap = getFilePathByPara(typeEnum, staId, type, fileName);
        String fileAllPath = pathMap.get("fileAllPath").toString();
        // 根据文件名查找文件
        String filePathName = fileAllPath + "/" + fileName + ".svg";
        FileUtil.downloadOrOnlineFile(new File(filePathName), response, isOnLine);
    }


    /**
     * 卫星专用返回PNG
     */
    public void getSatellitePngByFileName(TypeEnum typeEnum, String staId, String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        Map<String, Object> pathMap = getFilePathByPara(typeEnum, staId, type, fileName);
        String fileAllPath = pathMap.get("fileAllPath").toString();
        // 根据文件名查找文件
        String filePathName = fileAllPath + "/" + fileName + ".png";
        FileUtil.downloadOrOnlineFile(new File(filePathName), response, isOnLine);
    }

    /**
     * 天气雷达专用返回PNG
     */
    public void getPngByFileName(TypeEnum typeEnum, String staIdC, String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        Map<String, Object> pathMap = getFilePathByPara(typeEnum, staIdC, type, fileName);
        String fileAllPath = pathMap.get("fileAllPath").toString();
        // 将文件名后缀的_PNG消除
        fileName = fileName.replaceAll("_PNG", "");
        // 根据文件名查找文件
        String filePathName = fileAllPath + fileName + ".png";
        filePathName = filePathName.replaceAll("//",  Matcher.quoteReplacement(File.separator));
        logger.info("filePathName:" + filePathName);
        logger.info("fileAllPath:" + fileAllPath);
        logger.info("fileName:" + fileName);
        FileUtil.downloadOrOnlineFile(new File(filePathName), response, isOnLine);
    }

    /**
     * 短临预报专用返回数据，可能有png,json
     */
    public void getShortForecastPngByFileName(TypeEnum typeEnum, String staIdC, String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        Map<String, Object> pathMap = getFilePathByPara(typeEnum, staIdC, type, fileName);
        String fileAllPath = pathMap.get("fileAllPath").toString();
        // 根据文件名查找文件
        String filePathName = fileAllPath + fileName;
        FileUtil.downloadOrOnlineFile(new File(filePathName), response, isOnLine);
    }

    /**
     * 三维格点融合专用返回Json
     */
    public void getJsonByFileName(TypeEnum typeEnum, String staIdC, String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        Map<String, Object> pathMap = getFilePathByPara(typeEnum, staIdC, type, fileName);
        String fileAllPath = pathMap.get("fileAllPath").toString();
        // 根据文件名查找文件
        String filePathName = fileAllPath + fileName;
        FileUtil.downloadOrOnlineFile(new File(filePathName), response, isOnLine);
    }

    /**
     * 专门用来查询风廓线TXT文件
     */
    public void getTxtWindProByPara(TypeEnum typeEnum, String staIdC, String timeType, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        Map<String, Object> pathMap = getFilePathByPara(typeEnum, staIdC, timeType, fileName);
        String fileAllPath = pathMap.get("fileAllPath").toString();
        // 根据文件名查找文件
        String filePathName = fileAllPath + fileName;
        filePathName = filePathName.replaceAll("//", "/");
        FileUtil.downloadOrOnlineFile(new File(filePathName), response, isOnLine);
    }

    /**
     * 根据第一个接口查询出来的文件名，再加上类型和产品继续查询实际文件的路径
     */
    public Map<String, Object> getFilePathByPara(TypeEnum typeEnum, String staIdC, String type, String fileName) {
        Map<String, Object> map = new HashMap<>(16);
        String[] s = fileName.split("_");
        String date = s[0];
        String fileAllPath = null;
        switch (typeEnum) {
            case TYPE_RADAR_WEATHER:
                switch (type) {
                    case "7":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath()+ commonPath.getRadarWeaStaZ9280() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaEB();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath() + commonPath.getRadarWeaStaZ9280() + "/" + date + "/" + commonPath.getRadarWeaEB();
                        break;
                    case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath()+ commonPath.getRadarWeaStaZ9280() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaET();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath() + commonPath.getRadarWeaStaZ9280() + "/" + date + "/" + commonPath.getRadarWeaET();
                        break;
                    case "10":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath()+ commonPath.getRadarWeaStaZ9280() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaMAXREF();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath() + commonPath.getRadarWeaStaZ9280() + "/" + date + "/" + commonPath.getRadarWeaMAXREF();
                        break;
                    case "39":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath()+ commonPath.getRadarWeaStaZ9280() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaRZ();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath() + commonPath.getRadarWeaStaZ9280() + "/" + date + "/" + commonPath.getRadarWeaRZ();
                        break;
                    case "25":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath()+ commonPath.getRadarWeaStaZ9280() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaVIL();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath() + commonPath.getRadarWeaStaZ9280() + "/" + date + "/" + commonPath.getRadarWeaVIL();
                        break;
                    default:
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaParsePath() + commonPath.getRadarWeaStaZ9280() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                        break;
                }
                break;
            case TYPE_SATELLITE:
                String dateSatellite = s[9].substring(0, 8);
                switch (type) {
                    case "801":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getSatelliteIR1();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + dateSatellite + "/" + commonPath.getSatelliteIR1();
                        break;
                    case "802":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getSatelliteIR2();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + dateSatellite + "/" + commonPath.getSatelliteIR2();
                        break;
                    case "803":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getSatelliteIR3();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + dateSatellite + "/" + commonPath.getSatelliteIR3();
                        break;
                    case "804":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getSatelliteIR4();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + dateSatellite + "/" + commonPath.getSatelliteIR4();
                        break;
                    case "805":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getSatelliteVIS();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + "/" + dateSatellite + commonPath.getSatelliteVIS();
                        break;
                    case "807":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getSatelliteVIS();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY4A() + "/" + dateSatellite + commonPath.getSatelliteCHANNEL02();
                        break;
                    case "817":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getSatelliteVIS();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY4A() + "/" + dateSatellite + commonPath.getSatelliteCHANNEL12();
                        break;
                    default:
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY2G() + dateSatellite + "/";
                        break;
                }
                break;
            case TYPE_RADAR_WIND_PROFILE:
                String dateWP = s[4];
                String format = DateUtil.format(LocalDateTime.parse(dateWP, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), "yyyyMMdd");
                switch (staIdC) {
                    case "56189":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56189() + "/" + format + "/" + commonPath.getRadarWindProfileR();
                                break;
                            case "30":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileH();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56189() + "/" + format + "/" + commonPath.getRadarWindProfileH();
                                break;
                            case "60":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56189() + "/" + format + "/" + commonPath.getRadarWindProfileO();
                                break;
                            case "5":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56189() + "/" + format + "/" + commonPath.getRadarWindProfileRAD();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56189() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                                break;
                        }
                        break;
                    case "56272":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56272() + "/" + format + "/" + commonPath.getRadarWindProfileR();
                                break;
                            case "30":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileH();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56272() + "/" + format + "/" + commonPath.getRadarWindProfileH();
                                break;
                            case "60":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56272() + "/" + format + "/" + commonPath.getRadarWindProfileO();
                                break;
                            case "5":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56272() + "/" + format + "/" + commonPath.getRadarWindProfileRAD();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56272() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                                break;
                        }
                        break;
                    case "56276":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56276() + "/" + format + "/" + commonPath.getRadarWindProfileR();
                                break;
                            case "30":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileH();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56276() + "/" + format + "/" + commonPath.getRadarWindProfileH();
                                break;
                            case "60":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56276() + "/" + format + "/" + commonPath.getRadarWindProfileO();
                                break;
                            case "5":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56276() + "/" + format + "/" + commonPath.getRadarWindProfileRAD();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56276() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                                break;
                        }
                        break;
                    case "56285":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56285() + "/" + format + "/" + commonPath.getRadarWindProfileR();
                                break;
                            case "30":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileH();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56285() + "/" + format + "/" + commonPath.getRadarWindProfileH();
                                break;
                            case "60":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56285() + "/" + format + "/" + commonPath.getRadarWindProfileO();
                                break;
                            case "5":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56285() + "/" + format + "/" + commonPath.getRadarWindProfileRAD();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56285() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                                break;
                        }
                        break;
                    case "56286":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56286() + "/" + format + "/" + commonPath.getRadarWindProfileR();
                                break;
                            case "30":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileH();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56286() + "/" + format + "/" + commonPath.getRadarWindProfileH();
                                break;
                            case "60":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56286() + "/" + format + "/" + commonPath.getRadarWindProfileO();
                                break;
                            case "5":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56286() + "/" + format + "/" + commonPath.getRadarWindProfileRAD();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56286() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                                break;
                        }
                        break;
                    case "56290":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56290() + "/" + format + "/" + commonPath.getRadarWindProfileR();
                                break;
                            case "30":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileH();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56290() + "/" + format + "/" + commonPath.getRadarWindProfileH();
                                break;
                            case "60":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56290() + "/" + format + "/" + commonPath.getRadarWindProfileO();
                                break;
                            case "5":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56290() + "/" + format + "/" + commonPath.getRadarWindProfileRAD();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56290() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                                break;
                        }
                        break;
                    case "56296":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56296() + "/" + format + "/" + commonPath.getRadarWindProfileR();
                                break;
                            case "30":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileH();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56296() + "/" + format + "/" + commonPath.getRadarWindProfileH();
                                break;
                            case "60":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56296() + "/" + format + "/" + commonPath.getRadarWindProfileO();
                                break;
                            case "5":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileStaW() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileO();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56296() + "/" + format + "/" + commonPath.getRadarWindProfileRAD();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56296() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                                break;
                        }
                        break;
                    default:
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWindProfilePath() + commonPath.getRadarWindProfileSta56296() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
                        break;
                }
                break;
            case TYPE_RADAR_PHASED_ARRAY:
                switch (staIdC) {
                    case "00001":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaChongZhou() + "/" + date + "/" + commonPath.getRadarPhaArrET();
                                break;
                            case "7":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaChongZhou() + "/" + date + "/" + commonPath.getRadarPhaArrEB();
                                break;
                            case "10":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaChongZhou() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                            case "25":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaChongZhou() + "/" + date + "/" + commonPath.getRadarPhaArrVIL();
                                break;
                            case "39":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaChongZhou() + "/" + date + "/" + commonPath.getRadarPhaArrRZ();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaChongZhou() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                        }
                        break;
                    case "00002":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaTianFu() + "/" + date + "/" + commonPath.getRadarPhaArrET();
                                break;
                            case "7":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaTianFu() + "/" + date + "/" + commonPath.getRadarPhaArrEB();
                                break;
                            case "10":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaTianFu() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                            case "25":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaTianFu() + "/" + date + "/" + commonPath.getRadarPhaArrVIL();
                                break;
                            case "39":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaTianFu() + "/" + date + "/" + commonPath.getRadarPhaArrRZ();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaTianFu() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                        }
                        break;
                    case "00003":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaXinDu() + "/" + date + "/" + commonPath.getRadarPhaArrET();
                                break;
                            case "7":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaXinDu() + "/" + date + "/" + commonPath.getRadarPhaArrEB();
                                break;
                            case "10":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaXinDu() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                            case "25":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaXinDu() + "/" + date + "/" + commonPath.getRadarPhaArrVIL();
                                break;
                            case "39":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaXinDu() + "/" + date + "/" + commonPath.getRadarPhaArrRZ();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaXinDu() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                        }
                        break;
                    case "00004":
                        switch (type) {
                            case "6":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrSta4() + "/" + date + "/" + commonPath.getRadarPhaArrET();
                                break;
                            case "7":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrSta4() + "/" + date + "/" + commonPath.getRadarPhaArrEB();
                                break;
                            case "10":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrSta4() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                            case "25":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrSta4() + "/" + date + "/" + commonPath.getRadarPhaArrVIL();
                                break;
                            case "39":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrSta4() + "/" + date + "/" + commonPath.getRadarPhaArrRZ();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrSta4() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                                break;
                        }
                        break;
                    case "multi":
                        switch (type) {
                            case "121":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaMulti() + "/" + date + "/" + commonPath.getRadarPhaArrETPZ();
                                break;
                            case "122":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaMulti() + "/" + date + "/" + commonPath.getRadarPhaArrEBPZ();
                                break;
                            case "109":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaMulti() + "/" + date + "/" + commonPath.getRadarPhaArrCRPZ();
                                break;
                            case "112":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaMulti() + "/" + date + "/" + commonPath.getRadarPhaArrVILPZ();
                                break;
                            case "119":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrPath() + commonPath.getRadarPhaArrSta1() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWindProfileR();
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaMulti() + "/" + date + "/" + commonPath.getRadarPhaArrRZPZ();
                                break;
                            default:
                                fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaMulti() + "/" + date + "/" + commonPath.getRadarPhaArrCRPZ();
                                break;
                        }
                        break;
                    default:
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarPhaArrParsePath() + commonPath.getRadarPhaArrStaMulti() + "/" + date + "/" + commonPath.getRadarPhaArrMAXREF();
                        break;
                }
                break;
            case TYPE_AT_STATION:
                String atsationDate = s[0].substring(0, 8);
                switch (type) {
                    case "601":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationParsePath() + commonPath.getAtstationStaAll() + "/" + atsationDate + commonPath.getAtstationRHU();
                        break;
                    case "602":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationParsePath() + commonPath.getAtstationStaAll() + "/" + atsationDate + commonPath.getAtstationPRS();
                        break;
                    case "603":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationParsePath() + commonPath.getAtstationStaAll() + "/" + atsationDate + commonPath.getAtstationTEM();
                        break;
                    case "604":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationParsePath() + commonPath.getAtstationStaAll() + "/" + atsationDate + commonPath.getAtstationPRE10MIN();
                        break;
                    case "605":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationParsePath() + commonPath.getAtstationStaAll() + "/" + atsationDate + commonPath.getAtstationPRE1H();
                        break;
                    case "606":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationParsePath() + commonPath.getAtstationStaAll() + "/" + atsationDate + commonPath.getAtstationVIS();
                        break;
                }
                break;
            case TYPE_RADAR_EXT:
                switch (type) {
                    case "201":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarExtPath()+ commonPath.getRadarExtStaZ9911() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaEB();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarExtPath() + commonPath.getRadarExtStaZ9911() + "/" + date + "/" + commonPath.getRadarExtEXTRACR();
                        break;
                    case "204":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarExtPath()+ commonPath.getRadarExtStaZ9911() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaET();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarExtPath() + commonPath.getRadarExtStaZ9911() + "/" + date + "/" + commonPath.getRadarExtEXTRAVIL();
                        break;
                    default:
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarExtPath() + commonPath.getRadarExtStaZ9911() + "/" + date + "/" + "/";
                        break;
                }
                break;
            case TYPE_3D_GRID_FUSION:
                String d3Date = s[0].substring(0, 8);
                switch (type) {
                    case "600":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath()+ commonPath.getD3GridFusionStaThird() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaEB();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath() + commonPath.getD3GridFusionStaThird() + "/" + d3Date + "/" + commonPath.getD3GridFusionD3WF();
                        break;
                    case "601":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath()+ commonPath.getD3GridFusionStaThird() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaEB();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath() + commonPath.getD3GridFusionStaThird() + "/" + d3Date + "/" + commonPath.getD3GridFusionD3RHU();
                        break;
                    case "602":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath()+ commonPath.getD3GridFusionStaThird() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaEB();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath() + commonPath.getD3GridFusionStaThird() + "/" + d3Date + "/" + commonPath.getD3GridFusionD3PRS();
                        break;
                    case "603":
//                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath()+ commonPath.getD3GridFusionStaThird() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + commonPath.getRadarWeaEB();
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath() + commonPath.getD3GridFusionStaThird() + "/" + d3Date + "/" + commonPath.getD3GridFusionD3TEM();
                        break;
                    default:
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getD3GridFusionPath() + commonPath.getD3GridFusionStaThird() + "/" + d3Date + "/";
                        break;
                }
                break;
            case TYPE_SHORT_FORECAST:
                String sfDate = s[0].substring(0, 8);
                switch (type) {
                    case "901":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getShortForecastParsePath() + commonPath.getShortForecastStaNone() + "/" + sfDate + "/" + commonPath.getShortForecastTEM();
                        break;
                    case "902":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getShortForecastParsePath() + commonPath.getShortForecastStaNone() + "/" + sfDate + "/" + commonPath.getShortForecastWINDFIELD();
                        break;
                    case "903":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getShortForecastParsePath() + commonPath.getShortForecastStaNone() + "/" + sfDate + "/" + commonPath.getShortForecastRHU();
                        break;
                    case "904":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getShortForecastParsePath() + commonPath.getShortForecastStaNone() + "/" + sfDate + "/" + commonPath.getShortForecastPRS();
                        break;
                    case "905":
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getShortForecastParsePath() + commonPath.getShortForecastStaNone() + "/" + sfDate + "/" + commonPath.getShortForecastRAIN();
                        break;
                    default:
                        fileAllPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getShortForecastParsePath() + commonPath.getShortForecastStaNone() + "/" + sfDate + "/" + commonPath.getShortForecastTEM();
                        break;
                }
                break;
            default:
                break;
        }
        map.put("fileAllPath", fileAllPath);
        return map;
    }

    /**
     * 通过类型、站点、产品、起始时间、结束时间查询文件名;
     */
    public Map<String, Object> getFileNameByPara(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime, String mcode, String layer) {
        Map<String, Object> map = new HashMap<>(16);
        String t = commonTable.getSufParse();
        String yyyyMMddHHmmss = DateUtils.getDayStringByTimestamp(Long.parseLong(startTime), "yyyyMMddHHmmss");
        if (typeEnum.equals(TypeEnum.TYPE_RADAR_PHASED_ARRAY)) {
            String staChongZhou = "00001";
            String staTianFu = "00002";
            String staXinDu = "00003";
            if (staChongZhou.equals(staIdC)) {
                staIdC = commonTable.getStaRadarPhasedArrayChongzhou();
            } else if (staTianFu.equals(staIdC)) {
                staIdC = commonTable.getStaRadarPhasedArrayTianfu();
            } else if (staXinDu.equals(staIdC)) {
                staIdC = commonTable.getStaRadarPhasedArrayXindu();
            } else {
                staIdC = commonTable.getStaRadarPhasedArray();
            }
        }
        String tableParseNameByType = commonTableName.getTableNameByParam(typeEnum, staIdC, yyyyMMddHHmmss, t);
        Map<String, Object> dataParseByTypeAndTime = null;
        List<Map<String, Object>> dataRadarWindProList = null;
        switch (typeEnum) {
            case TYPE_RADAR_WEATHER:
                dataParseByTypeAndTime = radarWeatherMapper.getDataParseByTypeAndTime(tableParseNameByType, type, Long.valueOf(startTime), Long.valueOf(endTime));
                break;
            case TYPE_SATELLITE:
                dataParseByTypeAndTime = satelliteMapper.getDataParseByTypeAndTime(tableParseNameByType, type, Long.valueOf(startTime), Long.valueOf(endTime));
                break;
            case TYPE_RADAR_WIND_PROFILE:
                tableParseNameByType = tableParseNameByType.replaceAll("_parse", "_base");
                dataRadarWindProList = radarWindProfileMapper.getRadarWinFileByTimestamp(tableParseNameByType, type, Long.parseLong(startTime), Long.parseLong(endTime));
                break;
            case TYPE_RADAR_PHASED_ARRAY:
                dataParseByTypeAndTime = radarPhasedArrayMapper.getDataParseByTypeAndTime(tableParseNameByType, staIdC, type, Long.valueOf(startTime), Long.valueOf(endTime));
                break;
            case TYPE_AT_STATION:
                dataParseByTypeAndTime = atstationMapper.getDataParseByTypeAndTime(tableParseNameByType, type, Long.valueOf(startTime), Long.valueOf(endTime));
                break;
            case TYPE_RADAR_EXT:
                dataParseByTypeAndTime = radarExtraMapper.getDataParseByTypeAndTime(tableParseNameByType, type, startTime, endTime, mcode);
                break;
            case TYPE_3D_GRID_FUSION:
                dataParseByTypeAndTime = d3GridFusionMapper.getDataParseByTypeAndTime(tableParseNameByType, type, Long.valueOf(startTime), Long.valueOf(endTime), mcode);
                break;
            case TYPE_SHORT_FORECAST:
                dataParseByTypeAndTime = shortForecastMapper.getDataParseByTypeAndTime(tableParseNameByType, type, Long.valueOf(startTime), Long.valueOf(endTime), mcode, Integer.parseInt(layer));
                break;
            default:
                break;
        }
        if (typeEnum.equals(TypeEnum.TYPE_RADAR_WIND_PROFILE) && dataRadarWindProList != null) {
            map.put("filenameList", dataRadarWindProList);
        }
        if (dataParseByTypeAndTime != null) {
            map.putAll(dataParseByTypeAndTime);
        }
        return map;
    }

    /**
     * 通过文件路径加文件名找到指定文件，先找到文件名
     */
    public String parseFileToString(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime, String mcode, String layer) {
        String name = null;
        Map<String, Object> fileNameByPara = getFileNameByPara(typeEnum, staIdC, type, startTime, endTime, mcode, layer);
        if (fileNameByPara != null) {
            String dataParseByTypeAndTime = fileNameByPara.get("name").toString();
            switch (typeEnum) {
                case TYPE_AT_STATION:
                    name = dataParseByTypeAndTime.replace(".svg", ".txt");
                    break;
                case TYPE_SATELLITE:
                    name = dataParseByTypeAndTime.replace(".png", ".txt");
                    break;
                case TYPE_RADAR_WEATHER:
                case TYPE_RADAR_PHASED_ARRAY:
                case TYPE_RADAR_EXT:
                    name = dataParseByTypeAndTime.replace(".zip", "_PNG.txt");
                    break;
                case TYPE_3D_GRID_FUSION:
                    name = dataParseByTypeAndTime;
                    break;
                case TYPE_SHORT_FORECAST:
                    if (dataParseByTypeAndTime.endsWith(".png")) {
                        name = dataParseByTypeAndTime.replace(".png", ".txt");
                    } else if (dataParseByTypeAndTime.endsWith(".json")) {
                        name = dataParseByTypeAndTime.replace(".json", ".txt");
                    }
                    break;
            }
        }
        return name;
    }

    /**
     * 通过文件名，然后解析文件成字符串集合
     */
    public List<String> parseFileToList(TypeEnum typeEnum, String staIdC, String type, String name) throws FileNotFoundException {
        List<String> list = new ArrayList<>();
        Map<String, Object> pathMap = getFilePathByPara(typeEnum, staIdC, type, name);
        String fileAllPath = pathMap.get("fileAllPath").toString();

        String filePath = fileAllPath + "/" + name;
        File file = new File(filePath);
        // 读取文件，转换成集合
        InputStream inputStream = new FileInputStream(file);
        list = IoUtil.readLines(inputStream, StandardCharsets.UTF_8, list);
        // 过滤集合
        list.removeAll(Collections.singleton(""));
        list.removeIf(s -> s.contains("<"));
        return list;
    }

    /**
     * 通过时间段查询mcode集合
     */
    public List<String> getMcode(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime) {
        String tableParseNameByType = commonTableName.getTableParseNameByTypeAndTime(typeEnum, staIdC, startTime);
        switch (typeEnum) {
            case TYPE_RADAR_EXT:
                return radarExtraMapper.getMcode(tableParseNameByType, type, startTime, endTime);
            default:
                return null;
        }
    }

    /**
     * 通过时间段查询短临预报mcode集合
     */
    public List<String> getShortForecastMcode(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime) {
        String tableParseNameByType = commonTableName.getTableParseNameByTypeAndTime(typeEnum, staIdC, startTime);
        switch (typeEnum) {
            case TYPE_SHORT_FORECAST:
                return shortForecastMapper.getMcode(tableParseNameByType, type, startTime, endTime);
            default:
                return null;
        }
    }

    /**
     * 通过时间段查询短临预报layer集合
     */
    public List<Integer> getShortForecastLayer(TypeEnum typeEnum, String staIdC, String type, String startTime, String endTime) {
        String tableParseNameByType = commonTableName.getTableParseNameByTypeAndTime(typeEnum, staIdC, startTime);
        switch (typeEnum) {
            case TYPE_SHORT_FORECAST:
                return shortForecastMapper.getLayer(tableParseNameByType, type, startTime, endTime);
            default:
                return null;
        }
    }

    /**
     * 获取fusion12h文件内容
     */
    /*
    public CommonResult<?> getFusion12hFileByPara(String type, String time, String layer) throws FileNotFoundException {
        String tableName = "";
        long startTime = 0L;
        long endTime = 0L;
        Long baseTime = 0L;
        if (StringUtils.isEmpty(time)) {
            // 如果未传入时间，则认为是第一次访问，先根据当前时间查到最新一组数据的基时间
            long currentTime = System.currentTimeMillis();
            String timeStr = DateUtils.getDateToString(currentTime);
            tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() + commonTable.getLink()
                    + timeStr.substring(0, 6) + commonTable.getLink() + commonTable.getSufParse();
            Map<String, Object> timeMap = fusion12hMapper.selectNewestTime(tableName);
            int timecircleCount = 0;
            while (timeMap == null || timeMap.size() == 0) {
                if (timecircleCount <= 3) {
                    timecircleCount++;
                    Date date = DateUtil.parse(timeStr);
                    Date newDate = DateUtil.offset(date, DateField.MONTH, timecircleCount - 2 * timecircleCount);
                    String yyyyMM = DateUtils.getDateToString(newDate.getTime()).substring(0, 6);
                    tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() + commonTable.getLink()
                            + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
                    timeMap = fusion12hMapper.selectNewestTime(tableName);
                }
            }
            if (timeMap == null || timeMap.size() == 0) {
                return CommonResult.failed("未查询到数据。。");
            }
            baseTime = (Long) timeMap.get("time");
            startTime = baseTime + 3600;
            endTime = startTime + 3599;
        } else {
            String timeStr = DateUtils.getDateToString(Long.parseLong(time) * 1000);
            tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() + commonTable.getLink()
                    + timeStr.substring(0, 6) + commonTable.getLink() + commonTable.getSufParse();
            Map<String, Object> timeMap = fusion12hMapper.selectNewestTime(tableName);
            baseTime = (Long) timeMap.get("time");
            startTime = Long.parseLong(time);
            endTime = startTime + 3599;
        }
        Map<String, Object> modifiedMap = fusion12hMapper.selectModifiedNewest(tableName, startTime, endTime,
                Integer.parseInt(type), Integer.parseInt(layer), 0);
        String fileName = "";
        String relativePath = "";
        if (modifiedMap != null && modifiedMap.size() > 0) {
            fileName = (String) modifiedMap.get("name");
            relativePath = (String) modifiedMap.get("pos_file");
        } else {
            Map<String, Object> unmodifiedMap = fusion12hMapper.selectUnmodifiedNewest(tableName, startTime, endTime,
                    Integer.parseInt(type), Integer.parseInt(layer), 0);
            fileName = (String) unmodifiedMap.get("name");
            relativePath = (String) unmodifiedMap.get("pos_file");

        }
        String totalFilePath = commonPath.getDisk() + commonPath.getCommonPath() + relativePath + fileName;
        // 读取对应的数据内容
        String content = "";
        try {
            content = FileUtils.readCharByPath(totalFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", JSONUtil.parseObj(content));
        resultMap.put("basetime", baseTime);
        return CommonResult.success(resultMap);
    }
     */

}
