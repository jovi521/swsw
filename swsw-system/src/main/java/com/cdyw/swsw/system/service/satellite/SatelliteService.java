package com.cdyw.swsw.system.service.satellite;

import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.util.Base64Convert;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.SatelliteFY4AParseTxt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import com.cdyw.swsw.system.dao.satellite.SatelliteMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jovi
 */
@Slf4j
@Service
public class SatelliteService {

    private Logger logger = LoggerFactory.getLogger(SatelliteService.class);

    private final CommonDataParse commonDataParse;

    private final SatelliteMapper satelliteMapper;

    private final CommonTable commonTable;

    private final CommonPath commonPath;

    @Autowired
    public SatelliteService(CommonDataParse commonDataParse, SatelliteMapper satelliteMapper,
                            CommonTable commonTable, CommonPath commonPath) {
        this.commonDataParse = commonDataParse;
        this.satelliteMapper = satelliteMapper;
        this.commonTable = commonTable;
        this.commonPath = commonPath;
    }

    public CommonResult<?> getDataParseByTypeAndTime(String type, String startTime, String endTime) throws IOException {
        String staIdC = commonTable.getStaSatelliteFy4a();
        SatelliteFY4AParseTxt txt = commonDataParse.getSatelliteTxtByPara(TypeEnum.TYPE_SATELLITE, staIdC, type, startTime, endTime);
        if (txt == null) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(txt);
        }
    }

    public void getFileParseByFileName(String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        String staIdC = commonTable.getStaSatelliteFy4a();
        commonDataParse.getSatellitePngByFileName(TypeEnum.TYPE_SATELLITE, staIdC, type, fileName, response, isOnLine);
    }

    public CommonResult<?> getDataParseByParam(String type, String time) throws IOException {
        long endTime = Long.parseLong(time);
        long startTime = endTime - 6 * 60;
        String yyyyMMddHHmmss = DateUtils.getDateToString(startTime * 1000);
        String yyyyMMdd = yyyyMMddHHmmss.substring(0, 8);
        String yyyyMM = yyyyMMddHHmmss.substring(0, 6);
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeSatellite() + commonTable.getLink() + commonTable.getStaSatelliteFy4a()
                + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        logger.info("tablename:" + tablename);
        Map<String, Object> dataMap = satelliteMapper.getDataParseByParam(tablename, Integer.parseInt(type), startTime, endTime);
        logger.info("dataMap:" + dataMap);
        if(dataMap != null && dataMap.size() > 0){
            Map<String, Object> resultMap = new HashMap<>();
            String filename = (String)dataMap.get("name");
            String posFile = (String)dataMap.get("pos_file");
            double lonMin = Double.parseDouble((String)dataMap.get("lon_min"));
            double lonMax = Double.parseDouble((String)dataMap.get("lon_max"));
            double latMin = Double.parseDouble((String)dataMap.get("lat_min"));
            double latMax = Double.parseDouble((String)dataMap.get("lat_max"));
            String totalFilepath = commonPath.getDisk() + commonPath.getCommonPath() + posFile + filename;
            logger.info("totalFilepath:" + totalFilepath);
            String imgBase64 = Base64Convert.GetImageStr(totalFilepath);
            resultMap.put("lonMin", lonMin);
            resultMap.put("lonMax", lonMax);
            resultMap.put("latMin", latMin);
            resultMap.put("latMax", latMax);
            resultMap.put("img", imgBase64);
            return CommonResult.success(resultMap);
        }
        return CommonResult.success("未查询到数据");
    }

}
