package com.cdyw.swsw.system.service.radar;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.radar.RadarWindProfile;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jovi
 */
@Slf4j
@Service
public class RadarWindProfileService {

    private final CommonDataParse commonDataParse;

    //private final String tableNameByType;

    public RadarWindProfileService(CommonDataParse commonDataParse) {
        this.commonDataParse = commonDataParse;
    }


    /**
     * 返回雷达的站点信息
     *
     * @param profileRadarType String
     * @return CommonResult
     */
    public CommonResult<?> getStationByRadarType(String profileRadarType) {
        List<Map<String, Object>> stationByRadarType = commonDataParse.getStationByRadarType(profileRadarType);
        if (stationByRadarType != null && stationByRadarType.size() > 0) {
            return CommonResult.success(stationByRadarType);
        } else {
            return CommonResult.failed("查询不到数据。。");
        }

    }

    /**
     * 返回风廓线雷达的具体信息
     *
     * @param profileRadarType
     * @param time
     * @param timeType
     * @param height
     * @return
     */
    public CommonResult<?> getStationInfo(String profileRadarType,
                                          Long time,
                                          String timeType,
                                          Integer height) {
        List<Map<String, Object>> stationInfoList = commonDataParse.getStationByRadarType(profileRadarType);
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Map temMap : stationInfoList) {
            Map<String, Object> resultMap = new HashMap<>();
            String stationName = (String) temMap.get("stationName");
            Float lon = (Float) temMap.get("lon");
            Float lat = (Float) temMap.get("lat");
            resultMap.put("stationName", stationName);
            resultMap.put("lon", lon);
            resultMap.put("lat", lat);
            // 查询到对应的文件名
            Map<String, Object> fileNameMap = commonDataParse.getFileNameByPara(TypeEnum.TYPE_RADAR_WIND_PROFILE, stationName, timeType,
                    (time - 60 * 60) + "", time + "", null, null);
            if (fileNameMap == null || fileNameMap.size() == 0) {
                resultMap.put("windSpeed", "");
                resultMap.put("windDirect", "");
            }
            if (fileNameMap != null && fileNameMap.size() > 0) {
                ArrayList<Map<String, Object>> temFileNameList = (ArrayList) fileNameMap.get("filenameList");
                String fileName = (String) temFileNameList.get(0).get("name");
                // 获得文件名在磁盘上的全路径
                Map<String, Object> fileAllPathMap = commonDataParse.getFilePathByPara(TypeEnum.TYPE_RADAR_WIND_PROFILE,
                        stationName, timeType, fileName);
                String filePath = (String) fileAllPathMap.get("fileAllPath");
                String fileAllPath = filePath + fileName;
                FileReader fileReader = new FileReader(fileAllPath);
                List<String> stringList = fileReader.readLines();
                // 将高度的位数
                int heightCount = integerCount(height);
                String heightStr = intToSpecialString(height, heightCount);
                String targetLine = "";
                for (String line : stringList) {
                    if (line.startsWith(heightStr)) {
                        targetLine = line;
                        break;
                    }
                }
                if ("".equals(targetLine)) {
                    resultMap.put("windSpeed", "");
                    resultMap.put("windDirect", "");
                } else {
                    String[] targetArray = targetLine.split(" ");
                    String windSpeed = Float.parseFloat(targetArray[2]) + "";
                    String windDirect = Float.parseFloat(targetArray[1]) + "";
                    resultMap.put("windSpeed", windSpeed);
                    resultMap.put("windDirect", windDirect);
                }
            }
            resultList.add(resultMap);
        }
        return CommonResult.success(resultList);
    }


    public CommonResult<?> getDataByTypeAndTime(String timeType, String staIdC, String startTime, String endTime) {
        switch (timeType) {
            case "6":
                startTime = (Long.parseLong(endTime) - 3599) + "";
                break;
            case "30":
                startTime = (Long.parseLong(endTime) - 6 * 3600 + 1) + "";
                break;
            case "60":
                startTime = (Long.parseLong(endTime) - 12 * 3600 + 1) + "";
                break;
            default:
                return CommonResult.failed("传入的参数有误");
        }
//        String startTimeStr = TimeStampUtil.timeStampToLocalDateTimeStr(startTime);
//        String endTimeStr = TimeStampUtil.timeStampToLocalDateTimeStr(endTime);
        //获得所需要的数据
        Map<String, Object> fileNameByPara = commonDataParse.getFileNameByPara(TypeEnum.TYPE_RADAR_WIND_PROFILE, staIdC, timeType, startTime, endTime, null, null);
        //List<Map<String, Object>> radarWinFileByTime = radarWindProfileMapper.getRadarWinFileByTimestamp(tableNameByType, Long.parseLong(startTime), Long.parseLong(endTime), timeType);
        if (fileNameByPara.size() != 0) {
            return CommonResult.success(fileNameByPara);
        } else {
            return CommonResult.failed("查询不到数据");
        }
    }

    public void getFileByName(String staIdC, String timeType, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        commonDataParse.getTxtWindProByPara(TypeEnum.TYPE_RADAR_WIND_PROFILE, staIdC, timeType, fileName, response, isOnLine);
    }

    /**
     * 暂时不可用
     */
    public void insertRadarWindPro() {
        RadarWindProfile radarWindProfile = new RadarWindProfile();
        Map<String, Object> pathMap = commonDataParse.getFilePathByPara(TypeEnum.TYPE_RADAR_WIND_PROFILE, "", "60", "");
        String fileAllPath = pathMap.get("fileAllPath").toString();
        List<File> files = FileUtil.loopFiles(fileAllPath);
        String name;
        int insert = 0;
        for (File file : files) {
            name = file.getName();
            // 此步骤非常重要，只写入雷达文件，日志文件不管
            if (name.contains("_") && name.endsWith("TXT")) {
                radarWindProfile.setFileName(name);
                String[] nameSplit = name.split("_");
                String date = nameSplit[4];
                LocalDateTime localDateTime = DateUtil.parseLocalDateTime(date, "yyyyMMddHHmmss");
                radarWindProfile.setDateTime(localDateTime);
                radarWindProfile.setFileSize(Float.parseFloat(String.valueOf(file.length())));
                radarWindProfile.setFileUrl(file.getParent());
                radarWindProfile.setFormat(FileUtil.extName(file));
                radarWindProfile.setCreateTime(LocalDateTime.now());
                radarWindProfile.setProvince("四川省");
                radarWindProfile.setCity("成都市");
                radarWindProfile.setCnty("双流区");
                radarWindProfile.setStationIdC("W");
                //insert = radarWindProfileMapper.insert(tableNameByType, radarWindProfile);
                insert++;
            }
        }
        System.out.println(insert);
    }


    // 获得整数的位数
    public int integerCount(int source) {
        int intCount = 0;
        int tempInt = source;
        while (tempInt > 0) {
            tempInt = tempInt / 10;
            intCount++;
        }
        return intCount;
    }

    public String intToSpecialString(int source, int sourceCount) {
        if (sourceCount > 5) {
            return null;
        }
        String preStr = "";
        for (int i = 0; i < 5 - sourceCount; i++) {
            preStr += "0";
        }
        // String
        return preStr + source;

    }

}
