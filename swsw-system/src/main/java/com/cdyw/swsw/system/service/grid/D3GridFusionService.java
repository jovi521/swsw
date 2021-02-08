package com.cdyw.swsw.system.service.grid;

import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.component.CommonDataParse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author jovi
 */
@Slf4j
@Service
public class D3GridFusionService {

    private final CommonDataParse commonDataParse;

    private final CommonTable commonTable;

    @Autowired
    public D3GridFusionService(CommonDataParse commonDataParse, CommonTable commonTable) {
        this.commonDataParse = commonDataParse;
        this.commonTable = commonTable;
    }

    public CommonResult<?> getDataParseByTypeAndTime(String type, String startTime, String endTime, String hight) {
        String staIdC = commonTable.getSta3dGridFusion();
        if ("0".equals(hight)) {
            hight = "000";
        }
        Map<String, Object> fileNameByPara = commonDataParse.getFileNameByPara(TypeEnum.TYPE_3D_GRID_FUSION, staIdC, type, startTime, endTime, hight, null);
        if (fileNameByPara.size() == 0) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(fileNameByPara);
        }
    }

    public void getFileParseByFileName(String type, String fileName, HttpServletResponse response, boolean isOnLine) throws IOException {
        String staIdC = commonTable.getSta3dGridFusion();
        commonDataParse.getJsonByFileName(TypeEnum.TYPE_3D_GRID_FUSION, staIdC, type, fileName, response, isOnLine);
    }

}
