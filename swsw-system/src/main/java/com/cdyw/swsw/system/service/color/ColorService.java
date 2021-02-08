package com.cdyw.swsw.system.service.color;

import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import com.cdyw.swsw.system.dao.dic.DicLevelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ColorService {

    @Autowired
    private CommonDataParse commonDataParse;
    @Autowired
    private DicLevelMapper dicLevelMapper;

    public CommonResult<?> getColorByProductType(String productType, String parentCode) {
        List<HashMap<String, Object>> colorList = null;
        if (!StrUtil.isEmpty(productType) && !StrUtil.isEmpty(parentCode)) {
            colorList = commonDataParse.getColorByProductType(productType, parentCode);
        }
        if (StrUtil.isEmpty(productType)) {
            Map<String, Object> resultMap = new HashMap<>();
            List<HashMap<String, Object>> productCodeList = dicLevelMapper.selectByParentcodeAndLevel(parentCode, 3);
            for (HashMap<String, Object> temMap : productCodeList) {
                String productCode = (String) temMap.get("code");
                List<HashMap<String, Object>> colorByProductType = commonDataParse.getColorByProductType(productCode, parentCode);
                resultMap.put(productCode, colorByProductType);
            }
            if (resultMap != null) {
                return CommonResult.success(resultMap);
            } else {
                return CommonResult.failed();
            }
        }
        if (colorList != null) {
            return CommonResult.success(colorList);
        } else {
            return CommonResult.failed();
        }
    }

}
