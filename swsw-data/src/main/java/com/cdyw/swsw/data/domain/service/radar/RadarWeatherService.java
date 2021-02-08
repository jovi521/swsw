package com.cdyw.swsw.data.domain.service.radar;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.radar.RadarWeather;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.common.component.CommonFileName;
import com.cdyw.swsw.data.common.component.CommonRadarParseName;
import com.cdyw.swsw.data.domain.dao.radar.RadarWeatherMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jovi
 */
@Slf4j
@Service
public class RadarWeatherService {

    private final TypeEnum TYPE = TypeEnum.TYPE_RADAR_WEATHER;

    private final RadarWeatherMapper radarWeatherMapper;

    private final CommonUrlParam commonUrlParam;

    private final CommonTableName commonTableName;

    private final CommonRadarParseName commonRadarParseName;

    private final CommonPath commonPath;

    private final CommonFileName commonFileName;

    @Autowired
    public RadarWeatherService(RadarWeatherMapper radarWeatherMapper, CommonUrlParam commonUrlParam, CommonPath commonPath, CommonTableName commonTableName, CommonRadarParseName commonRadarParseName, CommonFileName commonFileName) {
        this.radarWeatherMapper = radarWeatherMapper;
        this.commonUrlParam = commonUrlParam;
        this.commonPath = commonPath;
        this.commonTableName = commonTableName;
        this.commonRadarParseName = commonRadarParseName;
        this.commonFileName = commonFileName;
    }

    public CommonResult<?> getRadaFileByTimeRangeAndStaId(String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String staIds, String timeRange, String staTypes) {
        String interfaceId = commonUrlParam.getInterfaceIdRadWeaGetRada();
        String resultsJson = mappingUrlToResult(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, staIds, timeRange, staTypes);
        return mappingResultToList(resultsJson);
    }


    public CommonResult<?> createAndInsertRadarWeaParseByParam(int time, String insertValues, String statinIdC) {
        radarWeatherMapper.createAndInsertRadarWeaParseByParam(time, insertValues, statinIdC);
        return CommonResult.success();
    }

    /**
     * 根据json数据结果获取最终数据集合
     *
     * @param resultsJson String
     * @return List<RadarWeather>
     */
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> mappingResultToList(String resultsJson) {
        List<String> tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_RADAR_WEATHER);
        // 我方数据库为：One   三方数据库为：Third
        List<RadarWeather> radarWeatherListThird = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = new HashMap<>(16);
        try {
            resultMap = mapper.readValue(resultsJson, Map.class);
            // 避免使用ifnull,使用Optional替代
            Optional.ofNullable(resultMap)
                    .ifPresent(resultMapTemp -> {
                        AtomicInteger rowCount = new AtomicInteger();
                        Optional.ofNullable(resultMapTemp.get("rowCount"))
                                .ifPresent(i -> rowCount.set(Integer.parseInt(i.toString())));
                        if (rowCount.get() > 0) {
                            try {
                                String dsJson = mapper.writeValueAsString(resultMapTemp.get("DS"));
                                List<Map<String, Object>> dsList = mapper.readValue(dsJson, List.class);
                                dsList.forEach(ds -> {
                                    RadarWeather radarWeather = mappingDsToSatel(ds);
                                    radarWeatherListThird.add(radarWeather);
                                });
                                List<RadarWeather> radarWeatherListOne = new ArrayList<>();
                                for (RadarWeather radarWeatherThird : radarWeatherListThird) {
                                    // 根据第三方数据库的dFileId和dateTime查询我方数据库，并将查询结果存入我方数据库的list中
                                    radarWeatherListOne.addAll(radarWeatherMapper.getRadarWeaFileByDfileIdAndDate(tableNamesByType.get(0), radarWeatherThird.getDFileId(), DateUtil.format(radarWeatherThird.getDateTime(), "yyyy-MM-dd HH:mm:ss")));
                                }
                                // *****难点：两个集合去重。一个有id和createTime，另一个没有，两个都有satId和dateTime，所以不能用两个对象直接比较（按照需求：只要satId和dateTime相同，则视为同一种“数据对象”）
                                String filePathAll = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getRadarWeaPath() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
                                if (radarWeatherListOne.size() == 0) {
                                    radarWeatherListThird.forEach(radarWea ->
                                    {
                                        radarWeatherMapper.insert(tableNamesByType.get(0), radarWea);
                                        // 先录入数据库，再下载至本地指定目录下
                                        if (FileUtil.exist(filePathAll)) {
                                            HttpUtil.downloadFile(radarWea.getFileUrl(), FileUtil.file(filePathAll));
                                        } else {
                                            HttpUtil.downloadFile(radarWea.getFileUrl(), FileUtil.mkdir(filePathAll));
                                        }
                                    });
                                } else {
                                    for (RadarWeather radarWeather : radarWeatherListOne) {
                                        // 逆序，否则remove有可能会发生异常
                                        // 找出“交集”，并去掉
                                        radarWeatherListThird.removeIf(radarWeatherThird -> radarWeather.getDFileId().equals(radarWeatherThird.getDFileId()) && radarWeather.getDateTime().equals(radarWeatherThird.getDateTime()));
                                    }
                                    Optional.of(radarWeatherListThird).ifPresent(satListThird -> satListThird.forEach(radarWea ->
                                    {
                                        radarWeatherMapper.insert(tableNamesByType.get(0), radarWea);
                                        // 先录入数据库，再下载至本地指定目录下
                                        if (FileUtil.exist(filePathAll)) {
                                            HttpUtil.downloadFile(radarWea.getFileUrl(), FileUtil.file(filePathAll));
                                        } else {
                                            HttpUtil.downloadFile(radarWea.getFileUrl(), FileUtil.mkdir(filePathAll));
                                        }
                                    }));
                                }
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        log.info(DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss") + ">>>>>执行成功了： " + radarWeatherListThird.size() + " 条数据");
        if (radarWeatherListThird.size() > 0) {
            return CommonResult.success(radarWeatherListThird, UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        } else {
            assert resultMap != null;
            return CommonResult.failed(UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        }
    }

    private String mappingUrlToResult(String interfaceId, String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String staIds, String timeRange, String staTypes) {
        Map<String, Object> paramsMap = new HashMap<>(16);
        paramsMap.put("dataFormat", dataFormat);
        paramsMap.put("interfaceId", interfaceId);
        paramsMap.put("dataCode", dataCode);
        paramsMap.put("elements", elements);
        paramsMap.put("limitCnt", limitCnt);
        paramsMap.put("orderBy", orderBy);
        paramsMap.put("staIds", staIds);
        paramsMap.put("userId", commonUrlParam.getUserId1());
        paramsMap.put("pwd", commonUrlParam.getPwd1());
        paramsMap.put("timeRange", timeRange);
        paramsMap.put("staTypes", staTypes);
        String encodeUrl = URLUtil.encode(commonUrlParam.getUrl());
        return HttpUtil.get(encodeUrl, paramsMap);
    }

    /**
     * 匹配DS和SAT数据:
     * 因为是动态匹配，所以必须处理好空指针异常
     *
     * @param ds Map<String, Object>
     * @return radarWeather
     */
    private RadarWeather mappingDsToSatel(Map<String, Object> ds) {
        RadarWeather radarWeather = new RadarWeather();
        radarWeather.setFileName(mappingStringByKey(ds, "FILE_NAME"));
        radarWeather.setFormat(mappingStringByKey(ds, "FORMAT"));
        radarWeather.setFileSize(Optional.ofNullable(mappingStringByKey(ds, "FILE_SIZE")).map(Float::valueOf).orElse(null));
        radarWeather.setFileUrl(mappingStringByKey(ds, "FILE_URL"));
        radarWeather.setDateTime(DateUtil.parseLocalDateTime(mappingStringByKey(ds, "DATETIME")));
        radarWeather.setProvince(mappingStringByKey(ds, "PROVINCE"));
        radarWeather.setCity(mappingStringByKey(ds, "CITY"));
        radarWeather.setCnty(mappingStringByKey(ds, "CNTY"));
        radarWeather.setStationIdC(mappingStringByKey(ds, "STATION_ID_C"));
        radarWeather.setRadaModel(mappingStringByKey(ds, "RADA_MODEL"));
        radarWeather.setProdTypeRada(mappingStringByKey(ds, "PROD_TYPE_RADA"));
        radarWeather.setDFileSaveHierarchy(Optional.ofNullable(mappingStringByKey(ds, "D_FILE_SAVE_HIERARCHY")).map(Integer::valueOf).orElse(null));
        radarWeather.setDSourceId(mappingStringByKey(ds, "D_SOURCE_ID"));
        radarWeather.setDFileId(mappingStringByKey(ds, "D_FILE_ID"));
        radarWeather.setElev(Optional.ofNullable(mappingStringByKey(ds, "ELEV")).map(Float::valueOf).orElse(null));
        return radarWeather;
    }

    /**
     * @param ds  Map
     * @param key String
     * @return String
     */
    private String mappingStringByKey(Map<String, Object> ds, String key) {
        return Optional.ofNullable(ds.get(key)).map(Object::toString).orElse(null);
    }

    public int insertRadarWeatherBase(File fileSource) {
        return commonFileName.insertFileEntityBase(TYPE, fileSource);
    }

    public int insertRadarWeatherParse(File fileSource) {
        return commonRadarParseName.insertRadarParse(TYPE, fileSource);
    }
}
