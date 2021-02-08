package com.cdyw.swsw.system.service.shortforecast;

import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.ShortForecastParseTxt;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cdyw
 * @description
 */
@Service
public class ShortForecastService {


    private final CommonDataParse commonDataParse;

    private final CommonTable commonTable;

    @Autowired
    public ShortForecastService(CommonDataParse commonDataParse, CommonTable commonTable) {
        this.commonDataParse = commonDataParse;
        this.commonTable = commonTable;
    }

    public CommonResult<?> getMcodeAndLayer(String staIdC, String type, String startTime, String endTime) {
        if (staIdC == null) {
            staIdC = commonTable.getStaShortForecast();
        }
        List<String> mcodes = commonDataParse.getShortForecastMcode(TypeEnum.TYPE_SHORT_FORECAST, staIdC, type, startTime, endTime);
        List<Integer> layers = commonDataParse.getShortForecastLayer(TypeEnum.TYPE_SHORT_FORECAST, staIdC, type, startTime, endTime);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("mcode", mcodes);
        resultMap.put("layer", layers);
        if (mcodes.size() == 0) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(resultMap);
        }
    }


    public CommonResult<?> getDataParseByTypeAndTime(String type, String startTime, String endTime, String mcode, String layer) throws IOException {
        String staIdC = commonTable.getStaShortForecast();
        ShortForecastParseTxt txt = commonDataParse.getShortForecastTxtByPara(TypeEnum.TYPE_SHORT_FORECAST, staIdC, type, startTime, endTime, mcode, layer);
        if (txt == null) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(txt);
        }
    }

    public void getFileParseByFileName(String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        String staIdC = commonTable.getStaShortForecast();
        commonDataParse.getShortForecastPngByFileName(TypeEnum.TYPE_SHORT_FORECAST, staIdC, type, fileName, response, isOnLine);
    }

}
