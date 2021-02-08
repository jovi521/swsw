package com.cdyw.swsw.system.app.job.satellite;

import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.component.CommonUrlParam;
import com.cdyw.swsw.common.domain.ao.enums.StatusEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.system.app.job.radar.RadarWeaDataJobHandler;
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
public class SatelliteDataJobHandler extends IJobHandler {

    private final CommonDataMapper commonDataMapper;

    private final CommonTableName commonTableName;

    private final CommonUrlParam commonUrlParam;

    private final DataMonitorLogMapper dataMonitorLogMapper;

    private final CommonTable commonTable;

    @Autowired
    public SatelliteDataJobHandler(CommonDataMapper commonDataMapper, CommonTableName commonTableName, CommonUrlParam commonUrlParam, DataMonitorLogMapper dataMonitorLogMapper, CommonTable commonTable) {
        this.commonDataMapper = commonDataMapper;
        this.commonTableName = commonTableName;
        this.commonUrlParam = commonUrlParam;
        this.dataMonitorLogMapper = dataMonitorLogMapper;
        this.commonTable = commonTable;
    }

    public static ReturnT<String> getStringReturnT(int denominator, DataMonitorLog dataMonitorLog, Map<String, Object> dataMonitorRadarWea, DataMonitorLogMapper dataMonitorLogMapper) {
        int molecular;
        if (dataMonitorRadarWea != null) {
            molecular = RadarWeaDataJobHandler.getMolecular(denominator, dataMonitorLog, dataMonitorRadarWea);

            dataMonitorLog.setType(TypeEnum.TYPE_SATELLITE.getType());

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

    /**
     * 将被执行的自动站相关的接口
     *
     * @param param 可以接收页面传来的参数（非必选）
     * @return ReturnT<String>
     */
    @XxlJob(value = "satelliteDataJobHandler")
    @Override
    public ReturnT<String> execute(String param) {
        String denominatorStr = commonUrlParam.getDenominator();
        int denominator = Integer.parseInt(denominatorStr);
        List<String> tableNameByTypeSate = commonTableName.getTableNamesByType(TypeEnum.TYPE_SATELLITE);

        DataMonitorLog dataMonitorLog = new DataMonitorLog();
        Map<String, Object> dataMonitorSate = commonDataMapper.getDataMonitorSate(tableNameByTypeSate.get(0));
        return getStringReturnT(denominator, dataMonitorLog, dataMonitorSate, dataMonitorLogMapper);
    }
}
