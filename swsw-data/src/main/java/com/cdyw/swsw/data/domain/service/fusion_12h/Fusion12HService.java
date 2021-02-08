package com.cdyw.swsw.data.domain.service.fusion_12h;

import com.alibaba.fastjson.JSON;
import com.cdyw.swsw.common.common.component.CmdUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.data.domain.dao.common.CommonDataMapper;
import com.cdyw.swsw.data.domain.dao.fusion.FusionMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class Fusion12HService {

    private final CommonTable commonTable;
    private final CommonPath commonPath;
    private final CommonDataMapper commonDataMapper;
    private final CmdUtil cmdUtil;
    private final FusionMapper fusionMapper;
    Logger logger = LoggerFactory.getLogger(Fusion12HService.class);

    @Autowired
    public Fusion12HService(CommonTable commonTable, CommonPath commonPath, CommonDataMapper commonDataMapper, CmdUtil cmdUtil, FusionMapper fusionMapper) {
        this.commonTable = commonTable;
        this.commonPath = commonPath;
        this.commonDataMapper = commonDataMapper;
        this.cmdUtil = cmdUtil;
        this.fusionMapper = fusionMapper;
    }

    public void getData(String filepath) {
        // D:\Data\grapes\Z_NAFP_C_BABJ_20201208000000_P_NWPC-GRAPES-3KM-CN-00000.grb2
        // 从文件路径中截取文件名
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        // 从文件名获得数据时间
        String timeStr = filename.substring(0, 10) + "0000";
        // 从时间里截取年月
        String yyyyMM = timeStr.substring(0, 6);
        // 时间里截取年月日
        String yyyyMMdd = timeStr.substring(0, 8);
        // 得到时间戳
        long dateTime = com.cdyw.swsw.common.common.util.DateUtils.getStringToDate(timeStr);
        dateTime /= 1000;
        int forecastCount = Integer.parseInt(filename.substring(12, 15));
        long forecastTime = dateTime + forecastCount * 3600;
        // 拼接要插入的表名
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h()
                + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        // 将数据转移到本地磁盘
        // 拼接要转移到的目标路径
        String totalPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getFusion12hPath() + yyyyMMdd + "/" + filename;
        try {
            FileUtils.copyFile(new File(filepath), new File(totalPath));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(filename + "文件复制失败！");
        }
        int productType = 21;
        // 拼接文件存储的相对路径
        String relativePath = commonPath.getFusion12hPath() + yyyyMMdd + "/";
        // 去掉第一个"/"
        relativePath = relativePath.replaceFirst("/", "");
        // 获得文件的大小
        int fileLength = (int) new File(totalPath).length();
        // 对要插入的数据进行封装
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(filename);
        fileEntity.setPosFile(relativePath);
        fileEntity.setFileSize(fileLength);
        fileEntity.setType(productType);
        fileEntity.setTime(dateTime);
        fileEntity.setModifyType(0);
        long createTime = System.currentTimeMillis() / 1000;
        commonDataMapper.insertFusion(fileEntity, createTime, forecastTime, tablename);
        int status = 1;
        // 记录日志
        recordLog(timeStr, yyyyMM, filename, status, productType);
        try {
            String[] productArray = {"pres", "tc", "rh"};
            for (String prodType : productArray) {
                createFileByProducttype(filepath, filename, yyyyMM, yyyyMMdd, prodType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getParseData2(String filepath) {
        System.out.println("filepath=" + filepath);
        return;
    }

    @Transactional(rollbackFor = Exception.class)
    public void getParseData(String filepath) {
        // D:\Data\grapes\20210105_000000.grapes.12h.nc
        // 从文件路径中截取文件名
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        // 从文件名获得数据时间
        String timeStr = filename.substring(0, 15);
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
        // 拼接要插入的表名
        String baseTablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h()
                + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        String parseTablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h()
                + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        // 将数据转移到本地磁盘
        // 拼接要转移到的目标路径
        String totalPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getFusion12hPath() + yyyyMMdd + "/" + filename;
        try {
            FileUtils.copyFile(new File(filepath), new File(totalPath));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(filename + "文件复制失败！");
        }
        if (!new File(totalPath).exists()) {
            return;
        }
        String saveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getFusion12hParsePath();
        // String pythonPath = "python";
        String pythonPath = commonPath.getPython();
        // String cmdCommandPrefix = "cmd /c";
        String cmdCommandPrefix = commonPath.getCmdCommandPrefix();
        // 获得解析脚本的路径
        String fusion12hScript = this.getClass().getResource(commonPath.getFusion12hResolveScript()).toString().replace("file:/", "");
        // 对文件进行解析
        String cmdCommand = cmdCommandPrefix + " " + pythonPath + " " + fusion12hScript + " " + totalPath + " " + saveDir;
        cmdUtil.executeCMDCommand(cmdCommand);
        // 获得解析后的文件名称
        String monitorPath = saveDir + yyyyMMdd;
        List<String> listFileName = new ArrayList<>();
        com.cdyw.swsw.common.common.util.FileUtils.getAllFileName(monitorPath, listFileName);
        // 找到刚刚生成的文件
        Iterator<String> iterator = listFileName.iterator();
        // 拼接文件存储的相对路径
        String relativePath = commonPath.getFusion12hPath() + yyyyMMdd + "/";
        // 去掉第一个"/"
        relativePath = relativePath.replaceFirst("/", "");
        // 获得文件的大小
        int fileLength = (int) new File(totalPath).length();
        // 对要插入的数据进行封装
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(filename);
        fileEntity.setPosFile(relativePath);
        fileEntity.setFileSize(fileLength);
        fileEntity.setType(25);
        fileEntity.setTime(dateTime);
        fileEntity.setModifyType(0);
        long createTime = System.currentTimeMillis() / 1000;
        logger.info("filename:" + filename + ";baseTablename:" + baseTablename);
        commonDataMapper.insertFusion(fileEntity, createTime, null, baseTablename);
        while (iterator.hasNext()) {
            String temFilename = iterator.next();
            if (!temFilename.startsWith(timeStr)) {
                iterator.remove();
            }
        }
        // 将这些文件存入数据库
        for (String temFilename : listFileName) {
            // 从文件名中获得预报次数和产品要素
            int forecastCount = Integer.parseInt(temFilename.split("_")[1]);
            String product = temFilename.split("_")[3].replace(".json", "");
            int productType;
            switch (product) {
                case "tem":
                    productType = 2501;
                    break;
                case "windfield":
                    productType = 2502;
                    break;
                case "rhu":
                    productType = 2503;
                    break;
                case "prs":
                    productType = 2504;
                    break;
                case "rain":
                    productType = 2505;
                    break;
                default:
                    productType = 2501;
            }
            long forecastTime = dateTime + forecastCount * 3600;
            // 拼接文件存储的相对路径
            relativePath = commonPath.getFusion12hParsePath() + yyyyMMdd + "/" + product.toUpperCase() + "/";
            // 去掉第一个"/"
            relativePath = relativePath.replaceFirst("/", "");
            // 获得文件的大小
            fileLength = (int) new File(totalPath).length();
            // 对要插入的数据进行封装
            fileEntity = new FileEntity();
            fileEntity.setName(temFilename);
            fileEntity.setPosFile(relativePath);
            fileEntity.setFileSize(fileLength);
            fileEntity.setType(productType);
            fileEntity.setTime(dateTime);
            fileEntity.setModifyType(0);
            fileEntity.setLayer(0);
            createTime = System.currentTimeMillis() / 1000;
            commonDataMapper.insertFusion(fileEntity, createTime, forecastTime, parseTablename);
            int status = 0;
            // 记录日志
            recordLog(timeStr, yyyyMM, temFilename, status, productType);
        }
        logger.info("解析数据插入完成。。");
        // 修改数据库其他信息表
        // 插入最新的basetime
        fusionMapper.insertBasetime(dateTime, 0);
        logger.info("basetime插入完成。。");
        // 查询最新basetime的一组数据
        List<Map<String, Object>> detailInfoNewestlist = fusionMapper.selectDetailInfoNewest();
        // 要进行替换的文件名数据集合
        List<Map<String, Object>> replacingFilenameList = new ArrayList<>();
        // 要进行替换的天气状况集合
        List<Map<String, Object>> repalcingWeatherList = new ArrayList<>();
        // 查询最新一组的天气状况
        List<Map<String, Object>> newestWeatherList = fusionMapper.selectNewestWeather();
        if (detailInfoNewestlist != null && detailInfoNewestlist.size() > 0 && detailInfoNewestlist.get(0) != null) {
            // 找到其中进行修改的数据
            for (Map<String, Object> tempMap : detailInfoNewestlist) {
                int temModifyType = (int) tempMap.get("modify_type");
                if (tempMap != null && temModifyType >= 0) {
                    // 通过这条数据的basetime和forecast_count判断这条数据是否可以进行替换
                    long temBasetime = (long) tempMap.get("basetime");
                    int temForecastcount = (int) tempMap.get("forecast_count");
                    long temForecasttime = temBasetime + temForecastcount * 3600;
                    int gapCount = (int) ((temForecasttime - dateTime) / 3600);
                    if (gapCount >= 0) {
                        // 说明可以进行替换, 查询到具体信息
                        int temType = (int) tempMap.get("type");
                        // 判断这条数据是否被修改
                        Map<String, Object> modifyingMap = fusionMapper.selectModified(temBasetime, temType, temForecastcount, 0);
                        if ((int) modifyingMap.get("modify_type") > 0) {
                            replacingFilenameList.add(fusionMapper.selectNewestfile(temBasetime, temType, temForecastcount, 0));
                        }
                        if (temModifyType >= 2) {
                            repalcingWeatherList.addAll(fusionMapper.selectWeather(temBasetime, temType, temForecastcount));
                        }
                    }
                }
            }
        }
        // 插入最新的newestfile
        for (String temFilename : listFileName) {
            // 从文件名中获得预报次数和产品要素
            int forecastCount = Integer.parseInt(temFilename.split("_")[1]);
            String product = temFilename.split("_")[3].replace(".json", "");
            int productType;
            switch (product) {
                case "tem":
                    productType = 2501;
                    break;
                case "windfield":
                    productType = 2502;
                    break;
                case "rhu":
                    productType = 2503;
                    break;
                case "prs":
                    productType = 2504;
                    break;
                case "rain":
                    productType = 2505;
                    break;
                default:
                    productType = 2501;
            }
            long forecastTime = dateTime + forecastCount * 3600;
            if (replacingFilenameList != null && replacingFilenameList.size() > 0) {
                // 找到对应的文件名进行替换
                for (Map<String, Object> temMap : replacingFilenameList) {
                    long temBasetime = (long) temMap.get("basetime");
                    int temForecastcount = (int) temMap.get("forecast_count");
                    int temType = (int) temMap.get("type");
                    long temForecasttime = temBasetime + temForecastcount * 3600;
                    if (forecastTime == temForecasttime && temType == productType) {
                        temFilename = (String) temMap.get("name");
                        break;
                    }
                }
            }
            // 插入最新的文件名
            fusionMapper.insertNewestfile(dateTime, productType, forecastCount, forecastTime, temFilename, 0);
            // 插入最新的modified
            fusionMapper.insertModified(dateTime, productType, forecastCount, 0);
        }
        if (repalcingWeatherList != null && repalcingWeatherList.size() > 0 && repalcingWeatherList.get(0) != null) {
            // 对这里面的值进行修改，然后插入
            for (Map<String, Object> temMap : repalcingWeatherList) {
                long temForecasttime = (long) temMap.get("forecast_time");
                int temForecastCount = (int) ((temForecasttime - dateTime) / 3600);
                temMap.put("forecast_count", temForecastCount);
                temMap.put("basetime", dateTime);
                fusionMapper.insertWeather(temMap);
            }
        }
    }


    public void parseData(String filepath, String originalFilename, String yyyyMM, String yyyyMMdd) throws IOException {
        NetcdfFile ncfile = NetcdfFile.open(filepath);
        System.out.println(ncfile);
        Variable temVariable = ncfile.findVariable("tc");
        Variable lvlVariable = ncfile.findVariable("lvl");
        int[] shape = temVariable.getShape();
        // short[][] temp = (short[][])data0.read().copyToNDJavaArray();
        double[][][] tem = (double[][][]) temVariable.read().copyToNDJavaArray();
        System.out.println("tem:" + tem.length);
        System.out.println(tem[0][0][0]);
        long[] lvl = (long[]) lvlVariable.read().copyToNDJavaArray();
        String product = "TEM";
        String time = originalFilename.substring(0, 10) + "0000";
        Variable lonVariable = ncfile.findVariable("lon");
        Variable latVariable = ncfile.findVariable("lat");
        double[] lonArray = (double[]) lonVariable.read().copyToNDJavaArray();
        double[] latArray = (double[]) latVariable.read().copyToNDJavaArray();
        double lonMin = lonArray[0];
        double lonMax = lonArray[lonArray.length - 1];
        double latMin = latArray[0];
        double latMax = latArray[latArray.length - 1];
        int lonCount = lonArray.length;
        int latCount = latArray.length;
        int forecastCount = Integer.parseInt(originalFilename.substring(12, 15));
        for (int i = 0; i < lvl.length; i++) {
            long layer = lvl[i];
            List<Double> dataList = new ArrayList<Double>();
            for (int j = 0; j < tem[i].length; j++) {
                for (int k = 0; k < tem[i][j].length; k++) {
                    dataList.add(tem[i][j][k]);
                }
            }
            Double[] rainData = dataList.toArray(new Double[dataList.size()]);
            String productType = product.toLowerCase();
            String filename = time + "_" + forecastCount + "_" + layer + "_" + productType + ".json";
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("productType", productType);
            dataMap.put("layer", layer);
            dataMap.put("time", time);
            dataMap.put("lonMin", lonMin);
            dataMap.put("lonMax", lonMax);
            dataMap.put("latMin", latMin);
            dataMap.put("latMax", latMax);
            dataMap.put("filename", filename);
            dataMap.put("data", rainData);
            dataMap.put("lonCount", lonCount);
            dataMap.put("latCount", latCount);
            dataMap.put("modifyType", 0);
            dataMap.put("forecastCount", forecastCount);
            String dataContent = JSON.toJSONString(dataMap);
            // String savePath = "D:\\" + filename;
            String saveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getFusion12hParsePath() + yyyyMMdd + commonPath.getFusion12hTEM();
            File file = new File(saveDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            String savePath = saveDir + filename;
            writeFile(dataContent, savePath);
            String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() + commonTable.getLink()
                    + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
            // 拼接文件存储的相对路径
            String relativePath = commonPath.getFusion12hParsePath() + yyyyMMdd + commonPath.getFusion12hTEM();
            // 去掉第一个"/"
            relativePath = relativePath.replaceFirst("/", "");
            // 获得文件的大小
            int fileLength = (int) new File(savePath).length();
            // 得到时间戳
            long dateTime = com.cdyw.swsw.common.common.util.DateUtils.getStringToDate(time);
            dateTime /= 1000;
            long forecastTime = dateTime + forecastCount * 3600;
            int productCode = 2501;
            String mcode = productCode + "-" + forecastCount;
            // 对要插入的数据进行封装
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(filename);
            fileEntity.setPosFile(relativePath);
            fileEntity.setFileSize(fileLength);
            fileEntity.setType(productCode);
            fileEntity.setTime(dateTime);
            fileEntity.setMcode(mcode);
            fileEntity.setLayer((int) layer);
            fileEntity.setModifyType(0);
            long createTime = System.currentTimeMillis() / 1000;
            commonDataMapper.insertFusion(fileEntity, createTime, forecastTime, tableName);
            int status = 1;
            // 记录日志
            recordLog(time, yyyyMM, filename, status, productCode);
        }
    }

    public void createFileByProducttype(String filepath, String originalFilename, String yyyyMM, String yyyyMMdd, String prodType) throws IOException {
        NetcdfFile ncfile = NetcdfFile.open(filepath);
        System.out.println(ncfile);
        Variable temVariable = ncfile.findVariable(prodType);
        Variable lvlVariable = ncfile.findVariable("lvl");
        int[] shape = temVariable.getShape();
        // short[][] temp = (short[][])data0.read().copyToNDJavaArray();
        double[][][] tem = (double[][][]) temVariable.read().copyToNDJavaArray();
        // System.out.println("tem:" + tem.length);
        System.out.println(tem[0][0][0]);
        long[] lvl = (long[]) lvlVariable.read().copyToNDJavaArray();
        String product;
        int productCode;
        switch (prodType) {
            case "tc":
                product = "TEM";
                productCode = 2501;
                break;
            case "pres":
                product = "PRS";
                productCode = 2504;
                break;
            case "rh":
                product = "RHU";
                productCode = 2503;
                break;
            default:
                product = "TEM";
                productCode = 2501;
        }
        String time = originalFilename.substring(0, 10) + "0000";
        Variable lonVariable = ncfile.findVariable("lon");
        Variable latVariable = ncfile.findVariable("lat");
        double[] lonArray = (double[]) lonVariable.read().copyToNDJavaArray();
        double[] latArray = (double[]) latVariable.read().copyToNDJavaArray();
        double lonMin = lonArray[0];
        double lonMax = lonArray[lonArray.length - 1];
        double latMin = latArray[0];
        double latMax = latArray[latArray.length - 1];
        int lonCount = lonArray.length;
        int latCount = latArray.length;
        int forecastCount = Integer.parseInt(originalFilename.substring(12, 15));
        for (int i = 0; i < lvl.length; i++) {
            long layer = lvl[i];
            List<Double> dataList = new ArrayList<Double>();
            for (int j = 0; j < tem[i].length; j++) {
                for (int k = 0; k < tem[i][j].length; k++) {
                    dataList.add(tem[i][j][k]);
                }
            }
            Double[] prodData = dataList.toArray(new Double[dataList.size()]);
            String productType = product.toLowerCase();
            String filename = time + "_" + forecastCount + "_" + layer + "_" + productType + ".json";
            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("productType", productType);
            dataMap.put("layer", layer);
            dataMap.put("time", time);
            dataMap.put("lonMin", lonMin);
            dataMap.put("lonMax", lonMax);
            dataMap.put("latMin", latMin);
            dataMap.put("latMax", latMax);
            dataMap.put("filename", filename);
            dataMap.put("data", prodData);
            dataMap.put("lonCount", lonCount);
            dataMap.put("latCount", latCount);
            dataMap.put("modifyType", 0);
            dataMap.put("forecastCount", forecastCount);
            String dataContent = JSON.toJSONString(dataMap);
            String productPath = "";
            switch (prodType) {
                case "tc":
                    productPath = commonPath.getFusion12hTEM();
                    break;
                case "pres":
                    productPath = commonPath.getFusion12hPRS();
                    break;
                case "rh":
                    productPath = commonPath.getFusion12hRHU();
                    break;
                default:
                    productPath = commonPath.getFusion12hTEM();

            }
            String saveDir = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getFusion12hParsePath() + yyyyMMdd + productPath;
            File file = new File(saveDir);
            if (!file.exists()) {
                file.mkdirs();
            }
            String savePath = saveDir + filename;
            writeFile(dataContent, savePath);
            String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() + commonTable.getLink()
                    + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
            // 拼接文件存储的相对路径
            String relativePath = commonPath.getFusion12hParsePath() + yyyyMMdd + productPath;
            // 去掉第一个"/"
            relativePath = relativePath.replaceFirst("/", "");
            // 获得文件的大小
            int fileLength = (int) new File(savePath).length();
            // 得到时间戳
            long dateTime = com.cdyw.swsw.common.common.util.DateUtils.getStringToDate(time);
            dateTime /= 1000;
            long forecastTime = dateTime + forecastCount * 3600;
            String mcode = productCode + "-" + forecastCount;
            // 对要插入的数据进行封装
            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(filename);
            fileEntity.setPosFile(relativePath);
            fileEntity.setFileSize(fileLength);
            fileEntity.setType(productCode);
            fileEntity.setTime(dateTime);
            fileEntity.setMcode(mcode);
            fileEntity.setLayer((int) layer);
            fileEntity.setModifyType(0);
            long createTime = System.currentTimeMillis() / 1000;
            commonDataMapper.insertFusion(fileEntity, createTime, forecastTime, tableName);
            int status = 1;
            // 记录日志
            recordLog(time, yyyyMM, filename, status, productCode);
        }
    }


    public void recordLog(String time, String yyyyMM, String filename, int status, int type) {
        // 记录日志
        String logTable = commonTable.getLog() + commonTable.getLink() + commonTable.getTypeFusion12h() +
                commonTable.getLink() + yyyyMM;
        DataMonitorLog log = new DataMonitorLog();
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime fileTime = LocalDateTime.of(Integer.parseInt(time.substring(0, 4)),
                Integer.parseInt(time.substring(4, 6)),
                Integer.parseInt(time.substring(6, 8)),
                Integer.parseInt(time.substring(8, 10)),
                Integer.parseInt(time.substring(10, 12)),
                Integer.parseInt(time.substring(12, 14)));
        String msg = localDateTime + "插入了" + filename + ", 状态为" + status;
        log.setCreateTime(localDateTime);
        log.setDateTime(fileTime);
        log.setType(type);
        log.setStatus(status);
        log.setMsg(msg);
        commonDataMapper.insertCommonLog(log, logTable);
    }


    public void writeFile(String content, String filepath) {
        File file = new File(filepath);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
            fw.write(content);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
