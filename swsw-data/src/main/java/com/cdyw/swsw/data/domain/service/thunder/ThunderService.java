package com.cdyw.swsw.data.domain.service.thunder;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.thunder.Thunder;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.dao.thunder.ThunderMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jovi
 */
@Slf4j
@Service
public class ThunderService {

    private final ThunderMapper thunderMapper;

    private final CommonUrlParam commonUrlParam;

    private final CommonPath commonPath;

    private final CommonTableName commonTableName;

    private final CommonTable commonTable;

    @Autowired
    public ThunderService(ThunderMapper thunderMapper, CommonUrlParam commonUrlParam, CommonPath commonPath, CommonTableName commonTableName, CommonTable commonTable) {
        this.thunderMapper = thunderMapper;
        this.commonUrlParam = commonUrlParam;
        this.commonPath = commonPath;
        this.commonTableName = commonTableName;
        this.commonTable = commonTable;
    }

    public CommonResult<?> getUparLightEleInRectByTimeRange(String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String eleValueRanges, String minLon, String maxLon, String minLat, String maxLat, String timeRange) {
        String interfaceId = commonUrlParam.getInterfaceIdThunderGetRada();
        String resultsJson = mappingUrlToResult(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, eleValueRanges, minLon, maxLon, minLat, maxLat, timeRange);
        return mappingResultToList(resultsJson);
    }

    /**
     * 根据json数据结果获取最终数据集合
     *
     * @param resultsJson String
     * @return List<Thunder>
     */
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> mappingResultToList(String resultsJson) {
        List<String> tableNameByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_THUNDER);
        // 我方数据库为：One   三方数据库为：Third
        List<Thunder> thunderListThird = new ArrayList<>();
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
                                    Thunder thunder = mappingDsToSatel(ds);
                                    thunderListThird.add(thunder);
                                });
                                List<Thunder> thunderListOne = new ArrayList<>();
                                for (Thunder thunderThird : thunderListThird) {
                                    // 根据第三方数据库的dFileId和dateTime查询我方数据库，并将查询结果存入我方数据库的list中
                                    thunderListOne.addAll(thunderMapper.getThunderByNumAndTime(tableNameByType.get(0), thunderThird.getLayerNum(), DateUtil.format(thunderThird.getDateTime(), "yyyy-MM-dd HH:mm:ss")));
                                }
                                // *****难点：两个集合去重。一个有id和createTime，另一个没有，两个都有satId和dateTime，所以不能用两个对象直接比较（按照需求：只要satId和dateTime相同，则视为同一种“数据对象”）
//                                String filePathAll = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getThunderPath() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
                                if (thunderListOne.size() == 0) {
                                    thunderListThird.forEach(thun ->
                                    {
                                        thunderMapper.insert(tableNameByType.get(0), thun);
                                        // 先录入数据库，再下载至本地指定目录下
//                                        if (FileUtil.exist(filePathAll)) {
//                                            HttpUtil.downloadFile(thun.getFileUrl(), FileUtil.file(filePathAll));
//                                        } else {
//                                            HttpUtil.downloadFile(thun.getFileUrl(), FileUtil.mkdir(filePathAll));
//                                        }
                                    });
                                } else {
                                    for (Thunder thunder : thunderListOne) {
                                        // 逆序，否则remove有可能会发生异常
                                        // 找出“交集”，并去掉
                                        thunderListThird.removeIf(thunderThird -> thunder.getLayerNum().equals(thunderThird.getLayerNum()) && thunder.getDateTime().equals(thunderThird.getDateTime()));
                                    }
                                    Optional.of(thunderListThird).ifPresent(satListThird -> satListThird.forEach(thun ->
                                    {
                                        thunderMapper.insert(tableNameByType.get(0), thun);
                                        // 先录入数据库，再下载至本地指定目录下
//                                        if (FileUtil.exist(filePathAll)) {
//                                            HttpUtil.downloadFile(thun.getFileUrl(), FileUtil.file(filePathAll));
//                                        } else {
//                                            HttpUtil.downloadFile(thun.getFileUrl(), FileUtil.mkdir(filePathAll));
//                                        }
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
        log.info(DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss") + ">>>>>执行成功了： " + thunderListThird.size() + " 条数据");
        if (thunderListThird.size() > 0) {
            return CommonResult.success(thunderListThird, UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        } else {
            assert resultMap != null;
            return CommonResult.failed(UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        }
    }

    private String mappingUrlToResult(String interfaceId, String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String eleValueRanges, String minLon, String maxLon, String minLat, String maxLat, String timeRange) {
        Map<String, Object> paramsMap = new HashMap<>(16);
        paramsMap.put("dataFormat", dataFormat);
        paramsMap.put("interfaceId", interfaceId);
        paramsMap.put("dataCode", dataCode);
        paramsMap.put("elements", elements);
        paramsMap.put("limitCnt", limitCnt);
        paramsMap.put("orderBy", orderBy);
        paramsMap.put("eleValueRanges", eleValueRanges);
        paramsMap.put("minLon", minLon);
        paramsMap.put("maxLon", maxLon);
        paramsMap.put("minLat", minLat);
        paramsMap.put("maxLat", maxLat);
        paramsMap.put("userId", commonUrlParam.getUserId1());
        paramsMap.put("pwd", commonUrlParam.getPwd1());
        paramsMap.put("timeRange", timeRange);
        String encodeUrl = URLUtil.encode(commonUrlParam.getUrl());
        return HttpUtil.get(encodeUrl, paramsMap);
    }

    /**
     * 匹配DS和SAT数据:
     * 因为是动态匹配，所以必须处理好空指针异常
     *
     * @param ds Map
     * @return thunder
     */
    private Thunder mappingDsToSatel(Map<String, Object> ds) {
        Thunder thunder = new Thunder();
        thunder.setDateTime(DateUtil.parseLocalDateTime(mappingStringByKey(ds, "DATETIME")));
        thunder.setLon(mappingFloatByStr(mappingStringByKey(ds, "LON")));
        thunder.setLat(mappingFloatByStr(mappingStringByKey(ds, "LAT")));
        thunder.setMSecond(mappingStringByKey(ds, "MSECOND"));
        thunder.setLayerNum(mappingIntegerByStr(mappingStringByKey(ds, "LAYER_NUM")));
        thunder.setMars3(mappingFloatByStr(mappingStringByKey(ds, "MARS_3")));
        thunder.setPoisErr(mappingFloatByStr(mappingStringByKey(ds, "POIS_ERR")));
        thunder.setPoisType(mappingIntegerByStr(mappingStringByKey(ds, "POIS_TYPE")));
        thunder.setLitCurrent(mappingFloatByStr(mappingStringByKey(ds, "LIT_CURRENT")));
        thunder.setLitProv(mappingStringByKey(ds, "LIT_PROV"));
        thunder.setLitCity(mappingStringByKey(ds, "LIT_CITY"));
        thunder.setLitCnty(mappingStringByKey(ds, "LIT_CNTY"));
        return thunder;
    }

    /**
     * @param ds  Map
     * @param key String
     * @return String
     */
    private String mappingStringByKey(Map<String, Object> ds, String key) {
        return Optional.ofNullable(ds.get(key)).map(Object::toString).orElse(null);
    }

    /**
     * @param str String
     * @return String
     */
    private Float mappingFloatByStr(String str) {
        return Optional.ofNullable(str).map(Float::valueOf).orElse(null);
    }

    /**
     * @param str String
     * @return String
     */
    private Integer mappingIntegerByStr(String str) {
        return Optional.ofNullable(str).map(Integer::valueOf).orElse(null);
    }
}
