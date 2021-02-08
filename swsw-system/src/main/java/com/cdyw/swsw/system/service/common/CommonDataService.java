package com.cdyw.swsw.system.service.common;

import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.ao.enums.StatusEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.dao.common.CommonDataMapper;
import com.cdyw.swsw.system.dao.user.SysUserMapper;
import com.cdyw.swsw.system.security.util.AuthenticationUtil;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一使用redis配置类中的key生成策略和其他配置
 *
 * @author jovi
 */
@CacheConfig(keyGenerator = "keyGenerator")
@Service
public class CommonDataService {

    private final CommonDataMapper commonDataMapper;

    private final CommonTableName commonTableName;

    private final CommonUrlParam commonUrlParam;

    private final CommonTable commonTable;

    private final CommonPath commonPath;

    private final AuthenticationUtil authenticationUtil;

    private final SysUserMapper sysUserMapper;

    public CommonDataService(CommonDataMapper commonDataMapper, CommonTableName commonTableName, CommonUrlParam commonUrlParam, CommonTable commonTable, CommonPath commonPath,
                             AuthenticationUtil authenticationUtil, SysUserMapper sysUserMapper) {
        this.commonDataMapper = commonDataMapper;
        this.commonTableName = commonTableName;
        this.commonUrlParam = commonUrlParam;
        this.commonTable = commonTable;
        this.commonPath = commonPath;
        this.authenticationUtil = authenticationUtil;
        this.sysUserMapper = sysUserMapper;
    }

    @Cacheable(value = {"dataMonitor"})
    public CommonResult<?> getDataMonitor() {
        String denominatorStr = commonUrlParam.getDenominator();
        int denominator = Integer.parseInt(denominatorStr);
        List<Map<String, Object>> mapList = new ArrayList<>(3);
        String keyType = "type";
        String keyName = "name";
        String keyMolecular = "molecular";
        String keyDenominator = "denominator";
        String keyStatus = "status";

        String nowFormat = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String t = commonTable.getSufBase();
        String tableNameByTypeAtSta = commonTableName.getTableNameByParam(TypeEnum.TYPE_AT_STATION, commonPath.getAtstationStaAll(), nowFormat, t);
        String tableNameByTypeSate = commonTableName.getTableNameByParam(TypeEnum.TYPE_SATELLITE, commonPath.getSatelliteStaFY4A(), nowFormat, t);
        String tableNameByTypeRadarWea = commonTableName.getTableNameByParam(TypeEnum.TYPE_RADAR_WEATHER, commonPath.getRadarWeaStaZ9280(), nowFormat, t);

        Map<String, Object> dataMonitorAtSta = commonDataMapper.getDataMonitorAtSta(tableNameByTypeAtSta);
        if (dataMonitorAtSta != null) {
            dataMonitorAtSta.put(keyType, TypeEnum.TYPE_AT_STATION.getType());
            dataMonitorAtSta.put(keyName, TypeEnum.TYPE_AT_STATION.getName());
            Object molecularObj = dataMonitorAtSta.get("molecular");
            int molecular = Integer.parseInt(molecularObj.toString());
            dataMonitorAtSta.put(keyMolecular, molecular);
            dataMonitorAtSta.put(keyDenominator, denominator);
            if (molecular >= denominator) {
                dataMonitorAtSta.put(keyStatus, StatusEnum.NORMAL.getValue());
            } else {
                dataMonitorAtSta.put(keyStatus, StatusEnum.TIMEOUT.getValue());
            }
        }

        Map<String, Object> dataMonitorSate = commonDataMapper.getDataMonitorSate(tableNameByTypeSate);
        if (dataMonitorSate != null) {
            dataMonitorSate.put(keyType, TypeEnum.TYPE_SATELLITE.getType());
            dataMonitorSate.put(keyName, TypeEnum.TYPE_SATELLITE.getName());
            if (!dataMonitorSate.values().isEmpty()) {
                dataMonitorSate.put(keyStatus, StatusEnum.NORMAL.getValue());
            } else {
                dataMonitorSate.put(keyStatus, StatusEnum.TIMEOUT.getValue());
            }
        }

        Map<String, Object> dataMonitorRadarWea = commonDataMapper.getDataMonitorRadarWea(tableNameByTypeRadarWea);
        if (dataMonitorRadarWea != null) {
            dataMonitorRadarWea.put(keyType, TypeEnum.TYPE_RADAR_WEATHER.getType());
            dataMonitorRadarWea.put(keyName, TypeEnum.TYPE_RADAR_WEATHER.getName());
            if (!dataMonitorRadarWea.values().isEmpty()) {
                dataMonitorRadarWea.put(keyStatus, StatusEnum.NORMAL.getValue());
            } else {
                dataMonitorRadarWea.put(keyStatus, StatusEnum.TIMEOUT.getValue());
            }
        }

        mapList.add(dataMonitorAtSta);
        mapList.add(dataMonitorSate);
        mapList.add(dataMonitorRadarWea);
        if (dataMonitorAtSta != null || dataMonitorSate != null || dataMonitorRadarWea != null) {
            return CommonResult.success(mapList);
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 定时查询三种产品所有的信息返回给前端
     *
     * @return CommonResult
     */
    @Cacheable(value = "dataMonitorAlarm")
    public CommonResult<?> getDataMonitorAlarm() {
        Map<String, Object> resultMap = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime localDateTimeMinus24 = localDateTime.minusMonths(1);

        String tableNameByTypeAtSta = commonTableName.getTableNameByParam(TypeEnum.TYPE_AT_STATION, commonPath.getAtstationStaAll(), localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), commonTable.getSufBase());
        String tableNameByTypeAtStaMonthAgo = commonTableName.getTableNameByParam(TypeEnum.TYPE_AT_STATION, commonPath.getAtstationStaAll(), localDateTimeMinus24.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")), commonTable.getSufBase());

        // 获得用户信息，查询用户的阈值
        SysUserEntity currentUser = authenticationUtil.getCurrentUser();
        int userId = currentUser.getUserId();
        List<Map<String, Object>> thresholdList = sysUserMapper.selectThresholdByUserid(userId);

        for (Map<String, Object> temMap : thresholdList) {
            int productType = (Integer) temMap.get("type");
            int flag = (Integer) temMap.get("flag");
            List<Map<String, Object>> resultList = commonDataMapper.selectAtstationNewest(tableNameByTypeAtSta, productType, flag);
            if (resultList == null || resultList.size() == 0) {
                resultList = commonDataMapper.selectAtstationNewest(tableNameByTypeAtStaMonthAgo, productType, flag);
            }
            switch (productType) {
                case 603:
                    resultMap.put("tem", resultList);
                    break;
                case 605:
                    resultMap.put("rain", resultList);
                    break;
                case 607:
                    resultMap.put("windspeed", resultList);
                    break;
            }
            int num = 1;
            for (Map<String, Object> temResult : resultList) {
                temResult.put("num", num);
                num++;
            }
        }
        return CommonResult.success(resultMap);
    }

    /**
     * 查询用户设置的阈值
     *
     * @return CommonResult
     */
    @Cacheable(value = "getThreshold")
    public CommonResult<?> getThreshold(String userId) {
        int userid = Integer.parseInt(userId);
        List<Map<String, Object>> thresholdList = sysUserMapper.selectThresholdByUserid(userid);
        return CommonResult.success(thresholdList);
    }


    public CommonResult<?> modifyThreshold(@RequestParam("userId") String userId, @RequestParam("type") String type,
                                           @RequestParam("threshold") String threshold, @RequestParam("flag") String flag) {

        return null;

    }


}
