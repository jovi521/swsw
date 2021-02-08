package com.cdyw.swsw.common.common.component;

import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 动态获取表名（base、parse、log）的公共类
 * 表名组成： 一级大类、二级站点、文件时间、base/parse后缀
 *
 * @author jovi
 */
@Component
public class CommonTableName {

    private final CommonTable commonTable;

    @Autowired
    public CommonTableName(CommonTable commonTable) {
        this.commonTable = commonTable;
    }

    /**
     * 动态获取表名公共方法：主要用来动态创建指定时间月份的所有表(暂时禁用)
     *
     * @param type TypeEnum
     * @param time String
     * @return String
     */
    @Deprecated
    public List<String> getTableNameByTypeAndTime(TypeEnum type, String time) {
        String yyyyMM = DateUtil.format(LocalDateTime.ofEpochSecond(Long.parseLong(time), 0, ZoneOffset.ofHours(8)), "yyyyMM");
        return getTableNamesByType(type);
    }

    /**
     * 直接传LDT格式
     *
     * @param type          TypeEnum
     * @param localDateTime LocalDateTime
     * @return String
     */
    public String getTableNameByTypeAndStaAndLocal(TypeEnum type, String staIdC, LocalDateTime localDateTime) {
        String yyyyMM = DateUtil.format(localDateTime, "yyyyMM");
        return getTableNamesByType(type) + commonTable.getSufBase();
    }

    /**
     * mysql：表名长度不能超过64个字符，此处设置为50
     * 动态获取解析表名公共方法：主要用来动态创建当前月的解析表
     *
     * @param type TypeEnum
     * @return String
     */
    @Scope("prototype")
    public String getTableParseNameByType(TypeEnum type, String staIdC) {
        return getTableNamesByType(type) + commonTable.getSufParse();
    }

    /**
     * mysql：表名长度不能超过64个字符，此处设置为50
     * 动态获取解析表名公共方法：主要用来动态创建实时传的时间月份的解析表
     *
     * @param type TypeEnum
     * @param time String
     * @return String
     */
    public String getTableParseNameByTypeAndTime(TypeEnum type, String staIdC, String time) {
        String yyyyMM = DateUtil.format(LocalDateTime.ofEpochSecond(Long.parseLong(time), 0, ZoneOffset.ofHours(8)), "yyyyMM");
        return getTableNamesByType(type) + commonTable.getSufParse();
    }

    /**
     * 日志表通用部分： log_
     */
    private String getTableNameCommonLog() {
        return commonTable.getLog() + commonTable.getLink();
    }

    /**
     * 数据表通用部分：data_
     */
    private String getTableNameCommonPre() {
        return commonTable.getPre() + commonTable.getLink();
    }

    /**
     * 数据表通用部分： _yyyyMM
     */
    private String getTableNameCommonDate() {
        return commonTable.getLink() + DateUtil.format(LocalDateTime.now(), "yyyyMM");
    }

    /**
     * 数据表指定时间部分： _yyyyMM
     *
     * @param time yyyyMMddHHmmss
     */
    private String getTableNameDate(String time) {
        return commonTable.getLink() + LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).format(DateTimeFormatter.ofPattern("yyyyMM"));
    }

    /**
     * 数据表通用部分：_base
     */
    private String getTableNameCommonSufBase() {
        return commonTable.getLink() + commonTable.getSufBase();
    }

    /**
     * 数据表通用部分：_parse
     */
    private String getTableNameCommonSufParse() {
        return commonTable.getLink() + commonTable.getSufParse();
    }

    /**
     * 数据表自定义部分：一级大类
     */
    private String getTableNameType(TypeEnum type) {
        switch (type) {
            case TYPE_AT_STATION:
                return commonTable.getTypeAtStation();
            case TYPE_SATELLITE:
                return commonTable.getTypeSatellite();
            case TYPE_RADAR_WEATHER:
                return commonTable.getTypeRadarWeather();
            case TYPE_RADAR_WIND_PROFILE:
                return commonTable.getTypeRadarWindProfile();
            case TYPE_RADAR_PHASED_ARRAY:
                return commonTable.getTypeRadarPhasedArray();
            case TYPE_RADAR_EXT:
                return commonTable.getTypeRadarExt();
            case TYPE_3D_GRID_FUSION:
                return commonTable.getType3dGridFusion();
            case TYPE_THUNDER:
                return commonTable.getTypeThunder();
            case TYPE_SHORT_FORECAST:
                return commonTable.getTypeShortForecast();
            case TYPE_CLDAS_1KM:
                return commonTable.getTypeCldas1km();
            case TYPE_ECMWF_HR:
                return commonTable.getTypeEcmwfHr();
            case TYPE_GRAPES_3KM:
                return commonTable.getTypeGrapes3km();
            case TYPE_SWC_WARM:
                return commonTable.getTypeSwcWarm();
            case TYPE_SWC_WARR:
                return commonTable.getTypeSwcWarr();
            case TYPE_FUSION_2H:
                return commonTable.getTypeFusion2h();
            case TYPE_FUSION_12H:
                return commonTable.getTypeFusion12h();
            case TYPE_WEATHER_RADAR_EXTRAPOLATION:
                return commonTable.getTypeWeatherRadarExtrapolation();
            case TYPE_PHASEDARRAY_RADAR_EXTRAPOLATION:
                return commonTable.getTypePhasedarrayRadarExtrapolation();
            default:
                break;
        }
        return null;
    }

    /**
     * 数据表自定义部分：二级站点（允许为空，因为有些数据没有站点，如果没有则省略）
     */
    private List<String> getTableNameSta(TypeEnum type) {
        List<String> tableNameStas = new ArrayList<>(10);
        String link = commonTable.getLink();
        switch (type) {
            case TYPE_AT_STATION:
                tableNameStas.add(link + commonTable.getStaAtStation());
                break;
            case TYPE_SATELLITE:
                tableNameStas.add(link + commonTable.getStaSatelliteFy4a());
                break;
            case TYPE_RADAR_WEATHER:
                tableNameStas.add(link + commonTable.getStaRadarWeather());
                break;
            case TYPE_RADAR_WIND_PROFILE:
                tableNameStas.add(link + commonTable.getStaRadarWindprofile56189());
                tableNameStas.add(link + commonTable.getStaRadarWindprofile56272());
                tableNameStas.add(link + commonTable.getStaRadarWindprofile56276());
                tableNameStas.add(link + commonTable.getStaRadarWindprofile56285());
                tableNameStas.add(link + commonTable.getStaRadarWindprofile56286());
                tableNameStas.add(link + commonTable.getStaRadarWindprofile56290());
                tableNameStas.add(link + commonTable.getStaRadarWindprofile56296());
                break;
            case TYPE_RADAR_PHASED_ARRAY:
                tableNameStas.add(link + commonTable.getStaRadarPhasedArray1());
                tableNameStas.add(link + commonTable.getStaRadarPhasedArray2());
                tableNameStas.add(link + commonTable.getStaRadarPhasedArray3());
                tableNameStas.add(link + commonTable.getStaRadarPhasedArray4());
                tableNameStas.add(link + commonTable.getStaRadarPhasedArray());
                tableNameStas.add(link + commonTable.getStaRadarPhasedArrayChongzhou());
                tableNameStas.add(link + commonTable.getStaRadarPhasedArrayTianfu());
                tableNameStas.add(link + commonTable.getStaRadarPhasedArrayXindu());
                break;
            case TYPE_RADAR_EXT:
                tableNameStas.add(link + commonTable.getStaRadarExt());
                break;
            case TYPE_3D_GRID_FUSION:
                tableNameStas.add(link + commonTable.getSta3dGridFusion());
                break;
            case TYPE_THUNDER:
                tableNameStas.add(link + commonTable.getStaThunder());
                break;
            case TYPE_SHORT_FORECAST:
                tableNameStas.add(link + commonTable.getStaShortForecast());
                break;
            default:
                break;
        }
        return tableNameStas;
    }

    /**
     * 所有数据表最终名字：base、parse、log
     */
    public List<String> getTableNamesByType(TypeEnum typeEnum) {
        List<String> tableNames = new ArrayList<>(10);
        String tableNameType = getTableNameType(typeEnum);
        List<String> tableNameStas = getTableNameSta(typeEnum);
        if (Objects.nonNull(tableNameType)) {
            if (!tableNameStas.isEmpty()) {
                for (String tableNameSta : tableNameStas) {
                    tableNames.add(getTableNameCommonPre() + tableNameType + tableNameSta + getTableNameCommonDate() + getTableNameCommonSufBase());
                    tableNames.add(getTableNameCommonPre() + tableNameType + tableNameSta + getTableNameCommonDate() + getTableNameCommonSufParse());
                }
            } else {
                tableNames.add(getTableNameCommonPre() + tableNameType + getTableNameCommonDate() + getTableNameCommonSufBase());
                tableNames.add(getTableNameCommonPre() + tableNameType + getTableNameCommonDate() + getTableNameCommonSufParse());
            }
            tableNames.add(getTableNameCommonLog() + tableNameType + getTableNameCommonDate());
        }
        return tableNames;
    }

    /**
     * 指定数据表最终名字
     *
     * @param typeEnum 一级大类
     * @param staIdC   二级站点：必须用实际名字，不用code
     * @param time     yyyyMMddHHmmss：必须是本地时间，不能取UTC
     * @param t        表类型：base、parse、log
     * @return 表名
     */
    public String getTableNameByParam(TypeEnum typeEnum, String staIdC, String time, String t) {
        String tableName = null;
        if (staIdC != null) {
            if (t.contains(commonTable.getSufBase())) {
                tableName = getTableNameCommonPre() + getTableNameType(typeEnum) + commonTable.getLink() + staIdC.toLowerCase().replace("/", "") + getTableNameDate(time) + getTableNameCommonSufBase();
            } else if (t.contains(commonTable.getSufParse())) {
                tableName = getTableNameCommonPre() + getTableNameType(typeEnum) + commonTable.getLink() + staIdC.toLowerCase().replace("/", "") + getTableNameDate(time) + getTableNameCommonSufParse();
            }
        } else {
            if (t.contains(commonTable.getSufBase())) {
                tableName = getTableNameCommonPre() + getTableNameType(typeEnum) + getTableNameDate(time) + getTableNameCommonSufBase();
            } else if (t.contains(commonTable.getSufParse())) {
                tableName = getTableNameCommonPre() + getTableNameType(typeEnum) + getTableNameDate(time) + getTableNameCommonSufParse();
            } else {
                tableName = getTableNameCommonLog() + getTableNameType(typeEnum) + getTableNameDate(time);
            }
        }
        return tableName;
    }
}
