package com.cdyw.swsw.data.domain.service.atstation;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.*;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.atstation.Atstation;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.common.http.HttpApi;
import com.cdyw.swsw.data.domain.dao.atstation.AtstationMapper;
import com.cdyw.swsw.data.domain.dao.colorvalue.ColorValueMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * @author jovi
 */
@Slf4j
@Service
public class AtstationService {

    private final TypeEnum TYPE = TypeEnum.TYPE_AT_STATION;

    private final AtstationMapper atstationMapper;

    private final ColorValueMapper colorValueMapper;

    private final CommonUrlName commonUrlName;

    private final CommonUrlParam commonUrlParam;

    private final CommonTableName commonTableName;

    private final CommonPath commonPath;

    private final CommonTable commonTable;

    private final HttpApi httpApi;

    @Autowired
    public AtstationService(AtstationMapper atstationMapper, ColorValueMapper colorValueMapper, CommonUrlName commonUrlName, CommonUrlParam commonUrlParam,
                            CommonTableName commonTableName, CommonPath commonPath, CommonTable commonTable, HttpApi httpApi) {
        this.atstationMapper = atstationMapper;
        this.colorValueMapper = colorValueMapper;
        this.commonUrlName = commonUrlName;
        this.commonUrlParam = commonUrlParam;
        this.commonTableName = commonTableName;
        this.commonPath = commonPath;
        this.commonTable = commonTable;
        this.httpApi = httpApi;
    }

    public static String excuteCMDCommand(String cmdCommand) {
        StringBuilder stringBuilder = new StringBuilder();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmdCommand);
            //Runtime rt = Runtime.getRuntime();
            //Process p = rt.exec(cmdCommand);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), "GBK"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            //int result = process.waitFor();
            //return result;
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 按时间段、地区检索地面要素数据；
     * 注意：调用第三方接口有两种方式实现-①使用springboot提供的原生的RestTemplate类的exchange、postForEntity实现get、post请求
     * ②借助第三方工具库Hutool的工具类HttpUtil实现
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> getSurfEleInRegionByTimeRange(String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String staLevels, String eleValueRanges, String adminCodes, String timeRange, String staTypes) {
        String interfaceId = "getSurfEleInRegionByTimeRange";
        String resultsJson = mappingUrlToResultByTime(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, staLevels, eleValueRanges, adminCodes, timeRange, staTypes);
        return mappingResultToList(resultsJson);
    }

    public CommonResult<?> getAtStationBase() {
        String t = commonTable.getSufBase();
        int size = 0;
        Map<String, Object> paramMap = commonUrlName.getUrlParamMap(TYPE, null, commonUrlParam.getDataCodeAtstation());
        Map<String, Object> resultMap = httpApi.getResult(paramMap, commonUrlParam.getUserId1(), commonUrlParam.getPwd1());
        List<Object> objects = commonUrlName.parseMap2Object(TYPE, resultMap);
        for (Object atstation : objects) {
            if (atstation instanceof Atstation) {
                // 此处时间获取的是UTC时间所以入库的时候需要转化一下
                LocalDateTime dateTime = ((Atstation) atstation).getDateTime();
                String time = DateUtils.getDateUtc2Local(dateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), "yyyyMMddHHmmss").format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String tableName = commonTableName.getTableNameByParam(TYPE, commonPath.getAtstationStaAll(), time, t);
                // 新增的时候切记去重：根据 dateTime 和 stationIdC 判断
                // 这里在将datetime时间类型转换为时间戳
                ((Atstation) atstation).setTimestamp(DateUtils.getStringToDate(time) / 1000);
                size += atstationMapper.insertAtStationBase(tableName, (Atstation) atstation);
            }
        }
        if (size > 0) {
            // log.info("总共生成 " + size + " 条数据。");
            CommonResult.success(objects);
        }
        return CommonResult.failed();
    }

    /**
     * 按时间段、站号段检索地面数据要素
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> getSurfEleByTimeRangeAndStaIDRange(String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String minStaId, String eleValueRanges, String maxStaId, String timeRange, String staTypes) {
        String interfaceId = "getSurfEleByTimeRangeAndStaIDRange";
        String resultsJson = mappingUrlToResultByTimeAndStaId(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, minStaId, eleValueRanges, maxStaId, timeRange, staTypes);
        return mappingResultToList(resultsJson);
    }

    /**
     * 按时间段检索地面数据要素
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> getSurfEleByTimeRange(String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String staLevels, String eleValueRanges, String adminCodes, String timeRange, String staTypes) {
        String interfaceId = "getSurfEleByTimeRange";
        String resultsJson = mappingUrlToResultByTime(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, staLevels, eleValueRanges, adminCodes, timeRange, staTypes);
        return mappingResultToList(resultsJson);
    }

    /**
     * 调用存储过程新增自动站表
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAtStationTableByName() {
        atstationMapper.createAtStationTableByName();
    }

    /**
     * 根据json数据结果获取最终数据集合
     *
     * @param resultsJson String
     * @return List<Atstation>
     */
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> mappingResultToList(String resultsJson) {
        List<String> tableNameByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_AT_STATION);
        // 我方数据库为：One   三方数据库为：Third
        List<Atstation> atstationListThird = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> resultMap = new HashMap<>(16);
        try {
            resultMap = mapper.readValue(resultsJson, Map.class);
            // 避免使用ifnull,使用Optional替代
            boolean present = Optional.ofNullable(resultMap).isPresent();
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
                                    Atstation atstation = mappingDsToAts(ds);
                                    atstationListThird.add(atstation);
                                    createContourf(atstation);
                                });
                                List<Atstation> atstationListOne = new ArrayList<>();
                                for (Atstation atstationThird : atstationListThird) {
                                    // 根据第三方数据库的atsId和dateTime查询我方数据库，并将查询结果存入我方数据库的list中
                                    Atstation atstationOne = atstationMapper.getAtsByAtsIdAndDate(tableNameByType.get(0), atstationThird.getStationIdD(), DateUtil.format(atstationThird.getDateTime(), "yyyy-MM-dd HH:mm:ss"));
                                    Optional.ofNullable(atstationOne).map((Function<Atstation, Object>) atstationListOne::add);
                                }
                                // *****难点：两个集合去重。一个有id和createTime，另一个没有，两个都有atsId和dateTime，所以不能用两个对象直接比较（按照需求：只要atsId和dateTime相同，则视为同一种“数据对象”）
                                if (atstationListOne.size() == 0) {
                                    atstationListThird.forEach(atstationThird -> atstationMapper.insertAtStationBase(tableNameByType.get(0), atstationThird));
                                } else {
                                    for (Atstation atstation : atstationListOne) {
                                        // 逆序，否则remove有可能会发生异常
                                        // 找出“交集”，并去掉
                                        atstationListThird.removeIf(atstationThird -> atstation.getStationIdD().equals(atstationThird.getStationIdD()) && atstation.getDateTime().equals(atstationThird.getDateTime()));
                                    }
                                    Optional.of(atstationListThird).ifPresent(atsListThird -> atsListThird.forEach((Atstation atstation) -> {
                                        atstationMapper.insertAtStationBase(tableNameByType.get(0), atstation);
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
        // log.info(DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss") + ">>>>>执行成功了： " + atstationListThird.size() + " 条数据");
        if (atstationListThird.size() > 0) {
            return CommonResult.success(atstationListThird, UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        } else {
            assert resultMap != null;
            return CommonResult.failed(UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        }
    }

    /**
     * 根据所传动态参数组装URL，并获取数据结果
     *
     * @param interfaceId    String
     * @param dataFormat     String
     * @param dataCode       String
     * @param elements       String
     * @param limitCnt       String
     * @param orderBy        String
     * @param staLevels      String
     * @param eleValueRanges String
     * @param adminCodes     String
     * @param timeRange      String
     * @param staTypes       String
     * @return String
     */
    private String mappingUrlToResultByTime(String interfaceId, String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String staLevels, String eleValueRanges, String adminCodes, String timeRange, String staTypes) {
        // 将第三方接口url中出现的固定不变的键值对提取出来
        Map<String, Object> paramsMap = mappingUrlToResult(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, eleValueRanges, timeRange, staTypes);
        paramsMap.put("staLevels", staLevels);
        paramsMap.put("adminCodes", adminCodes);
        String encodeUrl = URLUtil.encode(commonUrlParam.getUrl());
        return HttpUtil.get(encodeUrl, paramsMap);
    }

    /**
     * 根据所传动态参数组装URL，并获取数据结果
     *
     * @param interfaceId    String
     * @param dataFormat     String
     * @param dataCode       String
     * @param elements       String
     * @param limitCnt       String
     * @param orderBy        String
     * @param minStaId       String
     * @param eleValueRanges String
     * @param maxStaId       String
     * @param timeRange      String
     * @param staTypes       String
     * @return String
     */
    private String mappingUrlToResultByTimeAndStaId(String interfaceId, String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String minStaId, String eleValueRanges, String maxStaId, String timeRange, String staTypes) {
        // 将第三方接口url中出现的固定不变的键值对提取出来
        Map<String, Object> paramsMap = mappingUrlToResult(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, eleValueRanges, timeRange, staTypes);
        paramsMap.put("minStaId", minStaId);
        paramsMap.put("maxStaId", maxStaId);
        String encodeUrl = URLUtil.encode(commonUrlParam.getUrl());
        return HttpUtil.get(encodeUrl, paramsMap);
    }

    private Map<String, Object> mappingUrlToResult(String interfaceId, String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String eleValueRanges, String timeRange, String staTypes) {
        Map<String, Object> paramsMap = new HashMap<>(16);
        paramsMap.put("dataFormat", dataFormat);
        paramsMap.put("interfaceId", interfaceId);
        paramsMap.put("dataCode", dataCode);
        paramsMap.put("elements", elements);
        paramsMap.put("limitCnt", limitCnt);
        paramsMap.put("orderBy", orderBy);
        paramsMap.put("userId", commonUrlParam.getUserId1());
        paramsMap.put("pwd", commonUrlParam.getPwd1());
        paramsMap.put("eleValueRanges", eleValueRanges);
        paramsMap.put("timeRange", timeRange);
        paramsMap.put("staTypes", staTypes);
        return paramsMap;
    }

    /**
     * 匹配DS和ATS数据:
     * 因为是动态匹配，所以必须处理好空指针异常
     *
     * @param ds Map<String, Object>
     * @return Atstation
     */
    private Atstation mappingDsToAts(Map<String, Object> ds) {
        Atstation atstation = new Atstation();
        atstation.setStationName(mappingStringByKey(ds, "STATION_NAME"));
        atstation.setStationIdC(mappingStringByKey(ds, "STATION_ID_C"));
        atstation.setStationIdD(mappingIntegerByStr(mappingStringByKey(ds, "STATION_ID_D")));
        atstation.setProvince(mappingStringByKey(ds, "PROVINCE"));
        atstation.setCity(mappingStringByKey(ds, "CITY"));
        atstation.setCnty(mappingStringByKey(ds, "CNTY"));
        atstation.setDateTime(DateUtil.parseLocalDateTime(mappingStringByKey(ds, "DATETIME")));
        atstation.setAdminCodeChn(mappingIntegerByStr(mappingStringByKey(ds, "ADMIN_CODE_CHN")));
        atstation.setLon(mappingFloatByStr(mappingStringByKey(ds, "LON")));
        atstation.setLat(mappingFloatByStr(mappingStringByKey(ds, "LAT")));
        atstation.setAlti(mappingFloatByStr(mappingStringByKey(ds, "ALTI")));
        atstation.setStationLevl(mappingIntegerByStr(mappingStringByKey(ds, "STATION_LEVL")));
        atstation.setGst(mappingFloatByStr(mappingStringByKey(ds, "GST")));
        atstation.setPre(mappingFloatByStr(mappingStringByKey(ds, "PRE")));
        atstation.setPre1h(mappingFloatByStr(mappingStringByKey(ds, "PRE_1H")));
        // TODO 暂时不确定10min降水量如何获取值
        atstation.setPre10min(mappingFloatByStr(mappingStringByKey(ds, "PRE_1H")));
        atstation.setPrs(mappingFloatByStr(mappingStringByKey(ds, "PRS")));
        atstation.setRhu(mappingFloatByStr(mappingStringByKey(ds, "RHU")));
        atstation.setTem(mappingFloatByStr(mappingStringByKey(ds, "TEM")));
        atstation.setWinSmax(mappingFloatByStr(mappingStringByKey(ds, "WIN_S_MAX")));
        atstation.setWinDsmax(mappingFloatByStr(mappingStringByKey(ds, "WIN_D_S_MAX")));
        atstation.setVis(mappingFloatByStr(mappingStringByKey(ds, "VIS")));
        return atstation;
    }

    /**
     * @param ds  Map<String, Object>
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

    /**
     * 生成气象站每种产品的色斑图
     *
     * @param atstation
     * @return
     */
    public boolean createContourf(Atstation atstation) {
        if (atstation == null) {
            return false;
        }
        LocalDateTime localDateTime = atstation.getDateTime();
        // 将时间向前推一个小时
        LocalDateTime newLocalDateTime = localDateTime.minusHours(1);
        // newLocalDateTime.
        // 从磁盘上查找文件是否存在
        DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateTime1 = newLocalDateTime.format(dateTimeFormatter1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date parse = null;
        try {
            parse = simpleDateFormat.parse(dateTime1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeLong = parse.getTime();
        System.out.println(dateTime1);
        String yearMonthDay = dateTime1.substring(0, 8);
        String temType = "tem";
        String filePath = commonPath.getDisk() + commonPath.getCommonPath();
        String atstationParsePath = commonPath.getAtstationParsePath();
        String atstationStaAll = commonPath.getAtstationStaAll();
        String fileName = dateTime1 + "_" + temType.toUpperCase() + ".svg";
        // 组装文件在磁盘的全路径
        String totalFilePath = filePath + atstationParsePath + atstationStaAll + yearMonthDay + "/" + temType.toUpperCase() + "/" + fileName;
        totalFilePath = totalFilePath.replaceAll("//", "/");
        System.out.println(totalFilePath);
        File file = new File(totalFilePath);
        if (file.exists()) {
            return true;
        }
        // 磁盘上文件不存在的情况，就从数据库查询相应的数据
        // 获得要查询的数据库表
        List<String> tableName = commonTableName.getTableNamesByType(TypeEnum.TYPE_AT_STATION);
        // 每种产品对应的临界值
        Map<String, Float> criticalMap = new HashMap<>();
        criticalMap.put("rhu", 10000f);
        criticalMap.put("prs", 10000f);
        criticalMap.put("tem", 10000f);
        criticalMap.put("pre_10min", 10000f);
        criticalMap.put("pre_1h", 10000f);
        criticalMap.put("vis", 100000f);
        Map<String, Integer> typeidMap = new HashMap<>();
        typeidMap.put("rhu", 601);
        typeidMap.put("prs", 602);
        typeidMap.put("tem", 603);
        typeidMap.put("pre_10min", 604);
        typeidMap.put("pre_1h", 605);
        typeidMap.put("vis", 606);

        //String bathPath = "D:/Data/atstation_base/ALL/";
        //String targetPath = "D:/Data/atstation_parse/ALL/";
        String bathPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationPath() + commonPath.getAtstationStaAll();
        String targetPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getAtstationParsePath() + commonPath.getAtstationStaAll();
        String pythonPath = "D:/Python/Python37/python.exe";
        String pythonScript = "E:/python_script/气象站色斑图/gen_scatter_contour.py";
        bathPath = bathPath.replaceAll("//", "/");
        targetPath = targetPath.replaceAll("//", "/");
        for (String key : criticalMap.keySet()) {
            int typeId = typeidMap.get(key);
            float criticalValue = criticalMap.get(key);
            String uppperTypeName = "";
            if (key.equals("pre_10min")) {
                uppperTypeName = "RAIN10MIN";
            } else if (key.equals("pre_1h")) {
                uppperTypeName = "RAIN1HOUR";
            } else {
                uppperTypeName = key.toUpperCase();
            }
            String temFileName = dateTime1 + "_" + uppperTypeName + ".txt";
            createProductFile(typeId, key, criticalValue, timeLong, tableName.get(0), bathPath, targetPath);
            createAtstationContourfByType(key, Integer.parseInt(yearMonthDay), temFileName, pythonPath, pythonScript, bathPath);
        }
        return true;
    }

    public void createProductFile(int typeId, String typeName, float criticalValue, long time, String tableName, String bathPath, String targetPath) {
        String uppperTypeName = "";
        if (typeName.equals("pre_10min")) {
            uppperTypeName = "RAIN10MIN";
        } else if (typeName.equals("pre_1h")) {
            uppperTypeName = "RAIN1HOUR";
        } else {
            uppperTypeName = typeName.toUpperCase();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        String datetime = sdf.format(date);
        String fieldStr1 = "max(lon),min(lon),max(lat),min(lat)";
        String fieldStr2 = "lon,lat," + typeName;
        Float filterStr = criticalValue;
        String typeStr = typeName;

        List<HashMap<String, Object>> lists1 = atstationMapper.getByDatetime(tableName, datetime, fieldStr1, typeStr, filterStr);
        List<HashMap<String, Object>> lists2 = atstationMapper.getByDatetime(tableName, datetime, fieldStr2, typeStr, filterStr);
        System.out.println("list2:" + lists2.size());
        List<HashMap<String, Object>> colorLists = colorValueMapper.getByType(typeId);
        List<String> color = new ArrayList<String>();
        List<Float> colorcritical = new ArrayList<Float>();
        for (HashMap<String, Object> map : colorLists) {
            String colorValue = (String) map.get("colorvalue");
            Float criticalvalue = (Float) map.get("criticalvalue");
            color.add(colorValue);
            colorcritical.add(criticalvalue);
        }
        Iterator<HashMap<String, Object>> listIterator = lists2.iterator();
        while (listIterator.hasNext()) {
            HashMap<String, Object> nextMap = listIterator.next();
            Set<String> keySet = nextMap.keySet();
            Iterator<String> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String nextKey = iterator.next();
                if (typeName.equals(nextKey)) {
                    Float temValue = (Float) nextMap.get(nextKey);
                    if (temValue > criticalValue) {
                        listIterator.remove();
                    }
                } else {
                    continue;
                }
            }
        }
        System.out.println(lists1);
        System.out.println(lists2);
        System.out.println("list2:" + lists2.size());
        System.out.println(colorLists);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("lon_max", lists1.get(0).get("max(lon)"));
        result.put("lon_min", lists1.get(0).get("min(lon)"));
        result.put("lat_max", lists1.get(0).get("max(lat)"));
        result.put("lat_min", lists1.get(0).get("min(lat)"));
        //Float lon_min = (Float) lists1.get(1);
        //Float lat_max = (Float) lists1.get(2);
        //Float lon_min = (Float) lists1.get(3);
        Integer n_lon = (int) (((Float) lists1.get(0).get("max(lon)") - (Float) lists1.get(0).get("min(lon)")) / 0.01);
        Integer n_lat = (int) (((Float) lists1.get(0).get("max(lat)") - (Float) lists1.get(0).get("min(lat)")) / 0.01);
        result.put("n_lon", n_lon);
        result.put("n_lat", n_lat);
        result.put("color", color);
        result.put("color_levels", colorcritical);
        result.put("dataMap", lists2);
        //String filePath = "D:/Data/tem_603/20200701/20200701210000_603";
        //String fileDir = "D:/Data/atstation_parse/ALL/"+ datetime.substring(0,10).replaceAll("-","")+"/TEM/"+datetime.substring(11,19).replaceAll(":","");
        String fileDir = targetPath + datetime.substring(0, 10).replaceAll("-", "") + "/" + uppperTypeName;
        //String fileDir = "D:/Data/atstation_parse/ALL/"+ 20200629 +"/TEM";
        File file = new File(fileDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String filePath = fileDir + "/" + datetime.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "")
                + "_" + uppperTypeName;
        String imageFormat = ".svg";
        result.put("filePath", filePath);
        result.put("imageFormat", imageFormat);
        System.out.println(result);
        JSON parse = JSONUtil.parse(result);
        String parseStr = parse.toString();
        System.out.println(parseStr);
        try {
            File f = new File(bathPath + datetime.substring(0, 10).replaceAll("-", "") +
                    "/" + uppperTypeName + "/");
            if (!f.exists()) {
                f.mkdirs();
            }
            writeFile(parseStr, bathPath + datetime.substring(0, 10).replaceAll("-", "") +
                    "/" + uppperTypeName + "/" +
                    datetime.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "") + "_" + uppperTypeName + ".txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createAtstationContourfByType(String typeName, Integer dateTime, String fileName, String pythonPath, String pythonScript, String bathPath) {
        String uppperTypeName = "";
        if (typeName.equals("pre_10min")) {
            uppperTypeName = "RAIN10MIN";
        } else if (typeName.equals("pre_1h")) {
            uppperTypeName = "RAIN1HOUR";
        } else {
            uppperTypeName = typeName.toUpperCase();
        }
        String filePath = bathPath + dateTime + "/" + uppperTypeName + "/" + fileName;
        String cmdCommand = pythonPath + " " + pythonScript + " --config=" + filePath;
        excuteCMDCommand(cmdCommand);
    }

    public void writeFile(String content, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        System.out.println(content);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }


}
