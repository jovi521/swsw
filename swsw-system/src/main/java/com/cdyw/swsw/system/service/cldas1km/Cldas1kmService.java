package com.cdyw.swsw.system.service.cldas1km;

import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.common.util.FileUtils;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.dao.cldas1km.Cldas1kmMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Cldas1kmService {

    @Autowired
    private Cldas1kmMapper cldas1kmMapper;
    @Autowired
    private CommonPath commonPath;
    @Autowired
    private CommonTable commonTable;

    public CommonResult<?> getDataByParam(List<String> productType, String time) {
        if (productType == null || productType.size() == 0) {
            return CommonResult.failed("传入的参数不能为空！");
        }
        // 获得查询开始时间，结束时间
        long endTime = Long.parseLong(time);
        long startTime = endTime - 3599;
        String timeStr = DateUtils.getDateToString(endTime * 1000);
        String yyyyMM = timeStr.substring(0, 6);
        Integer[] productCode = new Integer[productType.size()];
        for (int i = 0; i < productType.size(); i++) {
            productCode[i] = Integer.parseInt(productType.get(i));
        }
        // 获得将要查询的表
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeCldas1km() + commonTable.getLink()
                + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        List<Map<String, Object>> dataList = cldas1kmMapper.selectByParam(tablename, productCode, startTime, endTime);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map<String, Object> temMap : dataList) {
            Map<String, Object> dataMap = new HashMap<>();
            String filename = (String) temMap.get("name");
            String posFile = (String) temMap.get("pos_file");
            int type = (Integer) temMap.get("type");
            // 获得完整路径
            String totalPath = commonPath.getDisk() + commonPath.getCommonPath() + posFile + filename;
            String content = "";
            try {
                content = FileUtils.readCharByPath(totalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String product = "";
            if (type == 1903) {
                product = "rain1h";
            } else if (type == 1901) {
                product = "tem";
            }
            dataMap.put("type", product);
            dataMap.put("data", JSONUtil.parseObj(content));
            resultList.add(dataMap);
        }
        return CommonResult.success(resultList);

    }
}
