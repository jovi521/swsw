package com.cdyw.swsw.system.app.job.radar;

import cn.hutool.core.date.DateUtil;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.ao.enums.StatusEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.system.dao.common.CommonDataMapper;
import com.cdyw.swsw.system.dao.log.DataMonitorLogMapper;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author jovi
 */
@Component
public class RadarWeaDataJobHandler extends IJobHandler {

    private final CommonDataMapper commonDataMapper;

    private final CommonTableName commonTableName;

    private final CommonUrlParam commonUrlParam;

    private final DataMonitorLogMapper dataMonitorLogMapper;

    private final CommonTable commonTable;

    @Autowired
    public RadarWeaDataJobHandler(CommonDataMapper commonDataMapper, CommonTableName commonTableName, CommonUrlParam commonUrlParam, DataMonitorLogMapper dataMonitorLogMapper, CommonTable commonTable) {
        this.commonDataMapper = commonDataMapper;
        this.commonTableName = commonTableName;
        this.commonUrlParam = commonUrlParam;
        this.dataMonitorLogMapper = dataMonitorLogMapper;
        this.commonTable = commonTable;
    }

    public static int getMolecular(int denominator, DataMonitorLog dataMonitorLog, Map<String, Object> dataMonitorRadarWea) {
        int molecular;
        LocalDateTime localDateTime = DateUtil.parseLocalDateTime(dataMonitorRadarWea.get("date_time").toString());
        dataMonitorLog.setDateTime(localDateTime);

        Object molecularObj = dataMonitorRadarWea.get("molecular");
        molecular = Integer.parseInt(molecularObj.toString());
        dataMonitorLog.setMolecular(molecular);

        dataMonitorLog.setDenominator(denominator);
        return molecular;
    }

    /**
     * 将被执行的自动站相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "radarWeaDataJobHandler")
    @Override
    public ReturnT<String> execute(String param) {
        String denominatorStr = commonUrlParam.getDenominator();
        int denominator = Integer.parseInt(denominatorStr);
        int molecular;
        List<String> tableNameByTypeRadarWea = commonTableName.getTableNamesByType(TypeEnum.TYPE_RADAR_WEATHER);

        DataMonitorLog dataMonitorLog = new DataMonitorLog();
        Map<String, Object> dataMonitorRadarWea = commonDataMapper.getDataMonitorRadarWea(tableNameByTypeRadarWea.get(0));
        if (dataMonitorRadarWea != null) {
            molecular = getMolecular(denominator, dataMonitorLog, dataMonitorRadarWea);

            dataMonitorLog.setType(TypeEnum.TYPE_RADAR_WEATHER.getType());

            if (molecular >= denominator) {
                dataMonitorLog.setStatus(StatusEnum.NORMAL.getValue());
            } else {
                dataMonitorLog.setStatus(StatusEnum.TIMEOUT.getValue());
            }
            dataMonitorLog.setCreateTime(LocalDateTime.now());
            dataMonitorLogMapper.insert(dataMonitorLog);
        }
        return ReturnT.SUCCESS;
    }
}
