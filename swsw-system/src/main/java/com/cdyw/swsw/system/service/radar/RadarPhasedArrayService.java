package com.cdyw.swsw.system.service.radar;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ZipUtil;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import com.cdyw.swsw.system.dao.radar.RadarPhasedArrayMapper;
import com.cdyw.swsw.system.vo.common.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author jovi
 */
@Slf4j
@Service
public class RadarPhasedArrayService {

    private final TypeEnum TYPE = TypeEnum.TYPE_RADAR_PHASED_ARRAY;

    private final CommonDataParse commonDataParse;

    private final CommonTable commonTable;

    private final CommonTableName commonTableName;

    private final RadarPhasedArrayMapper radarPhasedArrayMapper;

    @Autowired
    public RadarPhasedArrayService(CommonDataParse commonDataParse, CommonTable commonTable, CommonTableName commonTableName, RadarPhasedArrayMapper radarPhasedArrayMapper) {
        this.commonDataParse = commonDataParse;
        this.commonTable = commonTable;
        this.commonTableName = commonTableName;
        this.radarPhasedArrayMapper = radarPhasedArrayMapper;
    }

    public CommonResult<?> getDataParseByTypeAndTime(String staIdC, String type, String startTime, String endTime) throws IOException {
        String staChongZhou = "00001";
        String staTianFu = "00002";
        String staXinDu = "00003";
        if (staChongZhou.equals(staIdC)) {
            staIdC = commonTable.getStaRadarPhasedArrayChongzhou();
        } else if (staTianFu.equals(staIdC)) {
            staIdC = commonTable.getStaRadarPhasedArrayTianfu();
        } else if (staXinDu.equals(staIdC)) {
            staIdC = commonTable.getStaRadarPhasedArrayXindu();
        } else {
            staIdC = commonTable.getStaRadarPhasedArray();
        }
        String t = commonTable.getSufParse();
        String time = DateUtils.getDayStringByTimestamp(Long.parseLong(startTime), "yyyyMMddHHmmss");
        String startTimeFormat = DateUtils.getDayStringByTimestamp(Long.parseLong(startTime), "yyyy/MM/dd HH:mm:ss");
        String endTimeFormat = DateUtils.getDayStringByTimestamp(Long.parseLong(endTime), "yyyy/MM/dd HH:mm:ss");
        // 先查询数据库
        String tableName = commonTableName.getTableNameByParam(TYPE, staIdC, time, t);
        Map<String, Object> map = radarPhasedArrayMapper.getDataParseByTypeAndTime(tableName, staIdC, type, Long.parseLong(startTime), Long.parseLong(endTime));
        CommonRadarParseTxt radarParseTxt = BeanUtil.mapToBean(map, CommonRadarParseTxt.class, true, CopyOptions.create());
        // 找到文件路径
        if (ObjectUtil.isEmpty(map)) {
            return CommonResult.failed("未查询到相关信息，起始时间为： " + startTimeFormat + " ，终止时间为： " + endTimeFormat);
        } else {
            return CommonResult.success(radarParseTxt);
        }
    }

    public void getFileParseByFileName(String staIdC, String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        commonDataParse.getPngByFileName(TypeEnum.TYPE_RADAR_PHASED_ARRAY, staIdC, type, fileName, response, isOnLine);
    }

    public void getBinaryFileByPara(String staIdC, String type, String startTime, String endTime, HttpServletResponse response, boolean isOnLine) throws IOException {
        Map<String, Object> fileNameByPara = commonDataParse.getFileNameByPara(TypeEnum.TYPE_RADAR_PHASED_ARRAY, staIdC, type, startTime, endTime, null, null);
        if (fileNameByPara.size() != 0) {
            String fileName = fileNameByPara.get("name").toString();

            Map<String, Object> pathMap = commonDataParse.getFilePathByPara(TypeEnum.TYPE_RADAR_PHASED_ARRAY, staIdC, type, fileName);
            String fileAllPath = pathMap.get("fileAllPath").toString();

            String fileAllPathAndName = fileAllPath + "/" + fileName;
            File zipFile = new File(fileAllPathAndName);
            File unzip = ZipUtil.unzip(zipFile);
            if (unzip.isDirectory()) {
                File[] files = unzip.listFiles();
                if (files != null) {
                    for (File file1 : files) {
                        zipFile = file1;
                    }
                }
            }
            FileUtil.downloadOrOnlineFile(new File(zipFile.getAbsolutePath()), response, isOnLine);
        }
    }
}
