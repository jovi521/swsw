package com.cdyw.swsw.system.service.radarextrapolation;

import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.util.Base64Convert;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.radarextrapolation.RadarExtrapolation;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.dao.radarextrapolation.RadarExtraplationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RadarExtraplationService {


    @Autowired
    private CommonTableName commonTableName;
    @Autowired
    private RadarExtraplationMapper radarExtraplationMapper;
    @Autowired
    private CommonPath commonPath;

    public CommonResult<?> getDataByParam(String parentCode, String stationId, String productType, String time) {
        // 将时间戳转为yyyyMMddHHmmss
        String yyyyMMddHHmmss = DateUtils.getDateToString(Long.parseLong(time) * 1000);
        String tableName;
        String basemapCode;
        if ("26".equals(parentCode)) {
            tableName = commonTableName.getTableNameByParam(TypeEnum.TYPE_WEATHER_RADAR_EXTRAPOLATION, stationId, yyyyMMddHHmmss, "parse");
            // 将地图底图转换转化为base64编码
            basemapCode = Base64Convert.GetImageStr(this.getClass().getResourceAsStream(commonPath.getWeatherRadarExtrapolationBasemap()));
        } else if ("27".equals(parentCode)) {
            tableName = commonTableName.getTableNameByParam(TypeEnum.TYPE_PHASEDARRAY_RADAR_EXTRAPOLATION, stationId, yyyyMMddHHmmss, "parse");
            // 将地图底图转换转化为base64编码
            basemapCode = Base64Convert.GetImageStr(this.getClass().getResourceAsStream(commonPath.getWeatherRadarExtrapolationBasemap()));
        } else {
            return CommonResult.failed("参数parentCode传入有误！");
        }
        List<RadarExtrapolation> radarExtrapolationList = radarExtraplationMapper.selectDataByParam(tableName, Integer.parseInt(productType), Long.parseLong(time));
        if (radarExtrapolationList == null || radarExtrapolationList.size() == 0) {
            return CommonResult.failed("传入参数有误或未查询到数据！");
        }
        // 获得最大最小经纬度
        Double lonMax = radarExtrapolationList.get(0).getLonMax();
        Double lonMin = radarExtrapolationList.get(0).getLonMin();
        Double latMax = radarExtrapolationList.get(0).getLatMax();
        Double latMin = radarExtrapolationList.get(0).getLatMin();
        // 获得数据的起报时间
        Long forecastTime = radarExtrapolationList.get(0).getTime();
        Map<String, Object> dataMap = new HashMap<>();
        List<Map<String, Object>> neuralNetwork = new ArrayList<>();
        List<Map<String, Object>> pyramidOpticalFlowMethod = new ArrayList<>();
        List<Map<String, Object>> semiLagrangian = new ArrayList<>();
        // 遍历数据列表
        for (RadarExtrapolation radarExtrapolation : radarExtrapolationList) {
            // 通过layer值判断属于什么方法
            Integer layer = radarExtrapolation.getLayer();
            // 通过mcode码判断是第几次外推
            String mcode = radarExtrapolation.getMcode();
            String posFile = radarExtrapolation.getPosFile();
            String filename = radarExtrapolation.getName();
            String totalFilename = commonPath.getDisk() + commonPath.getCommonPath() + posFile + filename;
            int extrapolationCount = Integer.parseInt(mcode.split("_")[1]);
            if (layer == 0) {
                Map<String, Object> temMap = new HashMap<>();
                temMap.put("type", extrapolationCount % 12 == 0 ? 12 : 6);
                // 获得文件的base64编码
                String base64Code = Base64Convert.GetImageStr(totalFilename);
                temMap.put("img", base64Code);
                neuralNetwork.add(temMap);
            } else if (layer == 1) {
                Map<String, Object> temMap = new HashMap<>();
                temMap.put("type", extrapolationCount % 12 == 0 ? 12 : 6);
                // 获得文件的base64编码
                String base64Code = Base64Convert.GetImageStr(totalFilename);
                temMap.put("img", base64Code);
                pyramidOpticalFlowMethod.add(temMap);
            } else if (layer == 2) {
                Map<String, Object> temMap = new HashMap<>();
                temMap.put("type", extrapolationCount % 12 == 0 ? 12 : 6);
                // 获得文件的base64编码
                String base64Code = Base64Convert.GetImageStr(totalFilename);
                temMap.put("img", base64Code);
                semiLagrangian.add(temMap);
            }
        }
        dataMap.put("lonMax", lonMax);
        dataMap.put("lonMin", lonMin);
        dataMap.put("latMax", latMax);
        dataMap.put("latMin", latMin);
        dataMap.put("basemap", basemapCode);
        dataMap.put("forecastTime", forecastTime);
        dataMap.put("neuralNetwork", neuralNetwork);
        dataMap.put("pyramidOpticalFlowMethod", neuralNetwork);
        dataMap.put("semiLagrangian", neuralNetwork);
        return CommonResult.success(dataMap);
    }


}
