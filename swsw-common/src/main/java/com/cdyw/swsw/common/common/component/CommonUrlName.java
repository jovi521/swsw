package com.cdyw.swsw.common.common.component;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.domain.ao.enums.CommonParamKeyEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.dto.cimiss.CimissResult;
import com.cdyw.swsw.common.domain.dto.ds.CimissDsResult;
import com.cdyw.swsw.common.domain.entity.atstation.Atstation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cimiss获取访问URL、解析URL结果类
 *
 * @author jovi
 */
@Scope("prototype")
@Component
public class CommonUrlName {

    private final CommonUrlParam commonUrlParam;

    private final Map<String, Object> paramMap = new HashMap<>(64);

    @Autowired
    public CommonUrlName(CommonUrlParam commonUrlParam) {
        this.commonUrlParam = commonUrlParam;
    }

    /**
     * 根据 OKHttp 获取的结果map集，解析成最终实体类
     *
     * @param resultMap 结果集
     */
    public List<Object> parseMap2Object(TypeEnum typeEnum, Map<String, Object> resultMap) {
        CimissResult cimissResult = BeanUtil.mapToBean(resultMap, CimissResult.class, true, CopyOptions.create());
        List<Map<String, Object>> dsList = cimissResult.getDS();
        List<Object> dsObjList = new ArrayList<>();
        // ① 判断是否存在 DS
        if (dsList != null) {
            // ② 判断 DS 是否存在值
            if (dsList.size() != 0) {
                for (Map<String, Object> dsMap : dsList) {
                    switch (typeEnum) {
                        case TYPE_AT_STATION:
                            Atstation atstation = BeanUtil.mapToBean(dsMap, Atstation.class, true, CopyOptions.create().ignoreCase());
                            dsObjList.add(atstation);
                            continue;
                        case TYPE_SATELLITE:
                        case TYPE_RADAR_WEATHER:
                        case TYPE_RADAR_WIND_PROFILE:
                        case TYPE_CLDAS_1KM:
                        case TYPE_GRAPES_3KM:
                            CimissDsResult cimissDsResult = BeanUtil.mapToBean(dsMap, CimissDsResult.class, true, CopyOptions.create().ignoreCase());
                            dsObjList.add(cimissDsResult);
                            continue;
                        default:
                            break;
                    }
                }
            }
        }
        return dsObjList;
    }

    /**
     * 根据指定参数（一级大类、二级站点、三级产品）获取URL参数
     *
     * @param typeEnum 一级大类
     * @param staId    二级站点
     * @param type     三级产品
     * @return URL参数
     */
    public Map<String, Object> getUrlParamMap(TypeEnum typeEnum, String staId, String type) {
        paramMap.put(CommonParamKeyEnum.PARAM_KEY_TIME_RANGE.getParamKey(), getTimeRange());
        paramMap.put(CommonParamKeyEnum.PARAM_KEY_DATA_CODE.getParamKey(), type);
        // 目前暂定从 Cimiss 通过URL获取数据都必须要加站点
        if (staId != null) {
            paramMap.put(CommonParamKeyEnum.PARAM_KEY_STAT_IDS.getParamKey(), staId);
        }
        switch (typeEnum) {
            case TYPE_RADAR_WEATHER:
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_INTERFACE_ID.getParamKey(), commonUrlParam.getInterfaceIdRadWeaGetRada());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_ELEMENTS.getParamKey(), commonUrlParam.getElementsRadarWeather());
                break;
            case TYPE_RADAR_WIND_PROFILE:
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_INTERFACE_ID.getParamKey(), commonUrlParam.getInterfaceIdRadWinGetRada());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_ELEMENTS.getParamKey(), commonUrlParam.getElementsRadarWindProfile());
                break;
            case TYPE_AT_STATION:
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_INTERFACE_ID.getParamKey(), commonUrlParam.getInterfaceIdAtsGetSurf());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_ELEMENTS.getParamKey(), commonUrlParam.getElementsAtstation());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_ADMIN_CODES.getParamKey(), commonUrlParam.getAdminCodes());
                break;
            case TYPE_SATELLITE:
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_INTERFACE_ID.getParamKey(), commonUrlParam.getInterfaceIdSatGetSate());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_ELEMENTS.getParamKey(), commonUrlParam.getElementsSatellite());
                break;
            case TYPE_CLDAS_1KM:
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_INTERFACE_ID.getParamKey(), commonUrlParam.getInterfaceIdCldas1kmGetNafp());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_ELEMENTS.getParamKey(), commonUrlParam.getElementsCldas1km());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_FCST_ELES.getParamKey(), commonUrlParam.getFcstEles());
                break;
            case TYPE_GRAPES_3KM:
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_INTERFACE_ID.getParamKey(), commonUrlParam.getInterfaceIdGrapes3kmGetSevp());
                paramMap.put(CommonParamKeyEnum.PARAM_KEY_ELEMENTS.getParamKey(), commonUrlParam.getElementsGrapes3km());
                break;
            default:
                break;
        }
        return paramMap;
    }

    /**
     * 获取当前时间的前一小时（暂定）
     *
     * @return Time
     */
    private String getTimeRange() {
        String now15minusFormat = DateUtil.format(LocalDateTime.now().minusHours(10), "yyyyMMddHHmmss");
        String nowFormat = DateUtil.format(LocalDateTime.now(), "yyyyMMddHHmmss");
        return "[" + now15minusFormat + "," + nowFormat + "]";
    }
}
