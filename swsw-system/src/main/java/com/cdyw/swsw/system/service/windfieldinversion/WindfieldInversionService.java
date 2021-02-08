package com.cdyw.swsw.system.service.windfieldinversion;

import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.common.util.FileUtils;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.dao.windfieldinversion.WindfieldInversionMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class WindfieldInversionService {

    private final CommonTable commonTable;

    private final CommonPath commonPath;

    private final WindfieldInversionMapper windfieldInversionMapper;

    public WindfieldInversionService(CommonTable commonTable, CommonPath commonPath, WindfieldInversionMapper windfieldInversionMapper) {
        this.commonTable = commonTable;
        this.commonPath = commonPath;
        this.windfieldInversionMapper = windfieldInversionMapper;
    }

    public CommonResult<?> getDataByTypeAndTime(String type, String time, String layer) {
        long endTime = Long.parseLong(time);
        long startTime = endTime - 359;
        String yyyyMMddHHmmss = DateUtils.getDateToString(Long.parseLong(time) * 1000);
        String yyyyMM = yyyyMMddHHmmss.substring(0, 6);
        // 拼接得到表名
        String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getType3dWindfieldInversion() + commonTable.getLink()
                + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        List<Map<String, Object>> queryList;
        if (!StringUtils.isEmpty(layer)) {
            queryList = windfieldInversionMapper.selectByTypeAndTimeAndLayer(Integer.parseInt(type),
                    endTime, startTime, Integer.parseInt(layer), tableName);
        } else {
            queryList = windfieldInversionMapper.selectByTypeAndTimeAndLayer(Integer.parseInt(type),
                    endTime, startTime, null, tableName);
        }
        if (queryList == null || queryList.size() == 0) {
            return CommonResult.failed("未查询到数据。。");
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> temMap : queryList) {
            String posFile = (String) temMap.get("pos_file");
            String filename = (String) temMap.get("name");
            // 拼接得到全路径
            String totalPath = commonPath.getDisk() + commonPath.getCommonPath() + posFile + filename;
            // 得到文件内容
            String content = "";
            try {
                content = FileUtils.readCharByPath(totalPath);
            } catch (IOException e) {
                e.printStackTrace();
                content = null;
            }
            Map<String, Object> fileMap = (Map<String, Object>) JSONUtil.parse(content);
            resultList.add(fileMap);
        }
        return CommonResult.success(resultList);
    }

}
