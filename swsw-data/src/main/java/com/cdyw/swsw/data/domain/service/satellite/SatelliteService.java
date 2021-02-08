package com.cdyw.swsw.data.domain.service.satellite;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.UnicodeUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.*;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.common.domain.entity.satellite.Satellite;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.dao.common.CommonDataMapper;
import com.cdyw.swsw.data.domain.dao.satellite.SatelliteMapper;
import com.cdyw.swsw.data.domain.service.fusion_12h.Fusion12HService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jovi
 */
@Slf4j
@Service
public class SatelliteService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SatelliteMapper satelliteMapper;

    private final CommonUrlParam commonUrlParam;

    private final CommonPath commonPath;

    private final CommonTableName commonTableName;

    private final CommonTable commonTable;

    private final CmdUtil cmdUtil;

    private final CommonDataMapper commonDataMapper;

    @Autowired
    public SatelliteService(SatelliteMapper satelliteMapper, CommonUrlParam commonUrlParam, CommonPath commonPath,
                            CommonTableName commonTableName, CommonTable commonTable, CmdUtil cmdUtil, CommonDataMapper commonDataMapper) {
        this.satelliteMapper = satelliteMapper;
        this.commonUrlParam = commonUrlParam;
        this.commonPath = commonPath;
        this.commonTableName = commonTableName;
        this.commonTable = commonTable;
        this.cmdUtil = cmdUtil;
        this.commonDataMapper = commonDataMapper;
    }

    /**
     * 指定时间的卫星资料文件检索接口
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> getSateFileByTimeRange(String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String eleValueRanges, String timeRange) {
        String interfaceId = "getSateFileByTimeRange";
        String resultsJson = mappingUrlToResult(interfaceId, dataFormat, dataCode, elements, limitCnt, orderBy, eleValueRanges, timeRange);
        return mappingResultToList(resultsJson);
    }

    public CommonResult<?> createAndInsertSatelliteParseByParam(int time, String insertValues, String statinIdC) {
        satelliteMapper.createAndInsertSatelliteParseByParam(time, insertValues, statinIdC);
        return CommonResult.success();
    }

    /**
     * 根据json数据结果获取最终数据集合
     *
     * @param resultsJson String
     * @return List<Satellite>
     */
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> mappingResultToList(String resultsJson) {
        List<String> tableNameByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_SATELLITE);
        // 我方数据库为：One   三方数据库为：Third
        List<Satellite> satelliteListThird = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> resultMap = new HashMap<>(16);
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
                                    Satellite satellite = mappingDsToSatel(ds);
                                    satelliteListThird.add(satellite);
                                });
                                List<Satellite> satelliteListOne = new ArrayList<>();
                                for (Satellite satelliteThird : satelliteListThird) {
                                    // 根据第三方数据库的dFileId和dateTime查询我方数据库，并将查询结果存入我方数据库的list中
                                    satelliteListOne.addAll(satelliteMapper.getSateFileByDfileIdAndDate(tableNameByType.get(0), satelliteThird.getDFileId(), DateUtil.format(satelliteThird.getDateTime(), "yyyy-MM-dd HH:mm:ss")));
                                }
                                // *****难点：两个集合去重。一个有id和createTime，另一个没有，两个都有satId和dateTime，所以不能用两个对象直接比较（按照需求：只要satId和dateTime相同，则视为同一种“数据对象”）
                                String filePathAll = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatellitePath() + DateUtil.format(LocalDateTime.now(), "yyyyMMdd");
                                if (satelliteListOne.size() == 0) {
                                    satelliteListThird.forEach(satel ->
                                    {
                                        satelliteMapper.insertSatellite(tableNameByType.get(0), satel);
                                        // 先录入数据库，再下载至本地指定目录下
                                        if (FileUtil.exist(filePathAll)) {
                                            HttpUtil.downloadFile(satel.getFileUrl(), FileUtil.file(filePathAll));
                                        } else {
                                            HttpUtil.downloadFile(satel.getFileUrl(), FileUtil.mkdir(filePathAll));
                                        }
                                    });
                                } else {
                                    for (Satellite satellite : satelliteListOne) {
                                        // 逆序，否则remove有可能会发生异常
                                        // 找出“交集”，并去掉
                                        satelliteListThird.removeIf(satelliteThird -> satellite.getDFileId().equals(satelliteThird.getDFileId()) && satellite.getDateTime().equals(satelliteThird.getDateTime()));
                                    }
                                    Optional.of(satelliteListThird).ifPresent(satListThird -> satListThird.forEach(satel ->
                                    {
                                        satelliteMapper.insertSatellite(tableNameByType.get(0), satel);
                                        // 先录入数据库，再下载至本地指定目录下
                                        if (FileUtil.exist(filePathAll)) {
                                            HttpUtil.downloadFile(satel.getFileUrl(), FileUtil.file(filePathAll));
                                        } else {
                                            HttpUtil.downloadFile(satel.getFileUrl(), FileUtil.mkdir(filePathAll));
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
        log.info(DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss") + ">>>>>执行成功了： " + satelliteListThird.size() + " 条数据");
        if (satelliteListThird.size() > 0) {
            return CommonResult.success(satelliteListThird, UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        } else {
            assert resultMap != null;
            return CommonResult.failed(UnicodeUtil.toString(resultMap.get("returnMessage").toString()));
        }
    }

    private String mappingUrlToResult(String interfaceId, String dataFormat, String dataCode, String elements, String limitCnt, String orderBy, String eleValueRanges, String timeRange) {
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
        String encodeUrl = URLUtil.encode(commonUrlParam.getUrl());
        return HttpUtil.get(encodeUrl, paramsMap);
    }

    /**
     * 匹配DS和SAT数据:
     * 因为是动态匹配，所以必须处理好空指针异常
     *
     * @param ds Map<String, Object>
     * @return satellite
     */
    private Satellite mappingDsToSatel(Map<String, Object> ds) {
        Satellite satellite = new Satellite();
        satellite.setFileName(mappingStringByKey(ds, "FILE_NAME"));
        satellite.setFormat(mappingStringByKey(ds, "FORMAT"));
        satellite.setFileSize(mappingFloatByStr(mappingStringByKey(ds, "FILE_SIZE")));
        satellite.setFileUrl(mappingStringByKey(ds, "FILE_URL"));
        satellite.setDateTime(DateUtil.parseLocalDateTime(mappingStringByKey(ds, "DATETIME")));
        satellite.setFileTime(mappingStringByKey(ds, "FILETIME"));
        satellite.setDataLevl(mappingStringByKey(ds, "DATA_LEVL"));
        satellite.setBulCenter(mappingStringByKey(ds, "BUL_CENTER"));
        satellite.setSateName(mappingStringByKey(ds, "SATE_NAME"));
        satellite.setSateSensor(mappingStringByKey(ds, "SATE_SENSOR"));
        satellite.setDataArea(mappingStringByKey(ds, "DATA_AREA"));
        satellite.setProdId(mappingStringByKey(ds, "PROD_ID"));
        satellite.setSateSensorChanl(mappingStringByKey(ds, "SATE_SENSOR_CHANL"));
        satellite.setProjType(mappingStringByKey(ds, "PROJ_TYPE"));
        satellite.setSpacDpi(mappingStringByKey(ds, "SPAC_DPI"));
        satellite.setDFileSaveHierarchy(mappingIntegerByStr(mappingStringByKey(ds, "D_FILE_SAVE_HIERARCHY")));
        satellite.setDSourceId(mappingStringByKey(ds, "D_SOURCE_ID"));
        satellite.setDFileId(mappingStringByKey(ds, "D_FILE_ID"));
        return satellite;
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

    public void getSatelliteData(String channel) {
        String mdfsSatellitePathPrefix;
        String satelliteChannelPath;
        String saveSatelliteChannel;
        int type;
        switch (channel) {
            case "c002":
                mdfsSatellitePathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsSatellite() +
                        commonPath.getMdfsSatelliteFY4A() + commonPath.getMdfsSatelliteL1() + commonPath.getMdfsSatelliteChina() +
                        commonPath.getMdfsSatellitec002() + commonPath.getMdfsSatellitec002Prefix();
                satelliteChannelPath = commonPath.getSatelliteCHANNEL02();
                saveSatelliteChannel = commonPath.getSatelliteCHANNEL02();
                type = 807;
                break;
            case "c012":
                mdfsSatellitePathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsSatellite() +
                        commonPath.getMdfsSatelliteFY4A() + commonPath.getMdfsSatelliteL1() + commonPath.getMdfsSatelliteChina() +
                        commonPath.getMdfsSatellitec012() + commonPath.getMdfsSatellitec012Prefix();
                satelliteChannelPath = commonPath.getSatelliteCHANNEL12();
                saveSatelliteChannel = commonPath.getSatelliteCHANNEL12();
                type = 817;
                break;
            default:
                mdfsSatellitePathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsSatellite() +
                        commonPath.getMdfsSatelliteFY4A() + commonPath.getMdfsSatelliteL1() + commonPath.getMdfsSatelliteChina() +
                        commonPath.getMdfsSatellitec012() + commonPath.getMdfsSatellitec012Prefix();
                satelliteChannelPath = commonPath.getSatelliteCHANNEL12();
                saveSatelliteChannel = commonPath.getSatelliteCHANNEL12();
                type = 817;
        }
        // mdfs:///SATELLITE/FY4A/L1/CHINA/C012/C012_20201201140000_FY4A.AWX
        // String mdfsStatellitePathSufffix = "_FY4A.AWX";
        String mdfsSatellitePathSufffix = commonPath.getMdfsSatellitePathSufffix();
        // String baseDir = "D:/data";
        String baseDir = commonPath.getDisk() + commonPath.getCommonPath();
        // String statellitePathPrefix = "/satellite_parse/FY4A/";
        String satellitePathPrefix = commonPath.getSatellitePath() + commonPath.getSatelliteStaFY4A();
        // String ipconfigPath = "D:/ip_port.txt";
        String ipconfigPath = commonPath.getMdfsIpconfig();
        // String pythonPath = "python";
        String pythonPath = commonPath.getPython();
        // String statelliteScriptPath = "D:/downloadsatellitedata.py";
        String satelliteDownloadScriptPath = commonPath.getSatelliteDownloadScript();
        // String cmdCommandPrefix = "cmd /c";
        String cmdCommandPrefix = commonPath.getCmdCommandPrefix();
        // 获取当前时间
        String nowTime = DateUtil.now();
        String[] minArray = {"5336", "4918", "4500", "3836", "3418", "3000", "2336", "1918", "1500", "0000"};
        // 循环次数
        int circleCount = 2;
        boolean isEnd = false;
        for (int i = 0; i < circleCount; i++) {
            if (!isEnd) {
                // 格式化当前时间
                Date date = DateUtil.parse(nowTime);
                DateTime offsetDate = DateUtil.offset(date, DateField.HOUR, i - i * 2);
                String formatHourTime = DateUtil.format(offsetDate, "yyyyMMddHH");
                String formatDayTime = DateUtil.format(offsetDate, "yyyyMMdd");
                for (String minTem : minArray) {
                    String integralTime = formatHourTime + minTem;
                    // mdfs:///SATELLITE/FY4A/L1/CHINA/C012/C012_20201126154500_FY4A.AWX
                    String mdfsSatelliteTotalPath = mdfsSatellitePathPrefix + integralTime + mdfsSatellitePathSufffix;
                    // D:/data/satellite_base/FY4A/20201126/CHANNEL12
                    String satelliteSavePath = baseDir + satellitePathPrefix + formatDayTime + satelliteChannelPath;
                    String[] pathArray = mdfsSatelliteTotalPath.split("/");
                    String filename = pathArray[pathArray.length - 1];
                    // D:/data/satellite_parse/FY4A/20201126/CHANNEL02/C012_20201126154500_FY4A.AWX
                    String filePath = satelliteSavePath + filename;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // 文件不存在，说明未下载，执行下载脚本
                        String command = cmdCommandPrefix + " " + pythonPath + " " + satelliteDownloadScriptPath + " " +
                                mdfsSatelliteTotalPath + " " + satelliteSavePath + " " + ipconfigPath;
                        cmdUtil.executeCMDCommand(command);
                    } else {
                        // 文件存在，说明已经存在数据，不需要下载，直接退出循环
                        System.out.println("文件已存在");
                        isEnd = true;
                        break;
                    }
                    // 检查是否存在
                    if (!file.exists()) {
                        System.out.println(filename + "文件不存在");
                    } else {
                        String formatMonthTime = DateUtil.format(offsetDate, "yyyyMM");
                        // 文件存在，说明已经下载成功，将数据索引信息插入到数据库中
                        String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeSatellite() + commonTable.getLink() +
                                commonTable.getStaSatelliteFy4a() + commonTable.getLink() + formatMonthTime + commonTable.getLink() + commonTable.getSufBase();
                        Map<String, Object> dataMap = new HashMap<>();
                        long fileSize = file.length();
                        String radarcd = "FY4A";
                        long time = DateUtils.getStringToDate(integralTime) / 1000;
                        // name, pos_file, file_size, pos_picture, type, radarcd, time, layer, scan_mode, mcode, rain_type
                        dataMap.put("name", filename);
                        dataMap.put("pos_file", null);
                        dataMap.put("file_size", fileSize);
                        dataMap.put("pos_picture", null);
                        dataMap.put("type", type);
                        dataMap.put("radarcd", radarcd);
                        dataMap.put("time", time);
                        dataMap.put("layer", null);
                        dataMap.put("mcode", null);
                        isEnd = true;
                        commonDataMapper.insertCommonData(dataMap, tableName);
                        // D:/Data/satellite_parse/FY4A/20201127/CHANNEL12
                        String savePath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY4A() +
                                formatDayTime + saveSatelliteChannel;
                        String parseTableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeSatellite() + commonTable.getLink() +
                                commonTable.getStaSatelliteFy4a() + commonTable.getLink() + formatMonthTime + commonTable.getLink() + commonTable.getSufParse();
                        String parseFilename = filename.replace(".AWX", ".svg");
                        float lonMin = 70.0f;
                        float lonMax = 140.0f;
                        float latMin = 15.0f;
                        float latMax = 52.0f;
                        createImgAndInsert(filePath, savePath, parseFilename, parseTableName, dataMap, lonMax, lonMin, latMax, latMin);
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }


    /*
    public void getSatelliteData(){
        // String mdfsStatellitePathPrefix = "mdfs:///SATELLITE/FY4A/L1/CHINA/C012/C012_";
        String mdfsSatellitePathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsSatellite() +
                commonPath.getMdfsSatelliteFY4A() + commonPath.getMdfsSatelliteL1() + commonPath.getMdfsSatelliteChina() +
                commonPath.getMdfsSatellitec012() + commonPath.getMdfsSatellitec012Prefix();
        // mdfs:///SATELLITE/FY4A/L1/CHINA/C012/C012_20201201140000_FY4A.AWX
        // String mdfsStatellitePathSufffix = "_FY4A.AWX";
        String mdfsSatellitePathSufffix = commonPath.getMdfsSatellitePathSufffix();
        // String baseDir = "D:/data";
        String baseDir = commonPath.getDisk() + commonPath.getCommonPath();
        // String statellitePathPrefix = "/satellite_parse/FY4A";
        String satellitePathPrefix = commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY4A();
        String satelliteChannel12Path = commonPath.getSatelliteCHANNEL12();
        // String ipconfigPath = "D:/ip_port.txt";
        String ipconfigPath = commonPath.getMdfsIpconfig();
        // String pythonPath = "python";
        String pythonPath = commonPath.getPython();
        // String statelliteScriptPath = "D:/downloadsatellitedata.py";
        String satelliteDownloadScriptPath = commonPath.getSatelliteDownloadScript();
        // String cmdCommandPrefix = "cmd /c";
        String cmdCommandPrefix = commonPath.getCmdCommandPrefix();
        // 获取当前时间
        String nowTime = DateUtil.now();
        String[] minArray = {"5336", "4918",  "4500", "3836", "3418", "3000", "2336", "1918", "1500", "0000"};
        // 循环次数
        int circleCount = 2;
        boolean isEnd = false;
        for (int i = 0; i < circleCount; i++){
            if (!isEnd){
                // 格式化当前时间
                Date date = DateUtil.parse(nowTime);
                DateTime offsetDate = DateUtil.offset(date, DateField.HOUR, i - i * 2);
                String formatHourTime = DateUtil.format(offsetDate, "yyyyMMddHH");
                String formatDayTime = DateUtil.format(offsetDate, "yyyyMMdd");
                for (String minTem: minArray){
                    String integralTime = formatHourTime + minTem;
                    // mdfs:///SATELLITE/FY4A/L1/CHINA/C012/C012_20201126154500_FY4A.AWX
                    String mdfsSatelliteTotalPath = mdfsSatellitePathPrefix + integralTime + mdfsSatellitePathSufffix;
                    // D:/data/satellite_parse/FY4A/20201126/CHANNEL12
                    String satelliteSavePath = baseDir + satellitePathPrefix + "/" + formatDayTime + satelliteChannel12Path;
                    String[] pathArray = mdfsSatelliteTotalPath.split("/");
                    String filename = pathArray[pathArray.length - 1];
                    // D:/data/satellite_parse/FY4A/20201126/CHANNEL02/C012_20201126154500_FY4A.AWX
                    String filePath = satelliteSavePath + filename;
                    File file = new File(filePath);
                    if (!file.exists()){
                        // 文件不存在，说明未下载，执行下载脚本
                        String command = cmdCommandPrefix + " " + pythonPath + " " + satelliteDownloadScriptPath + " " +
                                mdfsSatelliteTotalPath + " " + satelliteSavePath + " " + ipconfigPath;
                        cmdUtil.executeCMDCommand(command);
                    }else {
                        // 文件存在，说明已经存在数据，不需要下载，直接退出循环
                        System.out.println("文件已存在");
                        isEnd = true;
                        break;
                    }
                    // 检查是否存在
                    if (!file.exists()){
                        System.out.println(filename + "文件不存在");
                    }else {
                        String formatMonthTime = DateUtil.format(offsetDate, "yyyyMM");
                        // 文件存在，说明已经下载成功，将数据索引信息插入到数据库中
                        String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeSatellite() + commonTable.getLink() +
                                commonTable.getStaSatelliteFy4a() + commonTable.getLink() + formatMonthTime + commonTable.getLink() + commonTable.getSufBase();
                        Map<String, Object> dataMap = new HashMap<>();
                        long fileSize = file.length();
                        int type = 817;
                        String radarcd = "FY4A";
                        long time = DateUtils.getStringToDate(integralTime) / 1000;
                        // name, pos_file, file_size, pos_picture, type, radarcd, time, layer, scan_mode, mcode, rain_type
                        dataMap.put("name", filename);
                        dataMap.put("pos_file", null);
                        dataMap.put("file_size", fileSize);
                        dataMap.put("pos_picture", null);
                        dataMap.put("type", type);
                        dataMap.put("radarcd", radarcd);
                        dataMap.put("time", time);
                        dataMap.put("layer", null);
                        dataMap.put("scan_mode", null);
                        dataMap.put("mcode", null);
                        dataMap.put("rain_type", null);
                        isEnd = true;
                        commonDataMapper.insertCommonData(dataMap, tableName);
                        // D:/Data/satellite_parse/FY4A/20201127/CHANNEL12
                        String savePath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY4A() +
                                formatDayTime + commonPath.getSatelliteCHANNEL12();
                        String parseTableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeSatellite() + commonTable.getLink() +
                                commonTable.getStaSatelliteFy4a() + commonTable.getLink() + formatMonthTime + commonTable.getLink() + commonTable.getSufParse();
                        String parseFilename = filename.replace(".AWX", ".svg");
                        float lonMin = 70.0f;
                        float lonMax = 140.0f;
                        float latMin = 15.0f;
                        float latMax = 55.0f;
                        createImgAndInsert(filePath, savePath, parseFilename, parseTableName, dataMap, lonMax, lonMin, latMax, latMin);
                        break;
                    }
                }
            }else {
                break;
            }
        }
    }
    */
    public void createImgAndInsert(String filePath, String savePath, String filename, String tableName, Map<String, Object> dataMap,
                                   float lonMax, float lonMin, float latMax, float latMin) {
        String cmdCommandPrefix = commonPath.getCmdCommandPrefix();
        String pythonPath = commonPath.getPython();
        String satelliteCreateImgScript = commonPath.getSatelliteCreateImgScript();
        String cmdCommand = cmdCommandPrefix + " " + pythonPath + " " + satelliteCreateImgScript + " " +
                filePath + " " + savePath + " " + lonMin + " " + lonMax + " " + latMin + " " + latMax;
        System.out.println(Thread.currentThread().getName() + ": " + cmdCommand);
        int exitCode = cmdUtil.executeCMDCommand(cmdCommand);
        System.out.println(filename + " 退出状态码：" + exitCode + "---------------------------------------------------------");
        if (exitCode != 0) {
            System.out.println("脚本执行有误，可能是源数据出现错误，请检查！");
        }
        String totalPath = savePath + filename;
        File file = new File(totalPath);
        if (!file.exists()) {
            System.out.println(totalPath + "文件未生成");
            return;
        }
        long fileSize = file.length();
        dataMap.put("name", filename);
        dataMap.put("file_size", fileSize);
        dataMap.put("lon_min", lonMin);
        dataMap.put("lon_max", lonMax);
        dataMap.put("lat_min", latMin);
        dataMap.put("lat_max", latMax);
        commonDataMapper.insertSatelliteData(dataMap, tableName);
    }

    public void getData(String filepath){
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        // 从文件名获得开始时间
        String[] filenameArray = filename.split("_");
        String startTime = filenameArray[filenameArray.length - 4];
        String endTime = filenameArray[filenameArray.length - 3];
        LocalDateTime startTimeLocal = DateUtils.getDateUtc2Local(startTime, "yyyyMMddHHmmss");
        LocalDateTime endTimeLocal = DateUtils.getDateUtc2Local(endTime, "yyyyMMddHHmmss");
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        startTime = df.format(startTimeLocal);
        endTime = df.format(endTimeLocal);
        long endTimeStamp = DateUtils.getStringToDate(endTime) / 1000;
        // 从时间里截取年月
        String yyyyMM = startTime.substring(0, 6);
        // 时间里截取年月日
        String yyyyMMdd = startTime.substring(0, 8);
        // 得到时间戳
        long dateTime = com.cdyw.swsw.common.common.util.DateUtils.getStringToDate(startTime) / 1000;
        // 复制文件到指定目录
        String baseTargetPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatellitePath() + commonPath.getSatelliteStaFY4A()
                + yyyyMMdd + "/" + filename;
        try {
            FileUtils.copyFile(new File(filepath), new File(baseTargetPath));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(filename + "文件复制失败！");
        }
        String baseProduct = "default";
        String channel02Product = "CHANNEL02";
        String channel10Product = "CHANNEL10";
        String channel12Product = "CHANNEL12";
        int modifyType = 0;
        insertBase(filename, yyyyMM, yyyyMMdd, baseProduct, dateTime, new File(baseTargetPath).length(), modifyType);
        recordLog(startTime, baseProduct, yyyyMM, filename, modifyType);
        String saveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getSatelliteParsePath();
        // String pythonPath = "python";
        String pythonPath = commonPath.getPython();
        // String cmdCommandPrefix = "cmd /c";
        String cmdCommandPrefix = commonPath.getCmdCommandPrefix();
        // 获得解析脚本的路径
        String channel02Script = this.getClass().getResource(commonPath.getSatelliteChannel02ResolveScript()).toString().replace("file:/", "");
        String channel10Script = this.getClass().getResource(commonPath.getSatelliteChannel10ResolveScript()).toString().replace("file:/", "");
        String channel12Script = this.getClass().getResource(commonPath.getSatelliteChannel12ResolveScript()).toString().replace("file:/", "");
        // 要绘制的区域范围
        String geoArea = "10,54,70,140,0.1";
        // 这里纬度减1.8度，因为画出来的图大概偏移了这么多
        double lonMax = 140;
        double lonMin = 70;
        double latMax = 52.2;
        double latMin = 8.2;
        // 对文件进行解析
        String cmdCommandChannel02 = cmdCommandPrefix + " " + pythonPath + " " + channel02Script + " " + filepath + " " + geoArea + " " + saveDir;
        String cmdCommandChannel10 = cmdCommandPrefix + " " + pythonPath + " " + channel10Script + " " + filepath + " " + geoArea + " " + saveDir;
        String cmdCommandChannel12 = cmdCommandPrefix + " " + pythonPath + " " + channel12Script + " " + filepath + " " + geoArea + " " + saveDir;
        logger.error("cmdCommandChannel02:" + cmdCommandChannel02);
        logger.error("cmdCommandChannel10:" + cmdCommandChannel10);
        logger.error("cmdCommandChannel12:" + cmdCommandChannel12);
        cmdUtil.executeCMDCommand(cmdCommandChannel02);
        cmdUtil.executeCMDCommand(cmdCommandChannel10);
        cmdUtil.executeCMDCommand(cmdCommandChannel12);
        // 找到刚刚生成的文件
        String channel02ParseFile = "C002_" + startTime + "_FY4A.png";
        String channel10ParseFile = "C010_" + startTime + "_FY4A.png";
        String channel12ParseFile = "C012_" + startTime + "_FY4A.png";
        String channel02ParsePath = saveDir +  commonPath.getSatelliteStaFY4A() + yyyyMMdd + commonPath.getSatelliteCHANNEL02() + channel02ParseFile;
        String channel10ParsePath = saveDir +  commonPath.getSatelliteStaFY4A() + yyyyMMdd + commonPath.getSatelliteCHANNEL10() + channel10ParseFile;
        String channel12ParsePath = saveDir +  commonPath.getSatelliteStaFY4A() + yyyyMMdd + commonPath.getSatelliteCHANNEL12() + channel12ParseFile;
        Map<String, Object> dataMapCH02 = new HashMap<>();
        Map<String, Object> dataMapCH10 = new HashMap<>();
        Map<String, Object> dataMapCH12 = new HashMap<>();
        dataMapCH02.put("filename", channel02ParseFile);
        dataMapCH02.put("time", startTime);
        dataMapCH02.put("lonMin", lonMin);
        dataMapCH02.put("lonMax", lonMax);
        dataMapCH02.put("latMin", latMin);
        dataMapCH02.put("latMax", latMax);
        dataMapCH02.put("latMax", latMax);
        dataMapCH02.put("endTime", endTimeStamp);
        dataMapCH10.put("filename", channel10ParseFile);
        dataMapCH10.put("time", startTime);
        dataMapCH10.put("lonMin", lonMin);
        dataMapCH10.put("lonMax", lonMax);
        dataMapCH10.put("latMin", latMin);
        dataMapCH10.put("latMax", latMax);
        dataMapCH10.put("endTime", endTimeStamp);
        dataMapCH12.put("filename", channel12ParseFile);
        dataMapCH12.put("time", startTime);
        dataMapCH12.put("lonMin", lonMin);
        dataMapCH12.put("lonMax", lonMax);
        dataMapCH12.put("latMin", latMin);
        dataMapCH12.put("latMax", latMax);
        dataMapCH12.put("endTime", endTimeStamp);
        long CH02fileSize = new File(channel02ParsePath).length();
        long CH10fileSize = new File(channel10ParsePath).length();
        long CH12fileSize = new File(channel12ParsePath).length();
        // 插入数据库
        insertParse(dataMapCH02, yyyyMM, yyyyMMdd, channel02Product, CH02fileSize, modifyType);
        recordLog(startTime, channel02Product, yyyyMM, channel02ParseFile, modifyType);
        insertParse(dataMapCH10, yyyyMM, yyyyMMdd, channel10Product, CH10fileSize, modifyType);
        recordLog(startTime, channel10Product, yyyyMM, channel10ParseFile, modifyType);
        insertParse(dataMapCH12, yyyyMM, yyyyMMdd, channel12Product, CH12fileSize, modifyType);
        recordLog(startTime, channel12Product, yyyyMM, channel12ParseFile, modifyType);
    }




    public void insertBase(String filename, String yyyyMM, String yyyyMMdd, String productType, long time,
                           long fileSize, int modifyType) {
        String relativePath = "";
        int type = 0;
        switch (productType) {
            case "CHANNEL02":
                type = 807;
                relativePath = (commonPath.getSatellitePath() + commonPath.getSatelliteStaFY4A() + yyyyMMdd + commonPath.getSatelliteCHANNEL02()).replaceFirst("/", "");
                break;
            case "CHANNEL10":
                type = 815;
                relativePath = (commonPath.getSatellitePath() + commonPath.getSatelliteStaFY4A() + yyyyMMdd + commonPath.getSatelliteCHANNEL10()).replaceFirst("/", "");
                break;
            case "CHANNEL12":
                type = 817;
                relativePath = (commonPath.getSatellitePath() + commonPath.getSatelliteStaFY4A() + yyyyMMdd + commonPath.getSatelliteCHANNEL12()).replaceFirst("/", "");
                break;
            case "default":
                type = 8;
                relativePath = (commonPath.getSatellitePath() + commonPath.getSatelliteStaFY4A() + yyyyMMdd).replaceFirst("/", "");
                break;
            default:
                type = 807;
                relativePath = (commonPath.getSatellitePath() + commonPath.getSatelliteStaFY4A() + yyyyMMdd + commonPath.getSatelliteCHANNEL02()).replaceFirst("/", "");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("name", filename);
        resultMap.put("posFile", relativePath);
        resultMap.put("fileSize", fileSize);
        resultMap.put("type", type);
        resultMap.put("radarcd", commonTable.getStaSatelliteFy4a().toUpperCase());
        resultMap.put("time", time);
        resultMap.put("layer", null);
        resultMap.put("mcode", null);
        resultMap.put("modifyType", modifyType);
        resultMap.put("createTime", System.currentTimeMillis() / 1000);
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeSatellite() + commonTable.getLink() + commonTable.getStaSatelliteFy4a() +
                commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        commonDataMapper.insertCommonData(resultMap, tablename);
    }

    public void insertParse(Map<String, Object> dataMap, String yyyyMM, String yyyyMMdd, String productType,
                            long fileSize, int modifyType) {
        String time = (String) dataMap.get("time");
        long timestamp = DateUtils.getStringToDate(time) / 1000;
        String relativePath = (commonPath.getSatelliteParsePath() + commonPath.getSatelliteStaFY4A() + yyyyMMdd + "/" + productType.toUpperCase() + "/").replaceFirst("/", "");
        int type = queryProduct(productType);
        double lonMax = (Double) dataMap.get("lonMax");
        double lonMin = (Double) dataMap.get("lonMin");
        double latMax = (Double) dataMap.get("latMax");
        double latMin = (Double) dataMap.get("latMin");
        long endTime = (long)dataMap.get("endTime");
        String filename = (String) dataMap.get("filename");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("name", filename);
        resultMap.put("pos_file", relativePath);
        resultMap.put("file_size", fileSize);
        resultMap.put("pos_picture", null);
        resultMap.put("type", type);
        resultMap.put("radarcd", commonTable.getStaSatelliteFy4a().toUpperCase());
        resultMap.put("time", timestamp);
        resultMap.put("layer", null);
        resultMap.put("mcode", null);
        resultMap.put("modify_type", modifyType);
        resultMap.put("create_time", System.currentTimeMillis() / 1000);
        resultMap.put("lon_max", lonMax);
        resultMap.put("lon_min", lonMin);
        resultMap.put("lat_max", latMax);
        resultMap.put("lat_min", latMin);
        resultMap.put("start_time", timestamp);
        resultMap.put("end_time", endTime);
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeSatellite() + commonTable.getLink()
                + commonTable.getStaSatelliteFy4a() + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        commonDataMapper.insertSatellite(resultMap, tablename);
    }

    public void recordLog(String dataTime, String productType, String yyyyMM, String filename, int status) {
        int type = queryProduct(productType);
        LocalDateTime fileTime = LocalDateTime.of(Integer.parseInt(dataTime.substring(0, 4)),
                Integer.parseInt(dataTime.substring(4, 6)),
                Integer.parseInt(dataTime.substring(6, 8)),
                Integer.parseInt(dataTime.substring(8, 10)),
                Integer.parseInt(dataTime.substring(10, 12)),
                Integer.parseInt(dataTime.substring(12, 14)));
        LocalDateTime localDateTime = LocalDateTime.now();
        // 记录日志
        String logTable = commonTable.getLog() + commonTable.getLink() + commonTable.getTypeSatellite() +
                commonTable.getLink() + yyyyMM;
        DataMonitorLog log = new DataMonitorLog();
        String msg = localDateTime + "插入了" + filename + ", 状态为" + status;
        log.setCreateTime(localDateTime);
        log.setDateTime(fileTime);
        log.setType(type);
        log.setStatus(status);
        log.setMsg(msg);
        commonDataMapper.insertCommonLog(log, logTable);
    }


    public int queryProduct(String productType) {
        int type;
        switch (productType) {
            case "CHANNEL02":
                type = 807;
                break;
            case "CHANNEL10":
                type = 815;
                break;
            case "CHANNEL12":
                type = 817;
                break;
            case "default":
                type = 8;
                break;
            default:
                type = 8;
                break;
        }
        return type;
    }


}
