package com.cdyw.swsw.system.service.atstation;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.AtstationParseTxt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import com.cdyw.swsw.system.app.util.TimeStampUtil;
import com.cdyw.swsw.system.dao.atstation.AtstationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author jovi
 */
@Service
public class AtstationService {

    private final TypeEnum TYPE = TypeEnum.TYPE_AT_STATION;

    private final AtstationMapper atstationMapper;

    private final CommonTableName commonTableName;

    private final CommonDataParse commonDataParse;

    private final CommonTable commonTable;

    private final CommonPath commonPath;

    @Autowired
    public AtstationService(AtstationMapper atstationMapper, CommonTableName commonTableName, CommonDataParse commonDataParse, CommonTable commonTable, CommonPath commonPath) {
        this.atstationMapper = atstationMapper;
        this.commonTableName = commonTableName;
        this.commonDataParse = commonDataParse;
        this.commonTable = commonTable;
        this.commonPath = commonPath;
    }

    public void createATStationTableByName() {
        System.out.println("*******createATStationTableByName*******" + DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
    }

    public void selectAtstationData() {
        System.out.println("************selectAtstationData**********" + DateUtil.format(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss"));
    }


    public CommonResult<?> getDataByTypeAndTime(String type, String startTime, String endTime) {
        String t = commonTable.getSufBase();
        List<Map<String, Object>> dataByTypeAndTime;
        String tableName = commonTableName.getTableNameByParam(TYPE, commonPath.getAtstationStaAll(), TimeStampUtil.timeStampToTimeStr(Long.parseLong(startTime), "yyyyMMddHHmmss"), t);
        Assert.notNull(startTime, "startTime is null");
        Assert.notNull(endTime, "endTime is null");
        // 此处时间数据库存的是 datetime 类型
        // 数据库存储是世界时
        startTime = (Long.parseLong(startTime) - 8 * 3600) + "";
        endTime = (Long.parseLong(endTime) - 8 * 3600) + "";
        startTime = TimeStampUtil.timeStampToTimeStr(Long.parseLong(startTime), "yyyy-MM-dd HH:mm:ss");
        endTime = TimeStampUtil.timeStampToTimeStr(Long.parseLong(endTime), "yyyy-MM-dd HH:mm:ss");
        dataByTypeAndTime = atstationMapper.getDataByTypeAndTimestamp(tableName, type, startTime, endTime);
        if (dataByTypeAndTime != null) {
            return CommonResult.success(dataByTypeAndTime);
        } else {
            return CommonResult.failed();
        }
    }

    public CommonResult<?> getDataParseByTypeAndTime(String type, String startTime, String endTime) throws IOException {
        String staIdC = commonTable.getStaAtStation();
        //this.staIdC = staIdC;
        //this.type = type;
        AtstationParseTxt txt = commonDataParse.getAtstationTxtByPara(TypeEnum.TYPE_AT_STATION, staIdC, type, startTime, endTime);
        if (txt != null) {
            return CommonResult.success(txt);
        } else {
            return CommonResult.failed();
        }
    }

    public void getFileParseByFileName(String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        String staIdC = commonTable.getStaAtStation();
        commonDataParse.getAtstationPngByFileName(TypeEnum.TYPE_AT_STATION, staIdC, type, fileName, response, isOnLine);
    }

    public CommonResult<?> getPastTimeDate(String stationId, Long time) {
        if (stationId == null || time == null) {
            return CommonResult.failed("传入的参数不能为空！");
        }
        // 得到需要查询区域的开始时间和结束时间
        Long endTime = time;
        // 这里减去一天的时间戳减1，为了防止同时取到两端的时间极值
        Long difference = (long) (3600 * 24 - 1);
        Long startTime = endTime - difference;
        // 先获得需要查询的数据库表
        List<String> tableName2 = commonTableName.getTableNamesByType(TypeEnum.TYPE_AT_STATION);
        List<String> tableName1 = commonTableName.getTableNamesByType(TypeEnum.TYPE_AT_STATION);
        // 声明极值数据集合和所有数据集合以及封装所有数据的集合
        HashMap<String, Object> extremevalueMap;
        List<HashMap<String, Object>> allProductDataList;
        HashMap<String, Object> resultMap = new HashMap<>();
        if (Objects.equals(tableName1, tableName2)) {
            // 如果两个表名相同，说明所有数据在一张表内，则不进行跨表查询
            extremevalueMap = atstationMapper.selectExtremevalue(tableName1.get(0), null, startTime, endTime, stationId);
            allProductDataList = atstationMapper.selectAllProduct(tableName1.get(0), null, startTime, endTime, stationId);
        } else {
            // 两个表名不同，说明要进行跨表查询
            extremevalueMap = atstationMapper.selectExtremevalue(tableName1.get(0), tableName2.get(0), startTime, endTime, stationId);
            allProductDataList = atstationMapper.selectAllProduct(tableName1.get(0), tableName2.get(0), startTime, endTime, stationId);
        }
        if ((extremevalueMap == null || extremevalueMap.size() == 0)
                && (allProductDataList == null || allProductDataList.size() == 0)) {
            return CommonResult.failed("查询的结果不存在。。。");
        }
        // 获得极值数据
        String minTem = (String) extremevalueMap.get("min_tem");
        String maxTem = (String) extremevalueMap.get("max_tem");
        String maxWin = (String) extremevalueMap.get("max_win");
        String maxPre = (String) extremevalueMap.get("max_pre");
        String maxWinDirect = "-";
        // 当最大风速为空时，则最大风向也不存在
        if ("-".equals(maxWin)) {
            maxWinDirect = "-";
        }
        // 将所有应该查询的时间放入到一个map
        Map<Integer, Long> allTimeMap = new HashMap<>();
        // 将结束时间时间戳转换为向下取整的整点小时时间戳
        long endTimefull = getIntegralTimestamp(endTime);
        // 将每个整点时间戳放到集合中
        for (int i = 0; i < 24; i++) {
            allTimeMap.put(i, endTimefull - (23 - i) * 3600);
        }
        // 遍历所有产品集合里存在的时间戳，找到不存在的时间戳
        for (HashMap<String, Object> temMap : allProductDataList) {
            Long temTimestamp = (Long) temMap.get("timestamp");
            for (Integer key : allTimeMap.keySet()) {
                Long timeValue = allTimeMap.get(key);
                if (Objects.equals(temTimestamp, timeValue)) {
                    allTimeMap.remove(key);
                    break;
                }
            }
        }
        // 声明每种产品对应时间点的数据的集合
        List<String> temList = new ArrayList<>();
        List<String> prsList = new ArrayList<>();
        List<String> rhuList = new ArrayList<>();
        List<String> winList = new ArrayList<>();
        List<String> winDirectList = new ArrayList<>();
        List<String> preList = new ArrayList<>();
        List<String> visList = new ArrayList<>();
        // 声明极值数据所对应的时间
        Long maxTemTime = null;
        Long minTemTime = null;
        Long maxWinTime = null;
        // 遍历所有产品数据，得到想要的字段
        for (HashMap<String, Object> temMap : allProductDataList) {
            String tempTem = (String) temMap.get("tem");
            temList.add(tempTem);
            prsList.add((String) temMap.get("prs"));
            rhuList.add((String) temMap.get("rhu"));
            String tempWin = (String) temMap.get("win_s_max");
            winList.add(tempWin);
            String tempWinDirect = (String) temMap.get("win_d_s_max");
            winDirectList.add(tempWinDirect);
            preList.add((String) temMap.get("pre_1h"));
            visList.add((String) temMap.get("vis"));
            // 当最大风速不为空时，在这里找到最大风速对应的风向
            if ((!"-".equals(maxWin)) && (!"-".equals(tempWin))) {
                if (Objects.equals(maxWin, tempWin) || (Float.parseFloat(maxWin) == Float.parseFloat(tempWin))) {
                    maxWinDirect = tempWinDirect;
                    maxWinTime = (Long) temMap.get("timestamp");
                }
            }
            // 查找最高温度所对应的时间
            if ((!"-".equals(maxTem)) && (!"-".equals(tempTem))) {
                if (Objects.equals(maxTem, tempTem) || (Float.parseFloat(maxTem) == Float.parseFloat(tempTem))) {
                    maxTemTime = (Long) temMap.get("timestamp");
                }
            }
            // 查找最低温度所对应的时间
            if ((!"-".equals(minTem)) && (!"-".equals(tempTem))) {
                if (Objects.equals(minTem, tempTem) || (Float.parseFloat(minTem) == Float.parseFloat(tempTem))) {
                    minTemTime = (Long) temMap.get("timestamp");
                }
            }
        }
        // 对于不存在数据的某些时刻，将数据置为空
        for (Integer key : allTimeMap.keySet()) {
            prsList.add(key, "-");
            temList.add(key, "-");
            rhuList.add(key, "-");
            winList.add(key, "-");
            winDirectList.add(key, "-");
            preList.add(key, "-");
            visList.add(key, "-");
        }
        // 声明判断过去时间段降水是否为空的标志
        boolean isEmptyPast3hRain;
        boolean isEmptyPast6hRain;
        boolean isEmptyPast12hRain;
        boolean isEmptyPast24hRain;
        // 过去时间段内降水为空的时间点个数，达到一定数量就可判断当前时间段降水为空
        int emptyPast3hRainCount = 0;
        int emptyPast6hRainCount = 0;
        int emptyPast12hRainCount = 0;
        int emptyPast24hRainCount = 0;
        for (int i = 23; i >= 0; i--) {
            if ("-".equals(preList.get(i))) {
                if (i > 20) {
                    emptyPast3hRainCount++;
                }
                if (i > 17) {
                    emptyPast6hRainCount++;
                }
                if (i > 11) {
                    emptyPast12hRainCount++;
                }
                emptyPast24hRainCount++;
            }
        }
        // 根据缺少的时间点个数来判断是否过去降水为空
        isEmptyPast3hRain = emptyPast3hRainCount >= 3;
        isEmptyPast6hRain = emptyPast6hRainCount >= 6;
        isEmptyPast12hRain = emptyPast12hRainCount >= 12;
        isEmptyPast24hRain = emptyPast24hRainCount >= 24;
        // 获得过去3h, 6h, 12h, 24h 的累计降水量
        int past3hRain = 0;
        int past6hRain = 0;
        int past12hRain = 0;
        int past24hRain = 0;
        // 只要有一个时间段降水不为空，就进行降水量累加
        if (!(isEmptyPast3hRain && isEmptyPast6hRain && isEmptyPast12hRain && isEmptyPast24hRain)) {
            for (int i = 23; i >= 0; i--) {
                String temRianStr = preList.get(i);
                Float temRain = 0f;
                if (!"-".equals(temRianStr)) {
                    temRain = Float.parseFloat(temRianStr);
                }
                // 这里乘以10000是让浮点数转为整型，防止累加时出错
                if (i > 20) {
                    past3hRain += (int) (temRain * 10000);
                }
                if (i > 17) {
                    past6hRain += (int) (temRain * 10000);
                }
                if (i > 11) {
                    past12hRain += (int) (temRain * 10000);
                }
                past24hRain += (int) (temRain * 10000);
            }
        }
        // 获得结束时间所对应的向下取整小时
        int integarHour = getIntegarHour(endTime);
        // 获得时间段分组方式
        List<Integer> groupNum = splitByPeriodOfTime(integarHour);
        // 声明每个时间段内应包含的数据
        List<String> maxWindInPeriod = new ArrayList<>();
        List<String> totalRainInPeriod = new ArrayList<>();
        List<String> maxTemInPeriod = new ArrayList<>();
        List<String> minTemInPeriod = new ArrayList<>();
        // 将这个时间段内的数据按照一定条件填充到分别时间段内
        fillSegmentDatalist(groupNum, maxWindInPeriod, totalRainInPeriod, maxTemInPeriod, minTemInPeriod,
                winList, preList, temList);
        resultMap.put("minTem", minTem);
        resultMap.put("maxTem", maxTem);
        resultMap.put("maxWin", maxWin);
        resultMap.put("maxPre", maxPre);
        resultMap.put("maxWinDirect", maxWinDirect);
        resultMap.put("maxWinTime", maxWinTime == null ? "-" : (maxWinTime + ""));
        resultMap.put("maxTemTime", maxTemTime == null ? "-" : (maxTemTime + ""));
        resultMap.put("minTemTime", minTemTime == null ? "-" : (minTemTime + ""));
        resultMap.put("prsList", prsList);
        resultMap.put("temList", temList);
        resultMap.put("rhuList", rhuList);
        resultMap.put("winList", winList);
        resultMap.put("winDirectList", winDirectList);
        resultMap.put("preList", preList);
        resultMap.put("visList", visList);
        resultMap.put("past3hRain", isEmptyPast3hRain ? "-" : (((float) past3hRain) / 10000) + "");
        resultMap.put("past6hRain", isEmptyPast6hRain ? "-" : (((float) past6hRain) / 10000) + "");
        resultMap.put("past12hRain", isEmptyPast12hRain ? "-" : (((float) past12hRain) / 10000) + "");
        resultMap.put("past24hRain", isEmptyPast24hRain ? "-" : (((float) past24hRain) / 10000) + "");
        resultMap.put("maxWindInPeriod", maxWindInPeriod);
        resultMap.put("maxTemInPeriod", maxTemInPeriod);
        resultMap.put("minTemInPeriod", minTemInPeriod);
        resultMap.put("totalRainInPeriod", totalRainInPeriod);
        return CommonResult.success(resultMap);
    }


    /**
     * 将时间戳转换为向下取整整点的时间戳
     *
     * @param sourceTimestamp 时间戳，秒为单位
     * @return 向下取整整点的时间戳，秒为单位
     */
    public long getIntegralTimestamp(long sourceTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(sourceTimestamp * 1000);
        // 这里是为了将传入的时间戳调整为最近整点的时间戳
        String sourceTimestampStr = sdf.format(date);
        String temSourceTimestampStr = sourceTimestampStr.substring(0, 13);
        temSourceTimestampStr += ":00:00";
        try {
            date = sdf.parse(temSourceTimestampStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timefull = date.getTime() / 1000;
        return timefull;
    }

    public int getIntegarHour(long sourceTimestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(sourceTimestamp * 1000);
        String sourceTimestampStr = sdf.format(date);
        return Integer.parseInt(sourceTimestampStr.substring(11, 13));
    }

    /**
     * 将时间按照时间段进行分割，得到每一段的个数
     *
     * @param hour
     * @return
     */
    public List<Integer> splitByPeriodOfTime(Integer hour) {
        int temNum = hour % 6 - 2;
        if (temNum < 0) {
            temNum += 6;
        }
        List<Integer> resultList = new ArrayList<>();
        if (temNum == 0) {
            resultList.add(0, 6);
            resultList.add(1, 6);
            resultList.add(2, 6);
            resultList.add(3, 6);
        } else {
            resultList.add(0, 6 - temNum);
            resultList.add(1, 6);
            resultList.add(2, 6);
            resultList.add(3, 6);
            resultList.add(4, temNum);
        }
        return resultList;
    }


    public void fillSegmentDatalist(List<Integer> groupNum, List<String> maxWindInPeriod, List<String> totalRainInPeriod, List<String> maxTemInPeriod,
                                    List<String> minTemInPeriod, List<String> winList, List<String> preList, List<String> temList) {
        int dataTotalCount = 0;
        int startIndex = 0;
        for (Integer dataCount : groupNum) {
            dataTotalCount += dataCount;
            String maxWinStr = "-";
            String maxTemStr = "-";
            String minTemStr = "-";
            String totalRainStr = "-";
            for (int i = startIndex; i < dataTotalCount; i++) {
                String temWinStr = winList.get(i);
                if (!"-".equals(temWinStr)) {
                    float temWin = Float.parseFloat(temWinStr);
                    if ("-".equals(maxWinStr)) {
                        maxWinStr = temWin + "";
                    } else {
                        float maxWin = Float.parseFloat(maxWinStr);
                        if (maxWin < temWin) {
                            maxWin = temWin;
                            maxWinStr = maxWin + "";
                        }
                    }
                }
            }
            for (int i = startIndex; i < dataTotalCount; i++) {
                String temTemStr = temList.get(i);
                if (!"-".equals(temTemStr)) {
                    float temTem = Float.parseFloat(temTemStr);
                    if ("-".equals(maxTemStr)) {
                        maxTemStr = temTem + "";
                    } else {
                        float maxTem = Float.parseFloat(maxTemStr);
                        if (maxTem < temTem) {
                            maxTem = temTem;
                            maxTemStr = maxTem + "";
                        }
                    }
                    if ("-".equals(minTemStr)) {
                        minTemStr = temTem + "";
                    } else {
                        float minTem = Float.parseFloat(minTemStr);
                        if (minTem > temTem) {
                            minTem = temTem;
                            minTemStr = minTem + "";
                        }
                    }
                }
            }
            for (int i = startIndex; i < dataTotalCount; i++) {
                String temRainStr = preList.get(i);
                if (!"-".equals(temRainStr)) {
                    float temRain = Float.parseFloat(temRainStr);
                    if ("-".equals(totalRainStr)) {
                        totalRainStr = temRain + "";
                    } else {
                        float totalRain = Float.parseFloat(totalRainStr);
                        totalRain = ((totalRain * 10000 + temRain * 10000)) / 10000;
                        totalRainStr = totalRain + "";
                    }
                }
            }
            maxWindInPeriod.add(maxWinStr);
            maxTemInPeriod.add(maxTemStr);
            minTemInPeriod.add(minTemStr);
            totalRainInPeriod.add(totalRainStr);
            startIndex += dataCount;
        }
    }
}