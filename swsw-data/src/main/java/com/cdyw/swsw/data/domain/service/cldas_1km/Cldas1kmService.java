package com.cdyw.swsw.data.domain.service.cldas_1km;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSON;
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
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

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
public class Cldas1kmService {

    private final TypeEnum TYPE = TypeEnum.TYPE_CLDAS_1KM;

    private final CommonPath commonPath;

    private final CommonTable commonTable;

    private final CmdUtil cmdUtil;

    private final CommonDataMapper commonDataMapper;

    private final CommonUrlName commonUrlName;

    private final CommonUrlParam commonUrlParam;

    private final CommonFileName commonFileName;

    private final HttpApi httpApi;

    Logger logger = LoggerFactory.getLogger(Cldas1kmService.class);

    @Autowired
    public Cldas1kmService(CommonPath commonPath, CommonTable commonTable, CmdUtil cmdUtil, CommonDataMapper commonDataMapper, CommonUrlName commonUrlName, CommonUrlParam commonUrlParam, CommonFileName commonFileName, HttpApi httpApi) {
        this.commonPath = commonPath;
        this.commonTable = commonTable;
        this.cmdUtil = cmdUtil;
        this.commonDataMapper = commonDataMapper;
        this.commonUrlName = commonUrlName;
        this.commonUrlParam = commonUrlParam;
        this.commonFileName = commonFileName;
        this.httpApi = httpApi;
    }

    public void getCldasData(String productType) {
        String mdfsCldasPathPrefix;
        String cldas1kmProductPath;
        int type;
        // 根据产品类型拼接得到mdfs接口的前面部分
        switch (productType) {
            case "rain1h":
                mdfsCldasPathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsCldas1km() +
                        commonPath.getMdfsCldas1kmRain1h();
                cldas1kmProductPath = commonPath.getCldas1kmRain1h();
                type = 1101;
                break;
            case "windfield":
                // mdfs:///CLDAS_1KM/WIND/10M_ABOVE_GROUND/20120313.000
                mdfsCldasPathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsCldas1km() +
                        commonPath.getMdfsCldas1kmWindField();
                cldas1kmProductPath = commonPath.getCldas1kmWindfield();
                type = 1102;
                break;
            case "tem":
                mdfsCldasPathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsCldas1km() +
                        commonPath.getMdfsCldas1kmTem();
                cldas1kmProductPath = commonPath.getCldas1kmTem();
                type = 1103;
                break;
            case "rhu":
                mdfsCldasPathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsCldas1km() +
                        commonPath.getMdfsCldas1kmRhu();
                cldas1kmProductPath = commonPath.getCldas1kmRhu();
                type = 1104;
                break;
            default:
                mdfsCldasPathPrefix = commonPath.getMdfsPath() + commonPath.getMdfsCldas1km() +
                        commonPath.getMdfsCldas1kmRain1h();
                cldas1kmProductPath = commonPath.getCldas1kmRain1h();
                type = 1101;
        }
        // mdfs:///CLDAS_1KM/WIND/10M_ABOVE_GROUND/20120313.000
        String mdfsCldas1kmPathSufffix = commonPath.getMdfsCldas1kmSuffix();
        // String baseDir = "D:/data";
        String baseDir = commonPath.getDisk() + commonPath.getCommonPath();
        // String cldas1kmPathPrefix = "/cldas_1km_base/";
        String cldas1kmPathPrefix = commonPath.getCldas1kmPath();
        // String ipconfigPath = "D:/ip_port.txt";
        String ipconfigPath = commonPath.getMdfsIpconfig();
        // String pythonPath = "python";
        String pythonPath = commonPath.getPython();
        String cldas1kmDownloadScriptPath = commonPath.getCldas1kmDownloadScript();
        // String cmdCommandPrefix = "cmd /c";
        String cmdCommandPrefix = commonPath.getCmdCommandPrefix();
        // 获取当前时间
        String nowTime = DateUtil.now();
        // 向前推移小时最多次数
        int circleCount = 2;
        boolean isEnd = false;
        for (int i = 0; i < circleCount; i++) {
            if (!isEnd) {
                // 格式化当前时间
                Date date = DateUtil.parse(nowTime);
                // 时间向前推移
                DateTime offsetDate = DateUtil.offset(date, DateField.HOUR, i - i * 2);
                // 格式化时间
                String formatHourTime = DateUtil.format(offsetDate, "yyMMddHH");
                String formatDayTime = DateUtil.format(offsetDate, "yyyyMMdd");
                // D:/Data/cldas_1km_base/20201203/RAIN1H/
                // 拼接得到想要保存地方的路径
                String cldas1kmSavePath = baseDir + cldas1kmPathPrefix + formatDayTime + cldas1kmProductPath;
                if ("rain1h".equals(productType)) {
                    // mdfs:///CLDAS_1KM/RAIN01/20120313.000
                    // 拼接得到mdfs访问接口全路径
                    String mdfsCldas1kmTotalPath = mdfsCldasPathPrefix + formatHourTime + mdfsCldas1kmPathSufffix;
                    // 获得将要下载的文件名
                    String[] pathArray = mdfsCldas1kmTotalPath.split("/");
                    String filename = pathArray[pathArray.length - 1];
                    // D:/data/satellite_parse/FY4A/20201126/CHANNEL02/C012_20201126154500_FY4A.AWX
                    // 获得保存到本地磁盘的全路径
                    String filePath = cldas1kmSavePath + filename;
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // 文件不存在，说明未下载，执行下载脚本
                        String command = cmdCommandPrefix + " " + pythonPath + " " + cldas1kmDownloadScriptPath + " " +
                                mdfsCldas1kmTotalPath + " " + cldas1kmSavePath + " " + ipconfigPath;
                        cmdUtil.executeCMDCommand(command);
                    } else {
                        // 文件存在，说明已经存在数据，不需要下载，直接退出循环
                        System.out.println("文件已存在");
                        break;
                    }
                    // 检查文件是否下载成功
                    if (!file.exists()) {
                        System.out.println(filename + "文件下载失败");
                    } else {
                        String formatMonthTime = DateUtil.format(offsetDate, "yyyyMM");
                        // 文件存在，说明已经下载成功，将数据索引信息插入到数据库中
                        String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeCldas1km() +
                                commonTable.getLink() + formatMonthTime + commonTable.getLink() + commonTable.getSufBase();
                        Map<String, Object> dataMap = new HashMap<>();
                        long fileSize = file.length();
                        String integralTime = "20" + formatHourTime + "0000";
                        long time = com.cdyw.swsw.common.common.util.DateUtils.getStringToDate(integralTime) / 1000;
                        // name, pos_file, file_size, pos_picture, type, radarcd, time, layer, scan_mode, mcode, rain_type
                        dataMap.put("name", filename);
                        dataMap.put("pos_file", null);
                        dataMap.put("file_size", fileSize);
                        dataMap.put("pos_picture", null);
                        dataMap.put("type", type);
                        dataMap.put("radarcd", null);
                        dataMap.put("time", time);
                        dataMap.put("layer", null);
                        dataMap.put("mcode", null);
                        commonDataMapper.insertCommonData(dataMap, tableName);
                        break;
                    }
                } else {
                    // 获得高度层次对应的路径
                    Map<String, String> heightMap = commonPath.getMdfsCldas1kmHeight();
                    // 进行遍历
                    for (String heightKey : heightMap.keySet()) {
                        String heightValue = heightMap.get(heightKey);
                        // mdfs:///CLDAS_1KM/RAIN01/20120313.000
                        // 拼接得到mdfs访问接口全路径(这里比上面多一个高度层次路径)
                        String mdfsCldas1kmTotalPath = mdfsCldasPathPrefix + heightValue + formatHourTime + mdfsCldas1kmPathSufffix;
                        // 获得将要下载的文件名
                        String[] pathArray = mdfsCldas1kmTotalPath.split("/");
                        String filename = pathArray[pathArray.length - 1];
                        // 获得保存到本地磁盘的全路径
                        String filePath = cldas1kmSavePath + filename;
                        File file = new File(filePath);
                        if (!file.exists()) {
                            // 文件不存在，说明未下载，执行下载脚本
                            String command = cmdCommandPrefix + " " + pythonPath + " " + cldas1kmDownloadScriptPath + " " +
                                    mdfsCldas1kmTotalPath + " " + cldas1kmSavePath + " " + ipconfigPath;
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
                            String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeCldas1km() +
                                    commonTable.getLink() + formatMonthTime + commonTable.getLink() + commonTable.getSufBase();
                            Map<String, Object> dataMap = new HashMap<>();
                            long fileSize = file.length();
                            String integralTime = "20" + formatHourTime + "0000";
                            long time = com.cdyw.swsw.common.common.util.DateUtils.getStringToDate(integralTime) / 1000;
                            // name, pos_file, file_size, pos_picture, type, radarcd, time, layer, scan_mode, mcode, rain_type
                            dataMap.put("name", filename);
                            dataMap.put("pos_file", null);
                            dataMap.put("file_size", fileSize);
                            dataMap.put("pos_picture", null);
                            dataMap.put("type", type);
                            dataMap.put("radarcd", null);
                            dataMap.put("time", time);
                            dataMap.put("layer", Integer.parseInt(heightKey));
                            dataMap.put("mcode", null);
                            isEnd = true;
                            commonDataMapper.insertCommonData(dataMap, tableName);
                            break;
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    public void getCldas1kmData2(String filepath) {
        System.out.println("filepath:" + filepath);
    }

    public void getCldas1kmData(String filepath) {
        // 获得文件名
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        logger.info("filepath:" + filepath);
        logger.info("filename:" + filename);
        String productType = "";
        if (filename.contains("-PRE-")) {
            productType = "rain1h";
            // 认为是降水
        } else if (filename.contains("-UWIN-")) {
            // 认为是风
            try {
                resolveWind(filepath);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("resolveWind:出现了异常。。。");
            }
            return;
        } else if (filename.contains("-TAIR-")) {
            // 认为是温度
            productType = "tem";
        } else {
            return;
        }
        int modifyType = 0;
        // 获得时间(当地时)
        String timeUTC = filename.substring(filename.length() - 15, filename.length() - 5);
        // 转换为时间戳(当地时)
        Long timestamp = DateUtils.getStringToDate5(timeUTC) / 1000;
        String timeUTC8 = DateUtils.getDateToString(timestamp * 1000);
        // 获得yyyyMMdd 和 yyyyMM
        String yyyyMMdd = timeUTC8.substring(0, 8);
        String yyyyMM = timeUTC8.substring(0, 6);
        String saveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getCldas1kmPath() +
                yyyyMMdd + "/" + productType.toUpperCase() + "/";
        if (!new File(saveDir).exists()) {
            new File(saveDir).mkdirs();
        }
        String targetFilepath = saveDir + filename;
        // 将获得的数据转移到指定文件夹
        WriteFileUtil.copyFile(filepath, targetFilepath);
        // 将源数据转换为nc格式
        String modifiedFilename = filename.replace(".GRB2", ".nc");
        String modifiedFilepath = saveDir + modifiedFilename;
        // 调用命令行进行转换
        // wgrib2 D:\\Data\\cldas1km\\20201222\\Z_SURF_C_BABJ_20201222021747_P_CMPA_RT_CHN-BCCD_0P01_HOR-PRE-2020122202.GRB2 -netcdf D:\\pre.nc
        String cmdCommand = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getWgrib2Path() + " " + targetFilepath + " -netcdf " + modifiedFilepath;
//        String cmdCommand = "D:\\wgrib2\\wgrib2.exe " + targetFilepath + " -netcdf " + modifiedFilepath;
        cmdUtil.executeCMDCommand(cmdCommand);
        if (!new File(modifiedFilepath).exists()) {
            // 文件转化失败，记录日志
            logger.error(modifiedFilename + "文件生成失败。。。");
            return;
        }
        insertBase(modifiedFilename, yyyyMM, yyyyMMdd, productType, timestamp, new File(modifiedFilepath).length(), modifyType);
        String cmdPrefix = commonPath.getCmdCommandPrefix();
        String pythonPrefix = commonPath.getPython();
        // 获得解析脚本的路径
        String cldas1kmTemRainScript = this.getClass().getResource(commonPath.getCldas1kmTemRainResolveScript()).toString().replace("file:/", "");
        String parseSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getCldas1kmParsePath();
        String resolveCmdCommand = cmdPrefix + " " + pythonPrefix + " " + cldas1kmTemRainScript + " " + modifiedFilepath + " " + parseSaveDir;
        // 解析数据
        cmdUtil.executeCMDCommand(resolveCmdCommand);
        // 拼接得到解析之后的文件名
        String parseFilename = timeUTC8 + "_" + productType + ".json";
        // 获得拼接得到解析之后的全路径
        String totalJsonPath = parseSaveDir + yyyyMMdd + "/" + productType.toUpperCase() + "/" + parseFilename;
        String content;
        try{
            content = FileUtils.readCharByPath(totalJsonPath);
        } catch (IOException e) {
            e.printStackTrace();
            content = "";
        }
        Map<String, Object> contentMap = (Map<String, Object>)JSONUtil.parse(content);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("time", timeUTC8);
        dataMap.put("lonMax", ((BigDecimal)contentMap.get("lonMax")).doubleValue());
        dataMap.put("lonMin", ((BigDecimal)contentMap.get("lonMin")).doubleValue());
        dataMap.put("latMax", ((BigDecimal)contentMap.get("latMax")).doubleValue());
        dataMap.put("latMin", ((BigDecimal)contentMap.get("latMin")).doubleValue());
        dataMap.put("filename", parseFilename);
        long fileSize = new File(totalJsonPath).length();
        // 插入数据库
        insertParse(dataMap, yyyyMM, yyyyMMdd, productType, fileSize, modifyType);
    }

    public void resolveWind(String filepath) throws IOException {
        // 必须同时监听到uwind,vwind才开始解析
        filepath = filepath.replaceAll("/", "\\\\");
        // 获得文件名
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        String pathyyyyMMdd = pathArray[3];
        // 这里是北京时
        String yyyyMMddHH = filename.split("-")[3].substring(0, 10);
        Long timestamp = DateUtils.getStringToDate5(yyyyMMddHH) / 1000;
        String yyyyMMddHHmmss = DateUtils.getDateToString(timestamp * 1000);
        // 获得年月日
        String yyyyMM = yyyyMMddHHmmss.substring(0, 6);
        String yyyyMMdd = yyyyMMddHHmmss.substring(0, 8);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 找到对应的vwin文件夹
        String sourceDir = commonPath.getDisk() + commonPath.getBaseMonitorPath() + commonPath.getCldas1kmMonitorPath() + pathyyyyMMdd + "/VWIN";
        logger.info("sourceDir:" + sourceDir);
        List<String> filenameList = new ArrayList<>();
        FileUtils.getAllFileName(sourceDir, filenameList);
        // 去除其他时间和其他要素的文件
        Iterator<String> iterator = filenameList.iterator();
        while (iterator.hasNext()) {
            String nextFilename = iterator.next();
            if (!nextFilename.contains(yyyyMMddHH)) {
                iterator.remove();
            }
        }
        String uwinProduct = "UWIN";
        String vwinProduct = "VWIN";
        String uwinTotalpath = "";
        String vwinTotalpath = "";
        String uwinFilename = "";
        String vwinFilename = "";
        for (String temfilename : filenameList) {
            // 拼接得到uwin和vwin的全路径
            uwinTotalpath = filepath;
            uwinFilename = filename;
            vwinTotalpath = sourceDir + "/" + temfilename;
            vwinFilename = temfilename;
        }
        // 复制到指定文件夹
        String uwinSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getCldas1kmPath() +
                yyyyMMdd + "/" + uwinProduct + "/";
        String vwinSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getCldas1kmPath() +
                yyyyMMdd + "/" + vwinProduct + "/";
        if (!new File(uwinSaveDir).exists()) {
            new File(uwinSaveDir).mkdirs();
        }
        if (!new File(vwinSaveDir).exists()) {
            new File(vwinSaveDir).mkdirs();
        }
        String uwinTargetFilepath = uwinSaveDir + uwinFilename;
        String vwinTargetFilepath = vwinSaveDir + vwinFilename;
        // 将获得的数据转移到指定文件夹
        WriteFileUtil.copyFile(uwinTotalpath, uwinTargetFilepath);
        WriteFileUtil.copyFile(vwinTotalpath, vwinTargetFilepath);
        // 将源数据转换为nc格式
        String uwinModifiedFilename = uwinFilename.replace(".GRB2", ".nc");
        String vwinModifiedFilename = vwinFilename.replace(".GRB2", ".nc");
        String uwinModifiedFilepath = uwinSaveDir + uwinModifiedFilename;
        String vwinModifiedFilepath = vwinSaveDir + vwinModifiedFilename;
        // 调用命令行进行转换
        // wgrib2 D:\\Data\\cldas1km\\20201222\\Z_SURF_C_BABJ_20201222021747_P_CMPA_RT_CHN-BCCD_0P01_HOR-PRE-2020122202.GRB2 -netcdf D:\\pre.nc
        String uwinCmdCommand = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getWgrib2Path() + " " + uwinTargetFilepath + " -netcdf " + uwinModifiedFilepath;
        String vwinCmdCommand = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getWgrib2Path() + " " + vwinTargetFilepath + " -netcdf " + vwinModifiedFilepath;
//        String cmdCommand = "D:\\wgrib2\\wgrib2.exe " + targetFilepath + " -netcdf " + modifiedFilepath;
        cmdUtil.executeCMDCommand(uwinCmdCommand);
        cmdUtil.executeCMDCommand(vwinCmdCommand);
        logger.info("uwinCmdCommand:" + uwinCmdCommand);
        logger.info("vwinCmdCommand:" + vwinCmdCommand);
        if (!new File(uwinModifiedFilepath).exists()) {
            // 文件转化失败，记录日志
            logger.error(uwinModifiedFilename + "文件生成失败。。。");
            return;
        }
        if (!new File(vwinModifiedFilepath).exists()) {
            // 文件转化失败，记录日志
            logger.error(vwinModifiedFilename + "文件生成失败。。。");
            return;
        }
        int modifyType = 0;
        insertBase(uwinModifiedFilename, yyyyMM, yyyyMMdd, "uwin", timestamp, new File(uwinModifiedFilepath).length(), modifyType);
        insertBase(vwinModifiedFilename, yyyyMM, yyyyMMdd, "vwin", timestamp, new File(vwinModifiedFilepath).length(), modifyType);
        recordLog(yyyyMMddHHmmss, uwinProduct, yyyyMM, uwinFilename, modifyType);
        recordLog(yyyyMMddHHmmss, vwinProduct, yyyyMM, vwinFilename, modifyType);
        String windfieldSaveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getCldas1kmParsePath();
        // 获得解析脚本的路径
        String cldas1kmWindfieldScript = this.getClass().getResource(commonPath.getCldas1kmWindfieldResolveScript()).toString().replace("file:/", "");
        String resolveCmdCommand = commonPath.getPython() + " " + cldas1kmWindfieldScript + " " + uwinModifiedFilepath + " " +
                vwinModifiedFilepath + " " + windfieldSaveDir;
        logger.info("resolveCmdCommand:" + resolveCmdCommand);
        cmdUtil.executeCMDCommand(resolveCmdCommand);
        String windfieldFilename = yyyyMMddHHmmss + "_windfield.json";
        String windfieldProduct = "windfield";
        String windfieldTotalpath = windfieldSaveDir + yyyyMMdd + "/" + windfieldProduct.toUpperCase() + "/" + windfieldFilename;
        if (!new File(windfieldTotalpath).exists()) {
            // 文件生成失败，记录日志
            logger.error(windfieldFilename + "文件生成失败。。。");
            return;
        }
        String windfieldContent = FileUtils.readCharByPath(windfieldTotalpath);
        Map<String, Object> windfieldMap = (Map<String, Object>) JSONUtil.parse(windfieldContent);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("filename", windfieldFilename);
        dataMap.put("time", yyyyMMddHHmmss);
        dataMap.put("lonMin", ((BigDecimal) windfieldMap.get("lonMin")).doubleValue());
        dataMap.put("lonMax", ((BigDecimal) windfieldMap.get("lonMax")).doubleValue());
        dataMap.put("latMin", ((BigDecimal) windfieldMap.get("latMin")).doubleValue());
        dataMap.put("latMax", ((BigDecimal) windfieldMap.get("latMax")).doubleValue());
        long fileSize = new File(windfieldTotalpath).length();
        // 插入数据库
        insertParse(dataMap, yyyyMM, yyyyMMdd, windfieldProduct, fileSize, modifyType);
        recordLog(yyyyMMddHHmmss, windfieldProduct, yyyyMM, windfieldFilename, modifyType);
    }


    public void insertBase(String filename, String yyyyMM, String yyyyMMdd, String productType, long time,
                           long fileSize, int modifyType) {
        String relativePath = "";
        int type = 0;
        switch (productType) {
            case "rain1h":
                type = 1903;
                relativePath = (commonPath.getCldas1kmPath() + yyyyMMdd + commonPath.getCldas1kmRain1h()).replaceFirst("/", "");
                break;
            case "tem":
                type = 1901;
                relativePath = (commonPath.getCldas1kmPath() + yyyyMMdd + commonPath.getCldas1kmTem()).replaceFirst("/", "");
                break;
            case "uwin":
                type = 1905;
                relativePath = (commonPath.getCldas1kmPath() + yyyyMMdd + commonPath.getCldas1kmUwin()).replaceFirst("/", "");
                break;
            case "vwin":
                type = 1906;
                relativePath = (commonPath.getCldas1kmPath() + yyyyMMdd + commonPath.getCldas1kmVwin()).replaceFirst("/", "");
                break;
            case "windfield":
                type = 1904;
                relativePath = (commonPath.getCldas1kmPath() + yyyyMMdd + commonPath.getCldas1kmWindfield()).replaceFirst("/", "");
                break;
            default:
                type = 1903;
                relativePath = (commonPath.getCldas1kmPath() + yyyyMMdd + commonPath.getCldas1kmRain1h()).replaceFirst("/", "");
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
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeCldas1km() + commonTable.getLink()
                + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        commonDataMapper.insertCommonData(resultMap, tablename);
    }

    public void insertParse(Map<String, Object> dataMap, String yyyyMM, String yyyyMMdd, String productType,
                            long fileSize, int modifyType) {
        String time = (String) dataMap.get("time");
        long timestamp = DateUtils.getStringToDate(time) / 1000;
        String relativePath = (commonPath.getCldas1kmParsePath() + yyyyMMdd + "/" + productType.toUpperCase() + "/").replaceFirst("/", "");
        int type = queryProduct(productType);
        double lonMax = (Double) dataMap.get("lonMax");
        double lonMin = (Double) dataMap.get("lonMin");
        double latMax = (Double) dataMap.get("latMax");
        double latMin = (Double) dataMap.get("latMin");
        String filename = (String) dataMap.get("filename");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("name", filename);
        resultMap.put("pos_file", relativePath);
        resultMap.put("file_size", fileSize);
        resultMap.put("pos_picture", null);
        resultMap.put("type", type);
        resultMap.put("radarcd", null);
        resultMap.put("time", timestamp);
        resultMap.put("layer", null);
        resultMap.put("mcode", null);
        resultMap.put("modify_type", modifyType);
        resultMap.put("create_time", System.currentTimeMillis() / 1000);
        resultMap.put("lon_max", lonMax);
        resultMap.put("lon_min", lonMin);
        resultMap.put("lat_max", latMax);
        resultMap.put("lat_min", latMin);
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeCldas1km() + commonTable.getLink()
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
        String logTable = commonTable.getLog() + commonTable.getLink() + commonTable.getTypeCldas1km() +
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
            case "rain1h":
                type = 1903;
                break;
            case "tem":
                type = 1901;
                break;
            case "uwin":
                type = 1905;
                break;
            case "vwin":
                type = 1906;
                break;
            case "windfield":
                type = 1904;
                break;
            default:
                type = 1903;
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
