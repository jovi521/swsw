package com.cdyw.swsw.data.domain.service.fusion;

import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.data.domain.dao.fusion.FusionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 0-2h融合业务处理类
 *
 * @author jovi
 */
@Service
public class FusionService {

    private final FusionMapper fusionMapper;

    private final CommonTableName commonTableName;

    private final CommonTable commonTable;

    @Autowired
    public FusionService(FusionMapper fusionMapper, CommonTableName commonTableName, CommonTable commonTable) {
        this.fusionMapper = fusionMapper;
        this.commonTableName = commonTableName;
        this.commonTable = commonTable;
    }

    public CommonResult<?> getFusionsByParam(String type, String time) {
        // 目前仅限获取一下 3 中类型的数据源
        TypeEnum[] enums = {TypeEnum.TYPE_GRAPES_3KM, TypeEnum.TYPE_SWC_WARM, TypeEnum.TYPE_SWC_WARR};
        FileEntity fileEntity = new FileEntity();
        if (type != null) {
            String typeUp = type.toUpperCase();
            for (TypeEnum typeEnum : enums) {
                if (typeUp.equals(typeEnum.getName())) {
                    List<String> tableNamesByType = commonTableName.getTableNamesByType(typeEnum);
                    for (String tableName : tableNamesByType) {
                        // 只查询 base 表即可
                        if (tableName.contains(commonTable.getSufBase())) {
                            fileEntity = fusionMapper.getFusionsByParam(tableName, time);
                            break;
                        }
                    }
                }
            }
        }
        if (fileEntity == null) {
            return CommonResult.failed("抱歉，此时刻查询不到数据，或者请检查参数是否正确");
        } else {
            return CommonResult.success(fileEntity);
        }
    }
}
