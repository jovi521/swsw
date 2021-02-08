package com.cdyw.swsw.data.domain.service.grapes_3km;

import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.common.util.RegexUtil;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.data.domain.dao.common.CommonDataMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class Grapes3kmService {

    Logger logger = LoggerFactory.getLogger(Grapes3kmService.class);

    @Autowired
    private CommonTable commonTable;
    @Autowired
    private CommonPath commonPath;
    @Autowired
    private CommonDataMapper commonDataMapper;

    @Transactional(rollbackFor = Exception.class)
    public void getData(String filepath) throws Exception {
        // D:\Data\grapes\Z_NAFP_C_BABJ_20201208000000_P_NWPC-GRAPES-3KM-CN-00000.grb2
        // 从文件路径中截取文件名
        String[] pathArray = filepath.split("\\\\");
        String filename = pathArray[pathArray.length - 1];
        String[] fileArray = filename.split("_");
        // 从文件名中匹配数据时间
        String timeStr = RegexUtil.getString("^\\d{14}$", fileArray);
        // 从文件名中匹配预报小时
        String forecasttimeStr = fileArray[fileArray.length - 1];
        String[] forecasttimeArray = forecasttimeStr.split("-");
        String forecasttimeCount = forecasttimeArray[forecasttimeArray.length - 1].replaceAll(".grb2", "");
        // mcode构成 产品类型_预报次数
        int type = 21;
        String mcode = type + "_" + Integer.parseInt(forecasttimeCount) / 100;
        // 从时间里截取年月
        String yyyyMM = timeStr.substring(0, 6);
        // 时间里截取年月日
        String yyyyMMdd = timeStr.substring(0, 8);
        // 这里得到的时间戳是世界时，要转化为北京时
        long dateTime = DateUtils.getStringToDate(timeStr);
        dateTime += 8 * 3600 * 1000;
        dateTime /= 1000;
        // 拼接要插入的表名
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeGrapes3km()
                + commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufBase();
        // 将数据转移到本地磁盘
        // 拼接要转移到的目标路径
        String totalPath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getGrapes3kmPath() + yyyyMMdd + "/" + filename;
        try {
            FileUtils.copyFile(new File(filepath), new File(totalPath));
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(filename + "文件复制失败！");
            throw new Exception();
        }
        // 拼接文件存储的相对路径
        String relativePath = commonPath.getGrapes3kmPath() + yyyyMMdd + "/";
        // 去掉第一个"/"
        relativePath = relativePath.replaceFirst("/", "");
        // 获得文件的大小
        int fileLength = (int) new File(totalPath).length();
        // 对要插入的数据进行封装
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(filename);
        fileEntity.setPosFile(relativePath);
        fileEntity.setFileSize(fileLength);
        fileEntity.setType(type);
        fileEntity.setTime(dateTime);
        fileEntity.setModifyType(0);
        fileEntity.setMcode(mcode);
        fileEntity.setCreateTime(System.currentTimeMillis() / 1000);
        commonDataMapper.insertCommonInfo(fileEntity, tablename);
        // 记录日志
        String logTable = commonTable.getLog() + commonTable.getLink() + commonTable.getTypeGrapes3km() +
                commonTable.getLink() + yyyyMM;
        DataMonitorLog log = new DataMonitorLog();
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime fileTime = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int status = 0;
        String msg = localDateTime + "插入了" + filename + ", 状态为" + status;
        log.setCreateTime(localDateTime);
        log.setDateTime(fileTime);
        log.setType(21);
        log.setStatus(status);
        log.setMsg(msg);
        commonDataMapper.insertCommonLog(log, logTable);
        logger.info(msg);
    }


}
