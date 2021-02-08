package com.cdyw.swsw.system.service.radar;

import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.util.Base64Convert;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import com.cdyw.swsw.system.dao.radar.RadarExtraMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cdyw.swsw.common.domain.vo.result.CommonResult.success;

/**
 * @author jovi
 */
@Slf4j
@Service
public class RadarExtraService {

    private final CommonDataParse commonDataParse;

    private final CommonTable commonTable;

    private final RadarExtraMapper radarExtraMapper;

    private final CommonPath commonPath;
    @Autowired
    public RadarExtraService(CommonDataParse commonDataParse, CommonTable commonTable, RadarExtraMapper radarExtraMapper, CommonPath commonPath) {
        this.commonDataParse = commonDataParse;
        this.commonTable = commonTable;
        this.radarExtraMapper = radarExtraMapper;
        this.commonPath = commonPath;
    }

    public CommonResult<?> getMcode(String staIdC, String type, String startTime, String endTime) {
        if (staIdC == null) {
            staIdC = commonTable.getStaRadarExt();
        }
        List<String> mcodes = commonDataParse.getMcode(TypeEnum.TYPE_RADAR_EXT, staIdC, type, startTime, endTime);
        if (mcodes.size() == 0) {
            return CommonResult.failed();
        } else {
            return success(mcodes);
        }
    }

    public CommonResult<?> getDataParseByTypeAndTime(String type, String startTime, String endTime, String mcode) throws IOException {
        String staIdC = commonTable.getStaRadarExt();
        CommonRadarParseTxt txt = commonDataParse.getRadarExtTxtByPara(TypeEnum.TYPE_RADAR_EXT, staIdC, type, startTime, endTime, mcode);
        if (txt == null) {
            return CommonResult.failed();
        } else {
            return success(txt);
        }
    }

    public void getFileParseByFileName(String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        String staIdC = commonTable.getStaRadarExt();
        commonDataParse.getPngByFileName(TypeEnum.TYPE_RADAR_EXT, staIdC, type, fileName, response, isOnLine);
    }

    public CommonResult<Map<String, Object>> getDataParseByParam(String type, String time, String layer){
        long queryTime = Long.parseLong(time);
        String yyyyMMddHHmmss = DateUtils.getDateToString(queryTime * 1000);
        // 获得上个月的时间
        String yyyyMMddHHmmssOneMonthAgo = DateUtils.offsetMonths(yyyyMMddHHmmss, -1);
        String yyyyMM = yyyyMMddHHmmss.substring(0, 6);
        String yyyyMMOneMonthAgo = yyyyMMddHHmmssOneMonthAgo.substring(0, 6);
        // 根据type找到要查询的表
        String tableName;
        String tableNameOneMonthAgo;
        if (type.startsWith("26")){
            tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeWeatherRadarExtrapolation() + commonTable.getLink() +
                    commonTable.getStaWeatherRadarExtrapolation() + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
            tableNameOneMonthAgo = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeWeatherRadarExtrapolation() + commonTable.getLink() +
                    commonTable.getStaWeatherRadarExtrapolation() + commonTable.getLink() + yyyyMMOneMonthAgo + commonTable.getLink() + commonTable.getSufParse();
        }else if (type.startsWith("27")){
            tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypePhasedarrayRadarExtrapolation() + commonTable.getLink() +
                    commonTable.getStaPhasedarrayRadarExtrapolation() + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
            tableNameOneMonthAgo = commonTable.getPre() + commonTable.getLink() + commonTable.getTypePhasedarrayRadarExtrapolation() + commonTable.getLink() +
                    commonTable.getStaPhasedarrayRadarExtrapolation() + commonTable.getLink() + yyyyMMOneMonthAgo + commonTable.getLink() + commonTable.getSufParse();
        }else {
            return CommonResult.failed("参数有误，请重新输入。。");
        }
        // 查询最新的起报时间
        Map<String, Object> newestTimeMap = radarExtraMapper.selectMaxTime(tableName, tableNameOneMonthAgo);
        if (newestTimeMap == null || newestTimeMap.size() == 0){
            return CommonResult.failed("无数据。。");
        }
        long baseTime = (Long)newestTimeMap.get("maxTime");
        String yyyyMMddHHmmssBaseTime = DateUtils.getDateToString(baseTime * 1000);
        String yyyyMMBaseTime = yyyyMMddHHmmssBaseTime.substring(0, 6);
        // 真正查询的表
        String tablenameBaseTime = "";
        if (type.startsWith("26")){
            tablenameBaseTime = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeWeatherRadarExtrapolation() + commonTable.getLink() +
                    commonTable.getStaWeatherRadarExtrapolation() + commonTable.getLink() + yyyyMMBaseTime + commonTable.getLink() + commonTable.getSufParse();
        }else if (type.startsWith("27")) {
            tablenameBaseTime = commonTable.getPre() + commonTable.getLink() + commonTable.getTypePhasedarrayRadarExtrapolation() + commonTable.getLink() +
                    commonTable.getStaPhasedarrayRadarExtrapolation() + commonTable.getLink() + yyyyMMBaseTime + commonTable.getLink() + commonTable.getSufParse();
        }
        // 拼接mcode(间隔为6分钟)
        String mcode = type + "_" + ((queryTime - baseTime) % (6*60) == 0 ? (queryTime - baseTime) / (6*60) * 6 : ((queryTime - baseTime) / (6*60) + 1) * 6);
        Map<String, Object> dataMap = radarExtraMapper.selectByParam(tablenameBaseTime, baseTime, Integer.parseInt(type), Integer.parseInt(layer), mcode);
        String filename = (String)dataMap.get("name");
        String relativePath = (String)dataMap.get("pos_file");
        Float lonMin = (Float)dataMap.get("lon_min");
        Float lonMax = (Float)dataMap.get("lon_max");
        Float latMin = (Float)dataMap.get("lat_min");
        Float latMax = (Float)dataMap.get("lat_max");
        String totalFilepath = commonPath.getDisk() + commonPath.getCommonPath() + relativePath + filename;
        String imgBase64 = Base64Convert.GetImageStr(totalFilepath);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("lonMin", lonMin);
        resultMap.put("lonMax", lonMax);
        resultMap.put("latMin", latMin);
        resultMap.put("latMax", latMax);
        resultMap.put("img", imgBase64);
        return CommonResult.success(resultMap);
    }

}
