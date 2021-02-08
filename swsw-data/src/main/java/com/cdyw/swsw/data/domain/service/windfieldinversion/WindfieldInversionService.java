package com.cdyw.swsw.data.domain.service.windfieldinversion;

import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.*;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.common.util.FileUtils;
import com.cdyw.swsw.common.common.util.WriteFileUtil;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.dto.ds.CimissDsResult;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.common.component.CommonFileName;
import com.cdyw.swsw.data.common.http.HttpApi;
import com.cdyw.swsw.data.domain.dao.common.CommonDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author liudong
 * @modified jovi
 */
@Service
public class WindfieldInversionService {

    private final TypeEnum TYPE = TypeEnum.TYPE_CLDAS_1KM;

    private final CommonPath commonPath;

    private final CommonTable commonTable;

    private final CmdUtil cmdUtil;

    private final CommonDataMapper commonDataMapper;

    private final CommonUrlName commonUrlName;

    private final CommonUrlParam commonUrlParam;

    private final CommonFileName commonFileName;

    private final HttpApi httpApi;

    Logger logger = LoggerFactory.getLogger(WindfieldInversionService.class);

    @Autowired
    public WindfieldInversionService(CommonPath commonPath, CommonTable commonTable, CmdUtil cmdUtil, CommonDataMapper commonDataMapper, CommonUrlName commonUrlName, CommonUrlParam commonUrlParam, CommonFileName commonFileName, HttpApi httpApi) {
        this.commonPath = commonPath;
        this.commonTable = commonTable;
        this.cmdUtil = cmdUtil;
        this.commonDataMapper = commonDataMapper;
        this.commonUrlName = commonUrlName;
        this.commonUrlParam = commonUrlParam;
        this.commonFileName = commonFileName;
        this.httpApi = httpApi;
    }

    public void getData(String filepath) {
        // 获得文件名
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        logger.info("filepath:" + filepath);
        logger.info("filename:" + filename);
        String productType = "windfield";
        int modifyType = 0;
        // 获得时间(当地时)
        String timeUTC = filename.substring(0, 15);
        // 转换为时间戳(国际时)
        Long timestamp = DateUtils.getDateUtc2TimeLocal(timeUTC, "yyyyMMdd_HHmmss");
        String timeUTC8 = DateUtils.getDateToString(timestamp * 1000);
        // 获得yyyyMMdd 和 yyyyMM
        String yyyyMMdd = timeUTC8.substring(0, 8);
        String yyyyMM = timeUTC8.substring(0, 6);
        String saveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getWindfieldInversionPath() +
                yyyyMMdd + "/" + productType.toUpperCase() + "/";
        if (!new File(saveDir).exists()) {
            new File(saveDir).mkdirs();
        }
        String targetFilepath = saveDir + filename;
        // 将获得的数据转移到指定文件夹
        WriteFileUtil.copyFile(filepath, targetFilepath);
        if (!new File(targetFilepath).exists()) {
            // 文件转化失败，记录日志
            logger.error(filename + "文件生成失败。。。");
            return;
        }
        insertBase(filename, yyyyMM, yyyyMMdd, productType.toUpperCase(), timestamp, new File(targetFilepath).length(), modifyType);
        // 记录日志
        recordLog(timeUTC8, productType, yyyyMM, filename, modifyType);
        String saveParseDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getWindfieldInversionParsePath();
        // 调用脚本解析数据
        String windfieldInversionScript = this.getClass().getResource(commonPath.getWindfieldInversionResolveScript()).toString().replace("file:/", "");
        String resolveCmdCommand = commonPath.getPython() + " " + windfieldInversionScript + " " + targetFilepath + " " + saveParseDir;
        logger.info("windfieldInversionScript:" + windfieldInversionScript);
        logger.info("resolveCmdCommand:" + resolveCmdCommand);
        cmdUtil.executeCMDCommand(resolveCmdCommand);
        // 查看数据是否生成
        String parseTargetFilepath = saveParseDir + yyyyMMdd + "/" + productType.toUpperCase();
        List<String> fileList = new ArrayList<>();
        FileUtils.getAllFileName(parseTargetFilepath, fileList);
        // 去除多余的文件
        Iterator<String> iterator = fileList.iterator();
        while (iterator.hasNext()) {
            String temFilename = iterator.next();
            if (!temFilename.contains(timeUTC8)) {
                iterator.remove();
            }
        }
        for (String temFilename : fileList) {
            String totalFilePath = parseTargetFilepath + "/" + temFilename;
            String content = "";
            try {
                content = FileUtils.readCharByPath(totalFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 从文件名中获取层数
            int layer = Integer.parseInt(temFilename.split("_")[1]);
            Map<String, Object> temMap = (Map<String, Object>) JSONUtil.parse(content);
            double lonMin = ((BigDecimal) temMap.get("lonMin")).doubleValue();
            double lonMax = ((BigDecimal) temMap.get("lonMax")).doubleValue();
            double latMin = ((BigDecimal) temMap.get("latMin")).doubleValue();
            double latMax = ((BigDecimal) temMap.get("latMax")).doubleValue();
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("time", timeUTC8);
            dataMap.put("filename", temFilename);
            dataMap.put("lonMin", lonMin);
            dataMap.put("lonMax", lonMax);
            dataMap.put("latMin", latMin);
            dataMap.put("latMax", latMax);
            dataMap.put("layer", layer);
            long fileSize = new File(totalFilePath).length();
            // 插入数据库
            insertParse(dataMap, yyyyMM, yyyyMMdd, productType, fileSize, modifyType);
            // 记录日志
            recordLog(timeUTC8, productType, yyyyMM, temFilename, modifyType);
        }
    }


    public void insertBase(String filename, String yyyyMM, String yyyyMMdd, String productType, long time,
                           long fileSize, int modifyType) {
        String relativePath = "";
        int type = 0;
        switch (productType) {
            case "windfield":
                type = 28;
                relativePath = (commonPath.getWindfieldInversionPath() + yyyyMMdd + commonPath.getWindfieldInversionWINDFIELD()).replaceFirst("/", "");
                break;
            default:
                type = 28;
                relativePath = (commonPath.getWindfieldInversionPath() + yyyyMMdd + commonPath.getWindfieldInversionWINDFIELD()).replaceFirst("/", "");
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("name", filename);
        resultMap.put("posFile", relativePath);
        resultMap.put("fileSize", fileSize);
        resultMap.put("type", type);
        resultMap.put("radarcd", null);
        resultMap.put("time", time);
        resultMap.put("layer", null);
        resultMap.put("mcode", null);
        resultMap.put("modifyType", modifyType);
        resultMap.put("createTime", System.currentTimeMillis() / 1000);
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getType3dWindfieldInversion() + commonTable.getLink()
                + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        commonDataMapper.insertCommonData(resultMap, tablename);
    }

    public void insertParse(Map<String, Object> dataMap, String yyyyMM, String yyyyMMdd, String productType,
                            long fileSize, int modifyType) {
        String time = (String) dataMap.get("time");
        long timestamp = DateUtils.getStringToDate(time) / 1000;
        String relativePath = (commonPath.getWindfieldInversionParsePath() + yyyyMMdd + "/" + productType.toUpperCase() + "/").replaceFirst("/", "");
        int type = queryProduct(productType);
        double lonMax = (Double) dataMap.get("lonMax");
        double lonMin = (Double) dataMap.get("lonMin");
        double latMax = (Double) dataMap.get("latMax");
        double latMin = (Double) dataMap.get("latMin");
        String filename = (String) dataMap.get("filename");
        int layer = (Integer) dataMap.get("layer");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("name", filename);
        resultMap.put("pos_file", relativePath);
        resultMap.put("file_size", fileSize);
        resultMap.put("pos_picture", null);
        resultMap.put("type", type);
        resultMap.put("radarcd", null);
        resultMap.put("time", timestamp);
        resultMap.put("layer", layer);
        resultMap.put("mcode", null);
        resultMap.put("modify_type", modifyType);
        resultMap.put("create_time", System.currentTimeMillis() / 1000);
        resultMap.put("lon_max", lonMax);
        resultMap.put("lon_min", lonMin);
        resultMap.put("lat_max", latMax);
        resultMap.put("lat_min", latMin);
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getType3dWindfieldInversion() + commonTable.getLink()
                + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        commonDataMapper.insertCldas1kmData(resultMap, tablename);
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
        String logTable = commonTable.getLog() + commonTable.getLink() + commonTable.getType3dWindfieldInversion() +
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
        int type = 0;
        switch (productType) {
            case "windfield":
                type = 2801;
                break;
            case "WINDFIELD":
                type = 28;
                break;
        }
        return type;
    }


    /**
     * 仅下载文件，文件保存和入库全部交给监控
     *
     * @return size
     */
    public CommonResult<?> getCldas1kmBase() {
        boolean flag = false;
        Map<String, Object> paramMap = commonUrlName.getUrlParamMap(TYPE, null, commonUrlParam.getDataCodeCldas1km());
        Map<String, Object> resultMap = httpApi.getResult(paramMap, commonUrlParam.getUserId2(), commonUrlParam.getPwd2());
        List<Object> objects = commonUrlName.parseMap2Object(TYPE, resultMap);
        for (Object cimissDsResult : objects) {
            if (cimissDsResult instanceof CimissDsResult) {
                // 此处调用下载 api ，下载文件至 BaseDataFiles，然后使用监控文件夹即可
                flag = commonFileName.downloadFile2Base(TYPE, ((CimissDsResult) cimissDsResult).getFileName(), ((CimissDsResult) cimissDsResult).getFileUrl());
            }
        }
        if (flag) {
            CommonResult.success(objects);
        }
        return CommonResult.failed();
    }

    /**
     * 文件保存和入库
     *
     * @param fileSource 监控文件夹下的文件
     * @return size
     */
    public int insertCldas1kmBase(File fileSource) {
        return commonFileName.insertFileEntityBase(TYPE, fileSource);
    }
}
