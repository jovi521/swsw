package com.cdyw.swsw.data.common.component;

import cn.hutool.core.io.FileUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonPathName;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.util.CommonRadarParseTxtUtil;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.enums.FileTypeEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.ao.txt.CommonRadarParseTxt;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.data.domain.dao.radar.RadarPhasedArrayMapper;
import com.cdyw.swsw.data.domain.dao.radar.RadarWeatherMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

/**
 * 天气雷达和相控阵雷达解析文件的处理类
 *
 * @author jovi
 */
@Component
public class CommonRadarParseName {

    private final CommonTableName commonTableName;

    private final CommonPathName commonPathName;

    private final CommonTable commonTable;

    private final CommonFileName commonFileName;

    private final CommonPath commonPath;

    private final RadarWeatherMapper radarWeatherMapper;

    private final RadarPhasedArrayMapper radarPhasedArrayMapper;

    public CommonRadarParseName(CommonTableName commonTableName, CommonPathName commonPathName, CommonTable commonTable, CommonFileName commonFileName, CommonPath commonPath, RadarWeatherMapper radarWeatherMapper, RadarPhasedArrayMapper radarPhasedArrayMapper) {
        this.commonTableName = commonTableName;
        this.commonPathName = commonPathName;
        this.commonTable = commonTable;
        this.commonFileName = commonFileName;
        this.commonPath = commonPath;
        this.radarWeatherMapper = radarWeatherMapper;
        this.radarPhasedArrayMapper = radarPhasedArrayMapper;
    }

    public int insertRadarParse(TypeEnum typeEnum, File fileSource) {
        boolean flag;
        int size = 0;
        // 判断文件是否正确，目前只接受3种格式文件
        String fileSuffixName = FileUtil.getSuffix(fileSource);
        if (fileSuffixName.equalsIgnoreCase(FileTypeEnum.FILE_TYPE_TXT.getSuffix()) ||
                fileSuffixName.equalsIgnoreCase(FileTypeEnum.FILE_TYPE_PNG.getSuffix()) ||
                fileSuffixName.equalsIgnoreCase(FileTypeEnum.FILE_TYPE_ZIP.getSuffix())) {
            // D:\ParseDataFiles\天气雷达\Z9280\20210107\EB\20210107_094900.00.007.000_18.5_R2.PNG
            // D:\ParseDataFiles\相控阵雷达\ZCD03\20210110\EB\\20210110_000000.00.007.000_18.5_R0.png
            Path filePath = fileSource.toPath();
            // 根据文件路径解析，获取文件的二级站点
            Path fileStaEle = FileUtil.getPathEle(filePath, filePath.getNameCount() - 4);
            String staIdC = fileStaEle.toString();
            String staChongZhou = "ZCD01";
            String staTianFu = "ZCD02";
            String staXinDu = "ZCD03";
            switch (typeEnum) {
                case TYPE_RADAR_PHASED_ARRAY:
                    if (staIdC.contains(staChongZhou)) {
                        staIdC = commonPath.getRadarPhaArrStaChongZhou();
                    } else if (staIdC.contains(staXinDu)) {
                        staIdC = commonPath.getRadarPhaArrStaXinDu();
                    } else if (staIdC.contains(staTianFu)) {
                        staIdC = commonPath.getRadarPhaArrStaTianFu();
                    } else {
                        staIdC = commonPath.getRadarPhaArrStaMulti();
                    }
                    break;
                case TYPE_RADAR_WEATHER:
                    staIdC = fileStaEle.toString();
                    break;
                default:
                    break;
            }
            // 根据文件路径解析，获取文件的三级产品
            Path fileTypeEle = FileUtil.getPathEle(filePath, filePath.getNameCount() - 2);
            String type = fileTypeEle.toString();
            // 根据文件名称解析，获取文件时间 yyyyMMddHHmmss(UTC) 这里是北京时
            String filePrefixName = FileUtil.getPrefix(fileSource);
            String yyyyMMddHHmmss = filePrefixName.split("\\.")[0].replace("_", "");
            // ① 根据文件基础信息查询目标存储路径
            String t = commonTable.getSufParse();
            String fileTargetPathName = commonPathName.getPathNameByPara(typeEnum, staIdC, type, yyyyMMddHHmmss, t);
            // 最终目标文件路径
            File fileDestPath = new File(fileTargetPathName);
            flag = fileDestPath.exists();
            if (!flag) {
                flag = fileDestPath.mkdirs();
            }
            if (flag) {
                // ②：存储文件(此处返回的是路径)
                fileDestPath = FileUtil.copy(fileSource, fileDestPath, false);
                // ③ 入库(先查询，三个文件只存储一条文件名称索引即可)
                String tableName = commonTableName.getTableNameByParam(typeEnum, staIdC, yyyyMMddHHmmss, t);
                String posFile = fileDestPath.getName();
                CommonRadarParseTxt radarParseTxt = new CommonRadarParseTxt();
                switch (typeEnum) {
                    case TYPE_RADAR_WEATHER:
                        radarParseTxt = radarWeatherMapper.selectParseByPathAndName(tableName, posFile, filePrefixName);
                        break;
                    case TYPE_RADAR_PHASED_ARRAY:
                        radarParseTxt = radarPhasedArrayMapper.selectParseByPathAndName(tableName, posFile, filePrefixName);
                        break;
                    default:
                        break;
                }

                if (radarParseTxt == null) {
                    fileSuffixName = FileUtil.getSuffix(fileSource);
                    // 只解析 txt 文件入库
                    if (fileSuffixName.equals(FileTypeEnum.FILE_TYPE_TXT.getSuffix())) {
                        radarParseTxt = CommonRadarParseTxtUtil.readParseTxt(fileSource);
                        File fileDest = new File(fileTargetPathName + "/" + fileSource.getName());
                        FileEntity fileEntity = commonFileName.getFileEntity(typeEnum, fileDest);
                        fileEntity.setTime(DateUtils.getStringToDate(yyyyMMddHHmmss) / 1000);
                        switch (typeEnum) {
                            case TYPE_RADAR_WEATHER:
                                size = radarWeatherMapper.insertRadarParse(tableName, fileEntity, radarParseTxt);
                                break;
                            case TYPE_RADAR_PHASED_ARRAY:
                                size = radarPhasedArrayMapper.insertRadarParse(tableName, fileEntity, radarParseTxt);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return size;
    }
}
