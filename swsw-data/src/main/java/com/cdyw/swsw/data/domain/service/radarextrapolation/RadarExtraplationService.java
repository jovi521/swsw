package com.cdyw.swsw.data.domain.service.radarextrapolation;

import com.cdyw.swsw.common.common.component.CmdUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.data.domain.dao.common.CommonDataMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class RadarExtraplationService {

    Logger logger = LoggerFactory.getLogger(RadarExtraplationService.class);

    @Autowired
    private CommonPath commonPath;
    @Autowired
    private CommonTable commonTable;
    @Autowired
    private CommonDataMapper commonDataMapper;
    @Autowired
    private CmdUtil cmdUtil;

    public void getDataByParam(String filepath) {
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        // 从文件名获得数据时间
        String timeStr = filename.substring(0, 15);
        String method = filename.split("[.]")[2];
        // 将世界时转换为北京时
        LocalDateTime localDateTime = DateUtils.getDateUtc2Local(timeStr, "yyyyMMdd_HHmmss");
        timeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 从时间里截取年月
        String yyyyMM = timeStr.substring(0, 6);
        // 时间里截取年月日
        String yyyyMMdd = timeStr.substring(0, 8);
        // 得到时间戳
        long dateTime = com.cdyw.swsw.common.common.util.DateUtils.getStringToDate(timeStr);
        dateTime /= 1000;
        // 从文件名中获得雷达型号
        String radarType = filename.split("[.]")[1];
        // 复制到目标路径
        String baseSaveDir = "";
        if (radarType.equals("SA")){
            baseSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getWeatherRadarExtraplationPath()
                    + commonPath.getWeatherRadarExtraplationStaZ9280() + yyyyMMdd + commonPath.getWeatherRadarExtraplationStaMAXREF();
        }else if (radarType.equals("XPAR")){
            baseSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getPhasedArrayRadarExtraplationPath()
                    + commonPath.getPhasedArrayRadarExtraplationStaMultistation() + yyyyMMdd + commonPath.getPhasedArrayRadarExtraplationStaMAXREF();
        }
        // 目标全路径
        String baseTotalPath = baseSaveDir + filename;
        try {
            FileUtils.copyFile(new File(filepath), new File(baseTotalPath));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(filename + "文件复制失败！");
        }
        long baseFileLength = new File(baseTotalPath).length();
        String productType = "MAXREF";
        int modifyType = 0;
        // 插入到数据库
        insertBase(filename, yyyyMM, yyyyMMdd, productType, dateTime, baseFileLength, modifyType, radarType);
        // 记录
        recordLog(timeStr, productType.toLowerCase(), yyyyMM, filename, modifyType, radarType);
        // 得到解析脚本
        String radarExtraplationMaxrefResolveScript = this.getClass().getResource(
                commonPath.getRadarExtraplationMAXREFResolveScript()).toString().replace("file:/", "");
        // String pythonPath = "python";
        String pythonPath = commonPath.getPython();
        // String cmdCommandPrefix = "cmd /c";
        String cmdCommandPrefix = commonPath.getCmdCommandPrefix();
        String parseSaveDir = "";
        if (radarType.equals("SA")){
            parseSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getWeatherRadarExtraplationParsePath();
        }else if (radarType.equals("XPAR")){
            parseSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getPhasedArrayRadarExtraplationParsePath();
        }
        String cmdCommand = cmdCommandPrefix + " " + pythonPath + " " + radarExtraplationMaxrefResolveScript + " " + filepath + " " + parseSaveDir;
        logger.info("radarExtraplationMaxrefResolveScript:" + radarExtraplationMaxrefResolveScript);
        logger.info("cmdCommand:" + cmdCommand);
        cmdUtil.executeCMDCommand(cmdCommand);
        // 从目标文件夹查找生成的文件
        String parseTargetDir = "";
        if (radarType.equals("SA")){
            parseTargetDir = parseSaveDir + commonPath.getWeatherRadarExtraplationStaZ9280() + yyyyMMdd + commonPath.getWeatherRadarExtraplationStaMAXREF();
        }else if (radarType.equals("XPAR")){
            parseTargetDir = parseSaveDir + commonPath.getPhasedArrayRadarExtraplationStaMultistation() + yyyyMMdd + commonPath.getPhasedArrayRadarExtraplationStaMAXREF();
        }
        parseTargetDir = parseTargetDir.replaceAll("//", "/");
        List<String> fileList = new ArrayList<>();
        com.cdyw.swsw.common.common.util.FileUtils.getAllFileName(parseTargetDir, fileList);
        // 去除多余的文件
        Iterator<String> iterator = fileList.iterator();
        while (iterator.hasNext()) {
            String temFilename = iterator.next();
            if (!temFilename.contains(timeStr) || !temFilename.contains(method)) {
                iterator.remove();
            }
        }
        for (String temFilename : fileList) {
            String parseTotalFilePath = parseTargetDir + temFilename;
            // 从文件名中获取层数
            int code = Integer.parseInt(temFilename.split("_")[1]);
            // 从文件名获得方法
            int layer = queryMethod(method);
            double lonMin = 101;
            double lonMax = 106;
            double latMin = 28;
            double latMax = 33;
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("time", timeStr);
            dataMap.put("filename", temFilename);
            dataMap.put("lonMin", lonMin);
            dataMap.put("lonMax", lonMax);
            dataMap.put("latMin", latMin);
            dataMap.put("latMax", latMax);
            dataMap.put("code", code);
            dataMap.put("layer", layer);
            long fileSize = new File(parseTotalFilePath).length();
            // 插入数据库
            insertParse(dataMap, yyyyMM, yyyyMMdd, productType, fileSize, modifyType, radarType);
            // 记录日志
            recordLog(timeStr, productType, yyyyMM, temFilename, modifyType, radarType);
        }


    }

    public void insertBase(String filename, String yyyyMM, String yyyyMMdd, String productType, long time,
                           long fileSize, int modifyType, String radarType) {
        String relativePath = "";
        int type = 0;
        switch (productType) {
            case "MAXREF":
                if (radarType.equals("SA")){
                    type = 26;
                    relativePath = (commonPath.getWeatherRadarExtraplationPath() + commonPath.getWeatherRadarExtraplationStaZ9280()
                            + yyyyMMdd + commonPath.getWeatherRadarExtraplationStaMAXREF()).replaceFirst("/", "");
                }else if (radarType.equals("XPAR")){
                    type = 27;
                    relativePath = (commonPath.getPhasedArrayRadarExtraplationPath() + commonPath.getPhasedArrayRadarExtraplationStaMultistation()
                            + yyyyMMdd + commonPath.getPhasedArrayRadarExtraplationStaMAXREF()).replaceFirst("/", "");
                }
                break;
            default:
                type = 26;
                relativePath = (commonPath.getWeatherRadarExtraplationPath() + commonPath.getWeatherRadarExtraplationStaZ9280()
                        + yyyyMMdd + commonPath.getWeatherRadarExtraplationStaMAXREF()).replaceFirst("/", "");
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
        String tablename = "";
        if (radarType.equals("SA")){
            tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeWeatherRadarExtrapolation() + commonTable.getLink()
                    + commonTable.getStaWeatherRadarExtrapolation() + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        }else if (radarType.equals("XPAR")){
            tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypePhasedarrayRadarExtrapolation() + commonTable.getLink()
                    + commonTable.getStaPhasedarrayRadarExtrapolation() + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        }
        commonDataMapper.insertCommonData(resultMap, tablename);
    }

    public void insertParse(Map<String, Object> dataMap, String yyyyMM, String yyyyMMdd, String productType,
                            long fileSize, int modifyType, String radarType) {
        String time = (String) dataMap.get("time");
        long timestamp = DateUtils.getStringToDate(time) / 1000;
        String relativePath = "";
        if (radarType.equals("SA")){
            relativePath = (commonPath.getWeatherRadarExtraplationParsePath() + commonPath.getWeatherRadarExtraplationStaZ9280() +
                    yyyyMMdd + commonPath.getWeatherRadarExtraplationStaMAXREF()).replaceFirst("/", "");
        }else if (radarType.equals("XPAR")){
            relativePath = (commonPath.getPhasedArrayRadarExtraplationParsePath() + commonPath.getPhasedArrayRadarExtraplationStaMultistation() +
                    yyyyMMdd + commonPath.getPhasedArrayRadarExtraplationStaMAXREF()).replaceFirst("/", "");
        }
        int type = queryProduct(productType, radarType);
        double lonMax = (Double) dataMap.get("lonMax");
        double lonMin = (Double) dataMap.get("lonMin");
        double latMax = (Double) dataMap.get("latMax");
        double latMin = (Double) dataMap.get("latMin");
        String filename = (String) dataMap.get("filename");
        int code = (Integer) dataMap.get("code");
        int layer = (Integer) dataMap.get("layer");
        String mcode = type + "_" + code;
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("name", filename);
        resultMap.put("pos_file", relativePath);
        resultMap.put("file_size", fileSize);
        resultMap.put("pos_picture", null);
        resultMap.put("type", type);
        resultMap.put("radarcd", null);
        resultMap.put("time", timestamp);
        resultMap.put("layer", layer);
        resultMap.put("mcode", mcode);
        resultMap.put("modify_type", modifyType);
        resultMap.put("create_time", System.currentTimeMillis() / 1000);
        resultMap.put("lon_max", lonMax);
        resultMap.put("lon_min", lonMin);
        resultMap.put("lat_max", latMax);
        resultMap.put("lat_min", latMin);
        String tablename = "";
        if (radarType.equals("SA")){
            tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeWeatherRadarExtrapolation() + commonTable.getLink()
                    + commonTable.getStaWeatherRadarExtrapolation() + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        }else if (radarType.equals("XPAR")){
            tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypePhasedarrayRadarExtrapolation() + commonTable.getLink()
                    + commonTable.getStaPhasedarrayRadarExtrapolation() + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        }
        commonDataMapper.insertCldas1kmData(resultMap, tablename);
    }

    public void recordLog(String dataTime, String productType, String yyyyMM, String filename, int status, String radarType) {
        int type = queryProduct(productType, radarType);
        LocalDateTime fileTime = LocalDateTime.of(Integer.parseInt(dataTime.substring(0, 4)),
                Integer.parseInt(dataTime.substring(4, 6)),
                Integer.parseInt(dataTime.substring(6, 8)),
                Integer.parseInt(dataTime.substring(8, 10)),
                Integer.parseInt(dataTime.substring(10, 12)),
                Integer.parseInt(dataTime.substring(12, 14)));
        LocalDateTime localDateTime = LocalDateTime.now();
        // 记录日志
        String logTable = "";
        if (radarType.equals("SA")){
            logTable = commonTable.getLog() + commonTable.getLink() + commonTable.getTypeWeatherRadarExtrapolation() +
                    commonTable.getLink() + yyyyMM;
        }else if (radarType.equals("XPAR")){
            logTable = commonTable.getLog() + commonTable.getLink() + commonTable.getTypePhasedarrayRadarExtrapolation() +
                    commonTable.getLink() + yyyyMM;
        }
        DataMonitorLog log = new DataMonitorLog();
        String msg = localDateTime + "插入了" + filename + ", 状态为" + status;
        log.setCreateTime(localDateTime);
        log.setDateTime(fileTime);
        log.setType(type);
        log.setStatus(status);
        log.setMsg(msg);
        commonDataMapper.insertCommonLog(log, logTable);
    }

    public int queryProduct(String productType, String radarType) {
        int type = 0;
        switch (productType) {
            case "MAXREF":
                switch (radarType){
                    case "SA":
                        type = 2601;
                        break;
                    case "XPAR":
                        type = 2701;
                        break;
                    }
                break;
            case "maxref":
                switch (radarType){
                    case "SA":
                        type = 26;
                        break;
                    case "XPAR":
                        type = 27;
                        break;
                }
                break;
            default:
                type = 26;
        }
        return type;
    }

    public int queryMethod(String method) {
        int type;
        switch (method) {
            case "dnn":
                type = 0;
                break;
            case "var":
                type = 1;
                break;
            case "opflow":
                type = 2;
                break;
            default:
                type = 0;
        }
        return type;
    }

}
