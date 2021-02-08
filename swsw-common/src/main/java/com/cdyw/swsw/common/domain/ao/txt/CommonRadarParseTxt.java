package com.cdyw.swsw.common.domain.ao.txt;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 天气雷达、相控阵雷达解析后的txt文件内容封装成对象，方便转化成json传给前端
 *
 * @author jovi
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonRadarParseTxt extends FileEntity {
    private String fileName;
    private String productID;
    private String productName;
    private String startTime;
    private String endTime;
    private String stationNo;
    private String stationName;
    private String radarCoor;
    private String maxDis;
    private String height;
    private String projection;
    private String leftBottomCoor;
    private String rightTopCoor;
    private String widthHeight;
    private String radarPos;
}
