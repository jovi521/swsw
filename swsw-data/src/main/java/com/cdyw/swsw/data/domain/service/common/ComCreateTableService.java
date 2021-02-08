package com.cdyw.swsw.data.domain.service.common;

import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.data.domain.dao.common.ComCreateTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 专门用来处理创建表的业务类
 *
 * @author jovi
 */
@Service
public class ComCreateTableService {

    private final CommonTableName commonTableName;

    private final ComCreateTableMapper comCreateTableMapper;

    private final CommonTable commonTable;

    @Autowired
    public ComCreateTableService(CommonTableName commonTableName, ComCreateTableMapper comCreateTableMapper, CommonTable commonTable) {
        this.commonTableName = commonTableName;
        this.comCreateTableMapper = comCreateTableMapper;
        this.commonTable = commonTable;
    }

    /**
     * 整合到一个方法中，实现所有大类所有表的创建：表名 + 字段
     */
    public int createTables() {
        int size = 0;
        List<String> tableNamesByType;
        String tableComment;
        TypeEnum[] typeEnums = TypeEnum.values();
//        TypeEnum[] typeEnums = {TypeEnum.TYPE_AT_STATION};
        for (TypeEnum type : typeEnums) {
            switch (type) {
                case TYPE_RADAR_WEATHER:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_RADAR_WEATHER);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "天气雷达基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "天气雷达解析表";
                            comCreateTableMapper.createRadarParse(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "天气雷达日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_RADAR_PHASED_ARRAY:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_RADAR_PHASED_ARRAY);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "相控阵雷达基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "相控阵雷达解析表";
                            comCreateTableMapper.createRadarParse(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "相控阵雷达日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_RADAR_WIND_PROFILE:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_RADAR_WIND_PROFILE);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "风廓线雷达基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "风廓线雷达解析表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "风廓线雷达日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_RADAR_EXT:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_RADAR_EXT);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "雷达外推基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "雷达外推解析表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "雷达外推日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_3D_GRID_FUSION:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_3D_GRID_FUSION);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "三维格点融合基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "三维格点融合解析表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "三维格点融合日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_AT_STATION:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_AT_STATION);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "自动站基础表";
                            comCreateTableMapper.createAtStationBase(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "自动站解析表";
                            comCreateTableMapper.createAtStationParse(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "自动站日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_SATELLITE:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_SATELLITE);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "卫星基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "卫星解析表";
                            comCreateTableMapper.createSatelliteParse(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "卫星日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_CLDAS_1KM:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_CLDAS_1KM);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "CLDAS_1KM基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "CLDAS_1KM解析表";
                            comCreateTableMapper.createCldas1kmParse(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "CLDAS_1KM日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_ECMWF_HR:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_ECMWF_HR);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "ECMWF_HR基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "ECMWF_HR解析表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "ECMWF_HR日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_GRAPES_3KM:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_GRAPES_3KM);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "GRAPES_3KM基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "GRAPES_3KM解析表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "GRAPES_3KM日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_SWC_WARM:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_SWC_WARM);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "SWC_WARM基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "SWC_WARM解析表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "SWC_WARM日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_SWC_WARR:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_SWC_WARR);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "SWC_WARR基础表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "SWC_WARR解析表";
                            comCreateTableMapper.createFileEntity(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "SWC_WARR日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_FUSION_2H:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_FUSION_2H);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "0-2小时融合基础表";
                            comCreateTableMapper.createFusion(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "0-2小时融合解析表";
                            comCreateTableMapper.createFusion(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "0-2小时融合日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                case TYPE_FUSION_12H:
                    tableNamesByType = commonTableName.getTableNamesByType(TypeEnum.TYPE_FUSION_12H);
                    for (String tableName : tableNamesByType) {
                        if (tableName.contains(commonTable.getSufBase())) {
                            tableComment = "0-12小时融合基础表";
                            comCreateTableMapper.createFusion(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getSufParse())) {
                            tableComment = "0-12小时融合解析表";
                            comCreateTableMapper.createFusion(tableName, tableComment);
                            size += 1;
                        } else if (tableName.contains(commonTable.getLog())) {
                            tableComment = "0-12小时融合日志表";
                            comCreateTableMapper.createLog(tableName, tableComment);
                            size += 1;
                        }
                    }
                    continue;
                default:
                    break;
            }
        }
        return size;
    }

}
