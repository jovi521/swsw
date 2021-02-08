package com.cdyw.swsw.data.common.component;

import cn.hutool.core.io.FileUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonPathName;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.common.util.RegexUtil;
import com.cdyw.swsw.common.domain.ao.enums.ProductEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import com.cdyw.swsw.data.common.http.HttpDownloadApi;
import com.cdyw.swsw.data.domain.dao.common.CommonDataMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 根据 源文件（名称或路径）获取 一级大类、二级站点、文件时间、三级产品，以及跟文件相关的所有操作
 *
 * @author jovi
 */
@Slf4j
@Component
public class CommonFileName {

    private final CommonPath commonPath;

    private final CommonTable commonTable;

    private final CommonTableName commonTableName;

    private final CommonPathName commonPathName;

    private final CommonDataMapper commonDataMapper;

    private final HttpDownloadApi httpDownloadApi;

    @Autowired
    public CommonFileName(CommonPath commonPath, CommonTable commonTable, CommonTableName commonTableName, CommonPathName commonPathName, CommonDataMapper commonDataMapper, HttpDownloadApi httpDownloadApi) {
        this.commonPath = commonPath;
        this.commonTable = commonTable;
        this.commonTableName = commonTableName;
        this.commonPathName = commonPathName;
        this.commonDataMapper = commonDataMapper;
        this.httpDownloadApi = httpDownloadApi;
    }

    private String[] getNameSplits(File fileSource) {
        String fileSourceName = fileSource.getName();
        return fileSourceName.split("_");
    }

    private String[] getNameSplits(String fileName) {
        return fileName.split("_");
    }

    /**
     * 根据 源文件（名称或路径）获取 时间
     * 目前所有大类的文件全部都是”yyyyMMddHHmmss“格式，所以暂时使用统一处理方式
     *
     * @param fileSource 源文件
     * @return 文件时间
     */
    private String getTime(File fileSource) {
        String regexTime = "^\\d{12,14}$";
        return RegexUtil.getString(regexTime, getNameSplits(fileSource));
    }

    /**
     * 根据 源文件名称获取 时间
     * 目前所有大类的文件全部都是”yyyyMMddHHmmss“格式，所以暂时使用统一处理方式
     *
     * @param fileName 源文件
     * @return 文件时间
     */
    private String getTime(String fileName) {
        String regexTime = "^\\d{12,14}$";
        return RegexUtil.getString(regexTime, getNameSplits(fileName));
    }

    /**
     * 根据 源文件（名称或路径）获取 二级站点
     *
     * @param typeEnum   一级大类
     * @param fileSource 源文件
     * @return 二级站点
     */
    public String getStaIdC(TypeEnum typeEnum, File fileSource) {
        String[] nameSplits = getNameSplits(fileSource);
        String staIdC = null;
        switch (typeEnum) {
            case TYPE_RADAR_WEATHER:
                // Z_RADR_I_Z9280_20201215215620_O_DOR_SC_CAP_FMT.bin.bz2
                String regexStaRadarWea = "^\\w{1}\\d{4}$";
                staIdC = RegexUtil.getString(regexStaRadarWea, nameSplits);
                break;
            case TYPE_RADAR_WIND_PROFILE:
                // Z_RADA_I_56285_20201215000000_P_WPRD_LC_OOBS.TXT
                String regexStaRadarWinPro = "^\\d{5}$";
                staIdC = RegexUtil.getString(regexStaRadarWinPro, nameSplits);
                break;
            case TYPE_RADAR_PHASED_ARRAY:
                // Z_RADR_I_ZCD02_20201210082115_O_DOR_DXK_CAR.bin.bz2
                String staChongZhou = "ZCD01";
                String staXinDu = "ZCD02";
                String staTianFu = "ZCD03";
                String regexStaRadarPhaArr = "^\\w{3}\\d{2}$";
                staIdC = RegexUtil.getString(regexStaRadarPhaArr, nameSplits);
                if (staIdC.contains(staChongZhou)) {
                    staIdC = commonPath.getRadarPhaArrStaChongZhou();
                } else if (staIdC.contains(staXinDu)) {
                    staIdC = commonPath.getRadarPhaArrStaXinDu();
                } else if (staIdC.contains(staTianFu)) {
                    staIdC = commonPath.getRadarPhaArrStaTianFu();
                }
                break;
            default:
                break;
        }
        return staIdC;
    }

    /**
     * 根据 源文件（名称或路径）获取 二级站点
     *
     * @param typeEnum 一级大类
     * @param fileName 源文件
     * @return 二级站点
     */
    private String getStaIdC(TypeEnum typeEnum, String fileName) {
        String[] nameSplits = getNameSplits(fileName);
        String staIdC = null;
        switch (typeEnum) {
            case TYPE_RADAR_WEATHER:
                // Z_RADR_I_Z9280_20201215215620_O_DOR_SC_CAP_FMT.bin.bz2
                String regexStaRadarWea = "^\\w{1}\\d{4}$";
                staIdC = RegexUtil.getString(regexStaRadarWea, nameSplits);
                break;
            case TYPE_RADAR_WIND_PROFILE:
                // Z_RADA_I_56285_20201215000000_P_WPRD_LC_OOBS.TXT
                String regexStaRadarWinPro = "^\\d{5}$";
                staIdC = RegexUtil.getString(regexStaRadarWinPro, nameSplits);
                break;
            case TYPE_RADAR_PHASED_ARRAY:
                // Z_RADR_I_ZCD02_20201210082115_O_DOR_DXK_CAR.bin.bz2
                String staChongZhou = "ZCD01";
                String staXinDu = "ZCD02";
                String staTianFu = "ZCD03";
                String regexStaRadarPhaArr = "^\\w{3}\\d{2}$";
                staIdC = RegexUtil.getString(regexStaRadarPhaArr, nameSplits);
                if (staIdC.contains(staChongZhou)) {
                    staIdC = commonPath.getRadarPhaArrStaChongZhou();
                } else if (staIdC.contains(staXinDu)) {
                    staIdC = commonPath.getRadarPhaArrStaXinDu();
                } else if (staIdC.contains(staTianFu)) {
                    staIdC = commonPath.getRadarPhaArrStaTianFu();
                }
                break;
            default:
                break;
        }
        return staIdC;
    }

    /**
     * 根据 源文件（名称或路径）获取 三级产品
     *
     * @param typeEnum   一级大类
     * @param fileSource 源文件
     * @return 三级产品
     */
    private String getType(TypeEnum typeEnum, File fileSource) {
        String[] nameSplits = getNameSplits(fileSource);
        String type = null;
        switch (typeEnum) {
            case TYPE_RADAR_WEATHER:
                // Z_RADR_I_Z9280_20201215215620_O_DOR_SC_CAP_FMT.bin.bz2
                type = nameSplits[8];
                break;
            case TYPE_RADAR_WIND_PROFILE:
            case TYPE_RADAR_PHASED_ARRAY:
                // // Z_RADR_I_ZCD02_20201210082115_O_DOR_DXK_CAR.bin.bz2
                // Z_RADA_I_56285_20201215000000_P_WPRD_LC_OOBS.TXT
                type = nameSplits[nameSplits.length - 1].split("\\.")[0];
                break;
            case TYPE_ECMWF_HR:
            case TYPE_SWC_WARR:
            case TYPE_SWC_WARM:
                // SWCWARR_20201213180000_F01_3KM.grb
                // W_NAFP_C_ECMF_20201213174247_P_C1D12131200121312001.bz2 由于暂时没站点没产品，所以先暂时默认个”产品“值
                type = commonTable.getSufBase();
                break;
            case TYPE_CLDAS_1KM:
                // Z_NAFP_C_BABJ_20201222181201_P_HRCLDAS_RT_CHN-BCCD_0P01_HOR-QAIR-2020122302.GRB2
                type = nameSplits[nameSplits.length - 1].split("-")[1];
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 根据 源文件（名称或路径）获取 三级产品
     *
     * @param typeEnum 一级大类
     * @param fileName 源文件
     * @return 三级产品
     */
    private String getType(TypeEnum typeEnum, String fileName) {
        String[] nameSplits = getNameSplits(fileName);
        String type = null;
        switch (typeEnum) {
            case TYPE_RADAR_WEATHER:
                // Z_RADR_I_Z9280_20201215215620_O_DOR_SC_CAP_FMT.bin.bz2
                type = nameSplits[8];
                break;
            case TYPE_RADAR_WIND_PROFILE:
            case TYPE_RADAR_PHASED_ARRAY:
                // // Z_RADR_I_ZCD02_20201210082115_O_DOR_DXK_CAR.bin.bz2
                // Z_RADA_I_56285_20201215000000_P_WPRD_LC_OOBS.TXT
                type = nameSplits[nameSplits.length - 1].split("\\.")[0];
                break;
            case TYPE_ECMWF_HR:
            case TYPE_SWC_WARR:
            case TYPE_SWC_WARM:
                // SWCWARR_20201213180000_F01_3KM.grb
                // W_NAFP_C_ECMF_20201213174247_P_C1D12131200121312001.bz2 由于暂时没站点没产品，所以先暂时默认个”产品“值
                type = commonTable.getSufBase();
                break;
            case TYPE_CLDAS_1KM:
                // Z_NAFP_C_BABJ_20201222181201_P_HRCLDAS_RT_CHN-BCCD_0P01_HOR-QAIR-2020122302.GRB2
                type = nameSplits[nameSplits.length - 1].split("-")[1];
                break;
            default:
                break;
        }
        return type;
    }

    /**
     * 根据 目标文件（名称或路径）获取 文件实体类
     *
     * @param typeEnum 一级大类
     * @param destFile 目标文件
     * @return 文件实体类
     */
    public FileEntity getFileEntity(TypeEnum typeEnum, File destFile) {
        FileEntity fileEntity = new FileEntity();
        String name = destFile.getName();
        fileEntity.setName(name);
        fileEntity.setPosFile(destFile.getParent());
        fileEntity.setFileSize(Integer.parseInt(String.valueOf(destFile.length())));
        if (typeEnum.equals(TypeEnum.TYPE_RADAR_WIND_PROFILE)) {
            // 风廓线比较特殊，type 值存 6、30、60
            if (name.contains(ProductEnum.ROBS.name())) {
                fileEntity.setType(6);
            } else if (name.contains(ProductEnum.HOBS.name())) {
                fileEntity.setType(30);
            } else if (name.contains(ProductEnum.OOBS.name())) {
                fileEntity.setType(60);
            }
        } else {
            fileEntity.setType(typeEnum.getType());
        }
        // 将毫秒时间戳转化成秒时间戳
        fileEntity.setCreateTime((System.currentTimeMillis()) / 1000);
        fileEntity.setModifyType(0);
        String patten = "yyyyMMddHHmmss";
        String staIdC = null;
        if (destFile.getPath().contains(commonTable.getSufParse())) {
            switch (typeEnum) {
                case TYPE_RADAR_WEATHER:
                case TYPE_RADAR_PHASED_ARRAY:
                    Path filePath = destFile.toPath();
                    String replace = destFile.getName().split("\\.")[0].replace("_", "");
                    fileEntity.setTime(DateUtils.getDateUtc2TimeLocal(replace, patten));
                    staIdC = FileUtil.getPathEle(filePath, filePath.getNameCount() - 4).toString();
                    break;
                default:
                    break;
            }
        } else {
            fileEntity.setTime(DateUtils.getDateUtc2TimeLocal(getTime(destFile), patten));
            staIdC = getStaIdC(typeEnum, destFile);
        }
        if (staIdC != null) {
            fileEntity.setRadarcd(staIdC.replace("/", ""));
        }
        return fileEntity;
    }

    /**
     * 根据 一级大类 和 源文件（名称或路径） 进行文件录入、数据录入、日志录入（仅限于 base 表操作）
     *
     * @param typeEnum   一级大类
     * @param fileSource 源文件
     * @return 插入数据表记录
     */
    public int insertFileEntityBase(TypeEnum typeEnum, File fileSource) {
        int size = 0;
        String t = commonTable.getSufBase();
        String staIdC = this.getStaIdC(typeEnum, fileSource);
        String type = this.getType(typeEnum, fileSource);
        String time = this.getTime(fileSource);
        String fileTargetPathName = commonPathName.getPathNameByPara(typeEnum, staIdC, type, time, t);
        // 最终目标文件路径
        File fileDestPath = new File(fileTargetPathName);
        boolean flag = fileDestPath.exists();
        if (!flag) {
            flag = fileDestPath.mkdirs();
        }
        // 如果此文件夹存在
        if (flag) {
            // ①：存储文件(此处返回的是路径)
            FileUtil.copy(fileSource, fileDestPath, false);
            File destFile = new File(fileDestPath.getPath() + "\\" + fileSource.getName());
            // ②：写入数据库
            String tableName = commonTableName.getTableNameByParam(typeEnum, staIdC, time, t);
            FileEntity fileEntity = this.getFileEntity(typeEnum, destFile);
            size = commonDataMapper.insertCommonInfo(fileEntity, tableName);
            // 插入数据、存储文件后，记录日志
            DataMonitorLog log = new DataMonitorLog();
            log.setCreateTime(LocalDateTime.now());
            String patten = "yyyyMMddHHmmss";
            log.setDateTime(DateUtils.getDateUtc2Local(time, patten));
            log.setType(typeEnum.getType());
            if (size > 0) {
                log.setStatus(1);
            } else {
                log.setStatus(0);
            }
            log.setMsg(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 共新增 " + size + " 条数据");
            // 日志表只需要一级大类
            String logTableName = commonTableName.getTableNameByParam(typeEnum, null, time, commonTable.getLog());
            commonDataMapper.insertCommonLog(log, logTableName);
        }
        return size;
    }

    public boolean downloadFile2Base(TypeEnum typeEnum, String fileName, String url) {
        boolean flag = false;
        String type = this.getType(typeEnum, fileName);
        String time = this.getTime(fileName);
        // ① 先查询监控路径
        String monitorPath = commonPathName.getMonitorPathNameByPara(typeEnum, null, type, time, commonTable.getSufBase());
        File monitorPathF = new File(monitorPath);
        if (!monitorPathF.exists()) {
            flag = monitorPathF.mkdirs();
        }
        File monitorPathFile = new File(monitorPath + "/" + fileName);
        // ② 再获取文件
        Call<ResponseBody> call = httpDownloadApi.downloadResult(url);
        try {
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (body != null) {
                            // ③ 将文件下载到指定监控路径下
                            boolean flag = downloadFileToBase(monitorPathFile, body);
                            if (flag) {
                                log.info("***** 下载成功： " + response.message() + " *****");
                            }
                        }
                    } else {
                        log.error("***** 下载失败： " + response.message() + " *****");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    log.error("***** 下载失败： " + t.getMessage() + " *****");
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean downloadFileToBase(File monitorPathFile, ResponseBody body) {
        try {
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(monitorPathFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
