package com.cdyw.swsw.common.common.component;

import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.DataProductTypeEnum;
import com.cdyw.swsw.common.domain.ao.enums.FileStatusEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.cdyw.swsw.common.domain.ao.enums.TypeEnum.*;

/**
 * 动态获取文件路径名的公共类
 * 文件路径名组成： 公共路径、一级大类、base/parse后缀、二级站点、文件时间、三级产品
 *
 * @author jovi
 */
@Component
public class CommonPathName {

    private final CommonPath commonPath;

    private final CommonTable commonTable;

    @Autowired
    public CommonPathName(CommonPath commonPath, CommonTable commonTable) {
        this.commonPath = commonPath;
        this.commonTable = commonTable;
    }

    /**
     * 文件路径通用部分：D盘 + data
     */
    private String getPathCommon() {
        return commonPath.getDisk() + commonPath.getCommonPath();
    }

    /**
     * 文件路径通用部分：D盘
     */
    private String getMonitorPathCommon() {
        return commonPath.getDisk();
    }

    /**
     * 文件路径日期通用部分： yyyyMMdd
     */
    private String getPathDate() {
        return "/" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/";
    }

    /**
     * 文件路径指定时间部分： yyyyMMdd 不加8小时
     */
    private String getPathNameDate(String time) {
        return "/" + LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).plusHours(0).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/";
    }

    /**
     * 文件路径指定时间部分： yyyyMMdd 切记此处是国际时间必须要加8小时
     */
    private String getPathNameDateAdd8H(String time) {
        return "/" + LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).plusHours(8).format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "/";
    }

    /**
     * 根据指定条件获取指定文件夹路径名
     *
     * @param typeEnum 一级大类
     * @param staIdC   二级站点：实际名字，不用code
     * @param type     三级产品：实际名字，不用code
     * @param time     文件时间 yyyyMMddHHmmss(UTC)
     * @param t        路径类型：base、parse
     * @return 路径名
     */
    public String getPathNameByPara(TypeEnum typeEnum, String staIdC, String type, String time, String t) {
        String pathName = null;
        switch (typeEnum) {
            case TYPE_AT_STATION:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getAtstationPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getAtstationParsePath();
                }
                break;
            case TYPE_SATELLITE:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getSatellitePath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getSatelliteParsePath();
                }
                break;
            case TYPE_RADAR_WEATHER:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getRadarWeaPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getRadarWeaParsePath();
                }
                break;
            case TYPE_RADAR_WIND_PROFILE:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getRadarWindProfilePath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getRadarWindProfileParsePath();
                }
                break;
            case TYPE_RADAR_PHASED_ARRAY:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getRadarPhaArrPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getRadarPhaArrParsePath();
                }
                break;
            case TYPE_RADAR_EXT:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getRadarExtPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getRadarExtParsePath();
                }
                break;
            case TYPE_3D_GRID_FUSION:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getD3GridFusionPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getD3GridFusionParsePath();
                }
                break;
            case TYPE_THUNDER:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getThunderPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getThunderParsePath();
                }
                break;
            case TYPE_SHORT_FORECAST:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getShortForecastPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getShortForecastParsePath();
                }
                break;
            case TYPE_CLDAS_1KM:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getCldas1kmPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getCldas1kmParsePath();
                }
                break;
            case TYPE_ECMWF_HR:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getEcmwfHrPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getEcmwfHrParsePath();
                }
                break;
            case TYPE_GRAPES_3KM:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getGrapes3kmPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getGrapes3kmParsePath();
                }
                break;
            case TYPE_SWC_WARM:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getSWCWARMPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getSWCWARMParsePath();
                }
                break;
            case TYPE_SWC_WARR:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getSWCWARRPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getPathCommon() + commonPath.getSWCWARRParsePath();
                }
                break;
            case TYPE_FUSION_2H:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getFusion2hPath();
                } else {
                    pathName = getPathCommon() + commonPath.getFusion2hParsePath();
                }
                break;
            case TYPE_FUSION_12H:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getPathCommon() + commonPath.getFusion12hPath();
                } else {
                    pathName = getPathCommon() + commonPath.getFusion12hParsePath();
                }
                break;
            default:
                break;
        }
        if (typeEnum == TYPE_RADAR_WEATHER || typeEnum == TYPE_RADAR_PHASED_ARRAY || typeEnum == TYPE_RADAR_WIND_PROFILE){
            pathName += "/" + (staIdC == null ? "" : staIdC + "/") + time.substring(0, 8) + "/" + (type == null ? "" : type) + "/";
        }else {
            pathName += "/" + (staIdC == null ? "" : staIdC + "/") + getPathNameDate(time) + "/" + (type == null ? "" : type) + "/";
        }
        return pathName;
    }

    /**
     * 根据指定条件获取指定监控文件夹路径名
     *
     * @param typeEnum 一级大类
     * @param staIdC   二级站点：实际名字，不用code
     * @param type     三级产品：实际名字，不用code
     * @param time     文件时间
     * @param t        路径类型：base、parse
     * @return 路径名
     */
    public String getMonitorPathNameByPara(TypeEnum typeEnum, String staIdC, String type, String time, String t) {
        String pathName = null;
        switch (typeEnum) {
            case TYPE_AT_STATION:
                break;
            case TYPE_SATELLITE:
                break;
            case TYPE_RADAR_WEATHER:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getRadarWeatherMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getRadarWeatherMonitorPath();
                }
                break;
            case TYPE_RADAR_WIND_PROFILE:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getRadarWindProfileMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getRadarWindProfileMonitorPath();
                }
                break;
            case TYPE_RADAR_PHASED_ARRAY:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getRadarPhasedArrayMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getRadarPhasedArrayMonitorPath();
                }
                break;
            case TYPE_RADAR_EXT:
                break;
            case TYPE_3D_GRID_FUSION:
                break;
            case TYPE_THUNDER:
                break;
            case TYPE_SHORT_FORECAST:
                break;
            case TYPE_CLDAS_1KM:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getCldas1kmMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getCldas1kmMonitorPath();
                }
                break;
            case TYPE_ECMWF_HR:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getEcmwfHrMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getEcmwfHrMonitorPath();
                }
                break;
            case TYPE_GRAPES_3KM:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getGrapes3kmMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getGrapes3kmMonitorPath();
                }
                break;
            case TYPE_SWC_WARM:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getSwcWarmMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getSwcWarmMonitorPath();
                }
                break;
            case TYPE_SWC_WARR:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getSwcWarrMonitorPath();
                } else if (t.contains(commonTable.getSufParse())) {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getSwcWarrMonitorPath();
                }
                break;
            case TYPE_FUSION_2H:
                break;
            case TYPE_FUSION_12H:
                if (t.contains(commonTable.getSufBase())) {
                    pathName = getMonitorPathCommon() + commonPath.getBaseMonitorPath() + commonPath.getFusion12hMonitorPath();
                } else {
                    pathName = getMonitorPathCommon() + commonPath.getParseMonitorPath() + commonPath.getFusion12hMonitorPath();
                }
                break;
            default:
                break;
        }
        pathName += "/" + (staIdC == null ? "" : staIdC + "/") + getPathNameDate(time) + "/" + (type == null ? "" : type) + "/";
        return pathName;
    }

    /**
     * 根据 产品类型、发布时间、文件状态 查询产品保存路径
     *
     * @param dataProductTypeEnum 产品类型
     * @param publishTime         发布时间 yyyy-MM-dd HH:mm:ss
     * @param fileStatusEnum      文件状态
     * @return path
     */
    public String getDataProductPathByPara(DataProductTypeEnum dataProductTypeEnum, Long publishTime, FileStatusEnum fileStatusEnum) {
        String dataProductPath;
        String time = "/" + DateUtils.getDayStringByTimestamp(publishTime, "yyyyMMdd") + "/";
        String status;
        switch (fileStatusEnum) {
            case FILE_STATUS_DEFAULT:
                status = commonPath.getDataProductDefaultPath();
                break;
            case FILE_STATUS_TEMP:
                status = commonPath.getDataProductTempPath();
                break;
            default:
                status = commonPath.getDataProductFinalPath();
                break;
        }
        String commonPathPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getDataProductPath() + time;
        switch (dataProductTypeEnum) {
            case PRODUCT_TYPE_SHORT_FORECAST:
                dataProductPath = commonPathPath + commonPath.getDataProductShortForecast();
                break;
            case PRODUCT_TYPE_GRID_FORECAST:
                dataProductPath = commonPathPath + commonPath.getDataProductGridForecast();
                break;
            case PRODUCT_TYPE_LINE_FORECAST:
                dataProductPath = commonPathPath + commonPath.getDataProductLineForecast();
                break;
            case PRODUCT_TYPE_STADIUM_WARNING:
                dataProductPath = commonPathPath + commonPath.getDataProductStadiumWarning();
                break;
            case PRODUCT_TYPE_DISASTER_FORECAST:
                dataProductPath = commonPathPath + commonPath.getDataProductDisasterForecast();
                break;
            case PRODUCT_TYPE_IRREGULAR_SHORT:
                dataProductPath = commonPathPath + commonPath.getDataProductIrregularShort();
                break;
            default:
                dataProductPath = commonPathPath + commonPath.getDataProductWarningSignal();
                break;
        }
        dataProductPath += status;
        return dataProductPath;
    }

    /**
     * 获取模板路径
     *
     * @return path
     */
    public String getDataProductTemplatePathByPara() {
        return commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getDataProductTemplatePath();
    }
}
