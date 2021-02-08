package com.cdyw.swsw.system.service.forecastcheck;

import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.util.TimeStampUtil;
import com.cdyw.swsw.system.dao.forecastcheck.ForecastCheckMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author cdyw
 * @description
 */
@Service
public class ForecastCheckService {

    @Autowired
    private CommonTable commonTable;
    @Autowired
    private ForecastCheckMapper forecastCheckMapper;


    public CommonResult<?> getDataByParam(String categoryId, String evaluateId,
                                          String productId, Long startTime,
                                          Long endTime, Integer timeCount,
                                          Float threshold) {
        String category = getCategory(categoryId);
        String evaluateType = getEvaluateType(evaluateId);
        String productType = getProductType(productId);
        // 先找到需要查找的表名
        String tablename1 = getTablename(startTime * 1000, category, evaluateType, productType);
        String tablename2 = getTablename(endTime * 1000, category, evaluateType, productType);
        // 组装需要查询的起始时间
        startTime = startTime + timeCount * 3600;
        endTime = endTime + timeCount * 3600;
        List<Long> queryTimeList = new ArrayList<>();
        // 获得数据的组数
        int dayCount = (int) ((endTime - startTime) / (24 * 3600));
        for (int i = 0; i <= dayCount; i++) {
            queryTimeList.add(startTime + i * 24 * 3600);
        }
        List<HashMap<String, Object>> queryList;
        // 查询
        if (tablename1.equals(tablename2)) {
            queryList = forecastCheckMapper.selectByThresholdAndTimeperiod(tablename1, null, queryTimeList, threshold);
        } else {
            queryList = forecastCheckMapper.selectByThresholdAndTimeperiod(tablename1, tablename2, queryTimeList, threshold);
        }
        if (queryList != null && queryList.size() > 0) {
            // 获得每一组的个数
            int fcstCount = ((Long) queryList.get(0).get("fcst_count")).intValue();
            // 创一个保存累计值的数组
            Float temValueArray[] = new Float[fcstCount];
            for (int i = 0; i < queryList.size(); i++) {
                HashMap<String, Object> temHashmap = queryList.get(i);
                Float value = (Float) temHashmap.get("value");
                Integer temIndex = i % fcstCount;
                if (temValueArray[temIndex] == null) {
                    temValueArray[temIndex] = 0.0f;
                }
                temValueArray[temIndex] = temValueArray[temIndex] + value;
            }
            Integer groupCount = queryList.size() / fcstCount;
            Float realValueArray[] = new Float[fcstCount];
            for (int j = 0; j < fcstCount; j++) {
                realValueArray[j] = (Math.round(temValueArray[j] / groupCount * 1000) / 1000f);
            }
            List<HashMap<String, Object>> resultList = new ArrayList<>();
            for (int k = 0; k < fcstCount; k++) {
                HashMap<String, Object> resultMap = new HashMap<>();
                resultMap.put("fcstHour", k);
                resultMap.put("value", realValueArray[k]);
                resultList.add(resultMap);
            }
            return CommonResult.success(resultList);
        }
        return CommonResult.failed("未查询到数据。。");
    }


    public String getCategory(String categoryId) {
        switch (categoryId) {
            case "16":
                return TypeEnum.TYPE_MODE_DATA.getName().replaceAll("_", "").toLowerCase();
            case "17":
                return TypeEnum.TYPE_CORRECTION_DATA.getName().replaceAll("_", "").toLowerCase();
            case "18":
                return TypeEnum.TYPE_WARNING_SIGNAL_AREA.getName().replaceAll("_", "").toLowerCase();
        }
        return null;
    }

    public String getEvaluateType(String evaluateId) {
        switch (evaluateId) {
            case "61":
                return commonTable.getStaForecastCheckTs();
            case "62":
                return commonTable.getStaForecastCheckSkill();
            case "63":
                return commonTable.getStaForecastCheckMissingreport();
            case "64":
                return commonTable.getStaForecastCheckNoreport();
        }
        return null;
    }

    public String getProductType(String productId) {
        switch (productId) {
            case "1001":
                return commonTable.getTypeForecastCheckRain();
            case "1002":
                return commonTable.getTypeForecastCheckTem();
            case "1003":
                return commonTable.getTypeForecastCheckWinddirect();
            case "1004":
                return commonTable.getTypeForecastCheckWindspeed();
        }
        return null;
    }

    public String getTablename(Long startTime, String category, String evaluateType, String productType) {
        String yyyy = TimeStampUtil.timeStampToTimeStr(startTime, "yyyy");
        String tableName = commonTable.getPre() + commonTable.getLink() + category + commonTable.getLink() + evaluateType +
                commonTable.getLink() + productType + commonTable.getLink() + yyyy + commonTable.getLink() + commonTable.getSufParse();
        return tableName;
    }


}
