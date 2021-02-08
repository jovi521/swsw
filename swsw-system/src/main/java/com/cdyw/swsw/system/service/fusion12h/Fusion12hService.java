package com.cdyw.swsw.system.service.fusion12h;


import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonPathName;
import com.cdyw.swsw.common.common.component.CommonTable;
import com.cdyw.swsw.common.common.component.CommonTableName;
import com.cdyw.swsw.common.common.util.FileUtils;
import com.cdyw.swsw.common.common.util.Fusion12hWeatherUtil;
import com.cdyw.swsw.common.common.util.MathUtil;
import com.cdyw.swsw.common.common.util.WriteFileUtil;
import com.cdyw.swsw.common.domain.ao.dic.DicLevel;
import com.cdyw.swsw.common.domain.ao.dic.DicLonLat;
import com.cdyw.swsw.common.domain.ao.enums.DataProductTypeEnum;
import com.cdyw.swsw.common.domain.ao.enums.Fusion12hModifiedTypeEnum;
import com.cdyw.swsw.common.domain.ao.enums.ProductEnum;
import com.cdyw.swsw.common.domain.ao.enums.TypeEnum;
import com.cdyw.swsw.common.domain.dto.fusion.Fusion12hLonLatArray;
import com.cdyw.swsw.common.domain.entity.fusion12h.*;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.util.TimeStampUtil;
import com.cdyw.swsw.system.dao.dic.DicLevelMapper;
import com.cdyw.swsw.system.dao.fusion12h.Fusion12hMapper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Fusion12 业务处理类
 *
 * @author liudong
 * @modified jovi
 */
@Service
public class Fusion12hService {

    private final TypeEnum TYPE = TypeEnum.TYPE_FUSION_12H;

    private final CommonTableName commonTableName;

    private final CommonTable commonTable;

    private final CommonPath commonPath;

    private final Fusion12hMapper fusion12hMapper;
    private final CommonPathName commonPathName;
    private final DicLevelMapper dicLevelMapper;
    Logger logger = LoggerFactory.getLogger(Fusion12hService.class);
    private GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    public Fusion12hService(CommonTableName commonTableName, CommonTable commonTable, CommonPath commonPath, Fusion12hMapper fusion12hMapper, CommonPathName commonPathName, DicLevelMapper dicLevelMapper) {
        this.commonTableName = commonTableName;
        this.commonTable = commonTable;
        this.commonPath = commonPath;
        this.fusion12hMapper = fusion12hMapper;
        this.commonPathName = commonPathName;
        this.dicLevelMapper = dicLevelMapper;
    }

    public CommonResult<?> getDataByTypeAndTime(String type, String layer, String basetime, String time) {
        Long baseTime = Long.parseLong(basetime);
        Long timestamp = Long.parseLong(time);
        Map<String, Object> modifiedMap = fusion12hMapper.selectModifiedNewest(Integer.parseInt(type), Integer.parseInt(layer), baseTime, timestamp);
        logger.error(modifiedMap.toString());
        String fileName = (String) modifiedMap.get("name");
        // 从文件名中获得时间，以确定从哪张表里面查
        String fileyyyyMM = fileName.substring(0, 6);
        String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() + commonTable.getLink() +
                fileyyyyMM + commonTable.getLink() + commonTable.getSufParse();
        // 通过文件名查
        Fusion12h fusion12h = fusion12hMapper.selectByFilename(tableName, fileName);
        // 获得文件相对路径
        String relativePath = fusion12h.getPosFile();
        String totalFilePath = commonPath.getDisk() + commonPath.getCommonPath() + relativePath + fileName;
        logger.error(totalFilePath);
        // 读取对应的数据内容
        String content = "";
        try {
            content = FileUtils.readCharByPath(totalFilePath);
            logger.error(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", JSONUtil.parseObj(content));
        resultMap.put("basetime", baseTime);
        resultMap.put("fileName", fileName);
        return CommonResult.success(resultMap);
    }

    public CommonResult<?> getDetailDataNewest() {
        // 查询最新一组数据的详细信息

        List<Map<String, Object>> detailList = fusion12hMapper.selectDetailInfoNewest();
        if (detailList == null || detailList.size() == 0) {
            return CommonResult.failed("未查询到数据！");
        }
        // 获得起报时间
        long basetime = (Long) detailList.get(0).get("basetime");
        // 查询这组数据的订正情况
        Map<String, Object> basetimeModifiedMap = fusion12hMapper.selectByBasetimeModified(basetime);
        int basemapModified = (Integer) basetimeModifiedMap.get("modify_type");
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> titleInfoList = new ArrayList<>();
        int[] productTypeArray = {2501, 2503, 2504, 2505};
        if (basemapModified == 0) {
            for (int j : productTypeArray) {
                Map<String, Object> temMap = new HashMap<>();
                temMap.put("type", j);
                temMap.put("modifiedType", basemapModified);
                titleInfoList.add(temMap);
            }
        } else {
            for (Map<String, Object> temMap : detailList) {
                long productCount;
                if (temMap.get("count") != null) {
                    Map<String, Object> tempProductMap = new HashMap<>();
                    productCount = (long) temMap.get("count");
                    if (productCount == 0) {
                        tempProductMap.put("modifiedType", basemapModified);
                    } else {
                        tempProductMap.put("modifiedType", 0);
                    }
                    int productType = (int) temMap.get("type");
                    tempProductMap.put("type", productType);
                    titleInfoList.add(tempProductMap);
                }
            }
        }
        // 去除detailList里面forecast_count不在1-12之内的数据
        Iterator<Map<String, Object>> iterator = detailList.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> detailMap = iterator.next();
            int forecastCount = (int) detailMap.get("forecast_count");
            if (forecastCount <= 0 || forecastCount >= 13) {
                iterator.remove();
            }
        }
        resultMap.put("basetime", basetime);
        resultMap.put("basemapModified", basemapModified);
        resultMap.put("detailInfo", detailList);
        resultMap.put("titleInfo", titleInfoList);
        return CommonResult.success(resultMap);
    }

    /*
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> correctData(List<FusionDataVo> dataList, String filename,
                                       String modifyingType) {
        // 根据文件名获得时间信息，获得将要查询的数据库表
        String timeStr = filename.substring(0, 14);
        String yyyyMM = timeStr.substring(0, 6);
        String tableName = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() +
                commonTable.getLink() + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        // 根据文件名查询文件的具体信息
        Fusion12h selectedFile = fusion12hMapper.selectByFilename(tableName, filename);
        if (selectedFile == null) {
            return CommonResult.failed("找不到指定文件，修订失败！");
        }
        // 先根据文件的修改类型判断是否新增一条数据还是对数据进行修改
        Integer modifiedType = selectedFile.getModifyType();
        switch (modifiedType) {
            case 0:
                // 处于未修订状态
                // 新建一个文件，进行修订
                // 获得文件相对路径
                String relativeFile = selectedFile.getPosFile();
                // 拼接得到文件全路径
                String totalFilepath = commonPath.getDisk() + commonPath.getCommonPath() + relativeFile + filename;
                // 读取文件的内容
                String dataContent = "";
                try {
                    dataContent = FileUtils.readCharByPath(totalFilepath);
                } catch (Exception e) {
                    return CommonResult.failed("找不到指定文件，修订失败！");
                }
                if (!StringUtils.isEmpty(dataContent)) {
                    // 将文本内容转换为json对象
                    Map<String, Object> dataMap = JSONUtil.parseObj(dataContent);
                    // 对文本内容进行修改
                    List<Double> valueList = (List<Double>) dataMap.get("data");
                    for (FusionDataVo modifiedData : dataList) {
                        Integer modifiedId = modifiedData.getId();
                        Double modifiedValue = modifiedData.getValue();
                        valueList.set(modifiedId, modifiedValue);
                    }
                    // 创建新文件的名字
                    String modifiedFilename = filename.replaceAll(".json", "_" + modifyingType);
                    modifiedFilename += ".json";
                    dataMap.put("modifyType", modifyingType);
                    dataMap.put("filename", modifiedFilename);
                    // 将json对象转换为json字符串
                    String modifiedData = JSONUtil.toJsonStr(dataMap);
                    // 构建新数据文件的路径
                    String totalModifiedPath = commonPath.getDisk() + commonPath.getCommonPath() + relativeFile + modifiedFilename;
                    // 将数据写入到新路径
                    try {
                        WriteFileUtil.writeFile(modifiedData, totalModifiedPath);
                    } catch (Exception e) {
                        return CommonResult.failed("操作失败！");
                    }
                    // 同时将新文件插入到数据库，并将modify_type值进行修改
                }
                break;
            case 1:
                // 处于已进行面修订状态
                break;
            case 2:
                // 处于已进行点修订状态
                break;
            case 3:
                // 处于锁定状态，不能进行修订
                return CommonResult.failed("该文件已处于锁定状态，修订失败！");
        }
        return null;
    }

     */

    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> correctDataByPolygon(List<String> lonlatList, String modifyValue, String filename, String basetime,
                                                String forecastTime, String layer, String productType) throws IOException, ParseException {
        // 查询这组起报时间的状况
        int modifyType = getModifyTypeByBasetime(basetime);
        // 如果该起报时间已经被锁定，则拒绝
        if (modifyType >= 3) {
            return CommonResult.failed("该数据已经完成锁定。。");
        }
        List<Integer> modifyingIdList = new ArrayList<>();
        for (String lonlat : lonlatList) {
            // 对经纬度串进行解析
            String[] lonlatArray = lonlat.split(",");
            // 找到这里最大的经纬度
            double lonMax = 0;
            double lonMin = 0;
            double latMax = 0;
            double latMin = 0;
            for (String temLonlat : lonlatArray) {
                String[] temLonlatArray = temLonlat.trim().split(" ");
                Double temLon = Double.parseDouble(temLonlatArray[0]);
                Double temLat = Double.parseDouble(temLonlatArray[1]);
                if (temLon > lonMax || lonMax == 0) {
                    lonMax = temLon;
                }
                if (temLon < lonMin || lonMin == 0) {
                    lonMin = temLon;
                }
                if (temLat > latMax || latMax == 0) {
                    latMax = temLat;
                }
                if (temLat < latMin || latMin == 0) {
                    latMin = temLat;
                }
            }
            String lonlatArrayContent;
            lonlatArrayContent = FileUtils.readCharInCurrentProject(commonPath.getLonlatArrayFusion12hPath());
            Map<String, Object> lonlatMap = JSONUtil.parseObj(lonlatArrayContent);
            List<BigDecimal> lonArray = (List<BigDecimal>) lonlatMap.get("lon");
            List<BigDecimal> latArray = (List<BigDecimal>) lonlatMap.get("lat");
            // 如果最大最小经纬度超过了网格的最大最小经纬度，那么将使用网格的最大最小经纬度
            if (lonMin < lonArray.get(0).doubleValue()) {
                lonMin = lonArray.get(0).doubleValue();
            }
            if (lonMax > lonArray.get(lonArray.size() - 1).doubleValue()) {
                lonMax = lonArray.get(lonArray.size() - 1).doubleValue();
            }
            if (latMin < latArray.get(0).doubleValue()) {
                latMin = latArray.get(0).doubleValue();
            }
            if (latMax > latArray.get(latArray.size() - 1).doubleValue()) {
                latMax = latArray.get(latArray.size() - 1).doubleValue();
            }
            int startIndexLon = 0;
            int endIndexLon = 0;
            int startIndexLat = 0;
            int endIndexLat = 0;
            boolean lonflag = false;
            boolean latflag = false;
            for (int i = 0; i < lonArray.size(); i++) {
                if (lonArray.get(i).doubleValue() >= lonMin) {
                    if (!lonflag) {
                        startIndexLon = i;
                        lonflag = true;
                    }
                }
                if (lonArray.get(i).doubleValue() >= lonMax) {
                    endIndexLon = i - 1;
                    break;
                }
            }
            for (int i = 0; i < latArray.size(); i++) {
                if (latArray.get(i).doubleValue() >= latMin) {
                    if (!latflag) {
                        startIndexLat = i;
                        latflag = true;
                    }
                }
                if (latArray.get(i).doubleValue() >= latMax) {
                    endIndexLat = i - 1;
                    break;
                }
            }
            for (int i = startIndexLon; i < endIndexLon + 1; i++) {
                for (int j = startIndexLat; j < endIndexLat + 1; j++) {
                    double temLon = lonArray.get(i).doubleValue();
                    double temLat = latArray.get(j).doubleValue();
                    String prePolygonFormat = "POLYGON((";
                    String sufPolygonFormat = "))";
                    StringBuilder sb = new StringBuilder();
                    sb.append(prePolygonFormat);
                    sb.append(lonlat);
                    sb.append(sufPolygonFormat);
                    String polygonFormat = sb.toString();
                    boolean flag;
                    flag = withinGeo(temLon, temLat, polygonFormat);
                    if (flag) {
                        int id = j * lonArray.size() + i;
                        modifyingIdList.add(id);
                    }
                }
            }
        }
        // 通过文件名进行查询到对于文件信息
        String yyyyMM = filename.substring(0, 6);
        String tablename = commonTable.getPre() + commonTable.getLink() + commonTable.getTypeFusion12h() + commonTable.getLink()
                + yyyyMM + commonTable.getLink() + commonTable.getSufParse();
        Fusion12h detailFileInfio = fusion12hMapper.selectByFilename(tablename, filename);
        String relativePath = detailFileInfio.getPosFile();
        String totalPath = commonPath.getDisk() + commonPath.getCommonPath() + relativePath + filename;
        String fileContent = FileUtils.readCharByPath(totalPath);
        Map<String, Object> sourceDatamap = JSONUtil.parseObj(fileContent);
        // 查询该文件是否已进行修改
        Integer modifiedType = detailFileInfio.getModifyType();
        double[] sourceData = (double[]) ((JSONArray) sourceDatamap.get("data")).toArray(double[].class);
        // 对里面的数据进行修改
        for (Integer modifiedId : modifyingIdList) {
            sourceData[modifiedId] = sourceData[modifiedId] + Double.parseDouble(modifyValue);
        }
        sourceDatamap.put("data", sourceData);
        if (modifiedType == 0) {
            // 表示该文件未进行修改
            filename = filename.replace(".json", "_m") + ".json";
            // 对文件里的文件名称进行修改
            sourceDatamap.put("filename", filename);
            totalPath = commonPath.getDisk() + commonPath.getCommonPath() + relativePath + filename;
            WriteFileUtil.writeFile(JSONUtil.toJsonStr(sourceDatamap), totalPath);
            int forecastCount = (int) ((Long.parseLong(forecastTime) - Long.parseLong(basetime)) / 3600);
            // 对data_fusion12h_modified表里的信息进行修改
            fusion12hMapper.updateModifytype(Long.parseLong(basetime), forecastCount, Integer.parseInt(productType),
                    Integer.parseInt(layer), 1);
            // 对data_fusion12h_modified表里的信息进行修改
            fusion12hMapper.updateNewestfile(Long.parseLong(basetime), Long.parseLong(forecastTime), Integer.parseInt(productType),
                    Integer.parseInt(layer), filename);
            // 数据库新加入一条修改后的数据
            int newFileLength = (int) new File(totalPath).length();
            Fusion12h newFusion12hFile = detailFileInfio;
            newFusion12hFile.setName(filename);
            newFusion12hFile.setFileSize(newFileLength);
            newFusion12hFile.setCreateTime(System.currentTimeMillis() / 1000);
            newFusion12hFile.setModifyType(1);
            fusion12hMapper.insertFusion12h(newFusion12hFile, tablename);
        } else {
            WriteFileUtil.writeFile(JSONUtil.toJsonStr(sourceDatamap), totalPath);
            // 对原数据信息进行修改
            int newFileLength = (int) new File(totalPath).length();
            Fusion12h newFusion12hFile = detailFileInfio;
            newFusion12hFile.setFileSize(newFileLength);
            newFusion12hFile.setCreateTime(System.currentTimeMillis() / 1000);
            fusion12hMapper.updateFusion12h(newFusion12hFile, tablename);
        }
        return CommonResult.success();
    }

    public int getModifyTypeByBasetime(String basetime) {
        Long time = Long.parseLong(basetime);
        return fusion12hMapper.selectModifytype(time);
    }


    /**
     * 判断以x,y为坐标的点point(x,y)是否在geometry表示的Polygon中
     *
     * @param x
     * @param y
     * @param geometry wkt格式
     * @return
     */
    public boolean withinGeo(double x, double y, String geometry) throws ParseException {
        Coordinate coord = new Coordinate(x, y);
        Point point = geometryFactory.createPoint(coord);
        WKTReader reader = new WKTReader(geometryFactory);
        Polygon polygon = (Polygon) reader.read(geometry);
        return point.within(polygon);
    }

    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> finishCorrect(String basetime, String modifyType) {
        if ((!"1".equals(modifyType)) && (!"2".equals(modifyType)) && (!"3".equals(modifyType))) {
            return CommonResult.failed("输入参数有误。。");
        }
        // 查询该起报时间状态
        int modifiedType = getModifyTypeByBasetime(basetime);
        if (modifiedType == 3) {
            return CommonResult.failed("该数据已经被锁定。。");
        }
        if ("3".equals(modifyType)) {
            // 对数据库表信息进行修改
            fusion12hMapper.updateBasetime(Long.parseLong(basetime), Integer.parseInt(modifyType));
            return CommonResult.success();
        }
        if (("1".equals(modifyType) && modifiedType == 0) || ("2".equals(modifyType) && modifiedType == 1)) {
            // 对数据库表信息进行修改
            fusion12hMapper.updateBasetime(Long.parseLong(basetime), Integer.parseInt(modifyType));
            return CommonResult.success();
        } else {
            return CommonResult.failed("不满足锁定条件，锁定失败。");
        }
    }

    /**
     * 解析对应 json 文件，获取对应 ID 下的值：点订正和产品制作的时候都要用到
     *
     * @param lonLat   经纬度
     * @param baseTime 起报时间
     * @param typeEnum 产品制作的时候用到：过滤部分时次的数据
     * @return CommonResult
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> previewPointModified(String lonLat, String baseTime, DataProductTypeEnum typeEnum) {
        List<Fusion12hWeatherInformation> fusion12hWeatherInformations = new ArrayList<>(12);
        long baseTimeLong = Long.parseLong(baseTime);
        Fusion12hBaseTime fusion12hBaseTime = fusion12hMapper.selectBaseTimeByBaseTime(baseTimeLong);
        if (fusion12hBaseTime != null) {
            int modifiedType = fusion12hBaseTime.getModifyType();
            // ① 已经锁定订正的(为的是产品制作时候顺利进行) ② 必须是面订正后才可以点订正
            if (modifiedType == Fusion12hModifiedTypeEnum.TYPE_POLYGON_MODIFIED.getType() ||
                    modifiedType == Fusion12hModifiedTypeEnum.TYPE_LOCKED_MODIFIED.getType()) {
                // 将经纬度解析成 bean
                DicLonLat dicLonLat = JSONUtil.toBean(lonLat, DicLonLat.class, true);
                // ① 替换文件擦操作
                DicLonLat dicLonLatFinal = readFusion12hLonLatJson(dicLonLat);
                Integer lonLatIndex = dicLonLatFinal.getId();
                // 确保 ID 不为空
                if (lonLatIndex != null) {
                    Map<String, Object> result = new HashMap<>(36);
                    // 找到文件
                    List<Fusion12hNewestFile> fusion12hNewestFiles = fusion12hMapper.selectNewestFileByBaseTime(baseTimeLong);
                    if (fusion12hNewestFiles.size() != 0) {
                        for (Fusion12hNewestFile newestFile : fusion12hNewestFiles) {
                            String fileName = newestFile.getName();
                            Integer type = newestFile.getType();
                            DicLevel dicLevel = dicLevelMapper.selectByParentcodeAndCode(TYPE.getType(), type);
                            String nameEn = dicLevel.getNameEn();
                            String timeUtc = TimeStampUtil.timeStampToUtcTimeStr(baseTimeLong, "yyyyMMddHHmmss");
                            String pathNameByPara = commonPathName.getPathNameByPara(TYPE, null, nameEn, timeUtc, commonTable.getSufParse());
                            Long forecastTime = newestFile.getForecastTime();
                            Integer forecastCount = newestFile.getForecastCount();
                            if (typeEnum.equals(DataProductTypeEnum.PRODUCT_TYPE_GRID_FORECAST) ||
                                    typeEnum.equals(DataProductTypeEnum.PRODUCT_TYPE_SHORT_FORECAST) ||
                                    typeEnum.equals(DataProductTypeEnum.PRODUCT_TYPE_LINE_FORECAST)) {
                                if (forecastCount >= 13) {
                                    continue;
                                }
                            }
                            try {
                                File file = new File(pathNameByPara + "/" + fileName);
                                // 将文件解析为对象
                                JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
                                Fusion12hParseJson fusion12hParseJson = JSONUtil.toBean(jsonObject, Fusion12hParseJson.class);
                                List<Double> dataS = fusion12hParseJson.getData();
                                // 找到对应 ID 的值
                                Double value = dataS.get(lonLatIndex);
                                // 查询对应元素对应天气信息
                                Fusion12hWeatherInformation fusion12hWeatherInformation = fusion12hMapper.selectWeatherInformationByParam(forecastTime, forecastCount, baseTimeLong, type, lonLatIndex);
                                if (fusion12hWeatherInformation == null) {
                                    Fusion12hWeatherInformation weatherInformationNew = new Fusion12hWeatherInformation();
                                    weatherInformationNew.setType(String.valueOf(type));
                                    if (type.equals(ProductEnum.FUSION12H_CLOUD.getValue())) {
                                        String cloudCharByValue = Fusion12hWeatherUtil.getCloudCharByValue(String.valueOf(value));
                                        weatherInformationNew.setTypeValue(cloudCharByValue);
                                    } else if (type.equals(ProductEnum.FUSION12H_WIND.getValue())) {
                                        String windCharByValue = Fusion12hWeatherUtil.getWindCharByValue(String.valueOf(value));
                                        weatherInformationNew.setTypeValue(windCharByValue);
                                    } else {
                                        weatherInformationNew.setTypeValue(String.valueOf(value));
                                    }
                                    weatherInformationNew.setForecastCount(forecastCount);
                                    weatherInformationNew.setForecastTime(forecastTime);
                                    weatherInformationNew.setBaseTime(baseTimeLong);
                                    weatherInformationNew.setLonLatIndex(lonLatIndex);
                                    fusion12hWeatherInformations.add(weatherInformationNew);
                                } else {
                                    fusion12hWeatherInformations.add(fusion12hWeatherInformation);
                                }
                            } catch (IORuntimeException e) {
                                return CommonResult.failed("文件查询异常： " + e.getMessage() + " 请仔细检查文件是否完整。");
                            }
                        }
                        result.put("fusion12hWeatherInformation", fusion12hWeatherInformations);
                        return CommonResult.success(result);
                    } else {
                        return CommonResult.failed("数据库未查询到 Fusion12hNewestFile ，请仔细检查数据是否完整。");
                    }
                } else {
                    return CommonResult.failed("所传经度： " + dicLonLat.getLon() + " ，纬度： " + dicLonLat.getLat() + " 未找到对应 ID，请重新检查。");
                }
            } else {
                return CommonResult.failed("该融合数据的订正状态为： " + modifiedType + " ，不满足点订正的要求，请重新检查。");
            }
        } else {
            return CommonResult.failed(baseTime + " 时刻不存在数据，请重新检查。");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> commitPointModified(String lonLat, String baseTime, String fusion12hWeatherInformationJsons) {
        List<Fusion12hWeatherInformation> fusion12hWeatherInformations;
        try {
            JSONObject weatherInformObject = JSONUtil.parseObj(fusion12hWeatherInformationJsons);
            fusion12hWeatherInformations = JSONUtil.toList(weatherInformObject.getJSONArray("fusion12hWeatherInformation"), Fusion12hWeatherInformation.class);
        } catch (Exception e) {
            return CommonResult.failed("解析json出错，请重新检查： " + e.getMessage());
        }
        if (fusion12hWeatherInformations != null) {
            String t = commonTable.getSufParse();
            String modifiedSuffix = "_m";
            for (Fusion12hWeatherInformation weatherInformation : fusion12hWeatherInformations) {
                Long forecastTime = weatherInformation.getForecastTime();
                String format = "yyyyMMddHHmmss";
                String formatTime = TimeStampUtil.timeStampToLocalTimeStr(forecastTime, format);
                String type = weatherInformation.getType();
                String tableName = commonTableName.getTableNameByParam(TYPE, null, formatTime, t);
                List<Fusion12h> fusion12hs = fusion12hMapper.selectByTimeAndType(tableName, forecastTime, type);
                List<Fusion12hNewestFile> fusion12hNewestFiles = fusion12hMapper.selectNewestFileByForecastTimeAndType(forecastTime, type);
                if (fusion12hs.size() != 0) {
                    // 一：如果只有一条，则新增订正后的数据，并新增订正后的文件
                    String posFile = null;
                    String name = null;
                    String nameNew = null;
                    File fileNew = null;
                    int size = 0;
                    Integer forecastCount = null;
                    if (fusion12hs.size() == 1) {
                        // ① 先新增 主体解析表 数据
                        for (Fusion12h fusion12h : fusion12hs) {
                            posFile = fusion12h.getPosFile();
                            name = fusion12h.getName();
                            nameNew = name.replace(name.split("\\.")[0], name.split("\\.")[0] + modifiedSuffix);

                            // 如果没修改则西安修改数据库数据（名称和状态（只有0和1））
                            Fusion12h fusion12hNew = new Fusion12h();
                            BeanUtils.copyProperties(fusion12h, fusion12hNew);
                            fusion12hNew.setName(nameNew);
                            fusion12hNew.setModifyType(Fusion12hModifiedTypeEnum.TYPE_POLYGON_MODIFIED.getType());
                            size = fusion12hMapper.insertFusion12h(fusion12hNew, tableName);
                        }
                        // ② 新增文件
                        if (size > 0) {
                            String pathName = commonPath.getDisk() + commonPath.getCommonPath() + "/" + posFile + "/" + name;
                            String pathNameNew = commonPath.getDisk() + commonPath.getCommonPath() + "/" + posFile + "/" + nameNew;
                            Path path = Paths.get(pathName);
                            Path pathTarget = Paths.get(pathNameNew);
                            try {
                                Files.copy(path, pathTarget);
                                fileNew = new File(pathNameNew);
                            } catch (IOException e) {
                                return CommonResult.failed("文件复制过程出错，请重新检查： " + e.getMessage());
                            }
                        }
                    }
                    for (Fusion12h fusion12h : fusion12hs) {
                        // 因为两天数据存储路径一样，这里只为了获取存储路径
                        posFile = fusion12h.getPosFile();
                        break;
                    }
                    // 二：然后根据查询的 fusion12hNewestFiles（如果数据正确，必须只能有一条数据）
                    if (fusion12hNewestFiles.size() == 1) {
                        for (Fusion12hNewestFile fusion12hNewestFile : fusion12hNewestFiles) {
                            // ③ 继续判断这一条数据是否已经被 面订正 修改过，如修改过了，则不管，如果没有修改，则继续修改
                            String newestFileName = fusion12hNewestFile.getName();
                            forecastCount = fusion12hNewestFile.getForecastCount();
                            if (!newestFileName.contains(modifiedSuffix)) {
                                fusion12hNewestFile.setName(nameNew);
                                size = fusion12hMapper.updateNewestFileByEntity(fusion12hNewestFile);
                            } else {
                                break;
                            }
                        }
                    } else {
                        return CommonResult.failed("fusion12hNewestFile 数据必须只能存在一条，请重新检查。");
                    }
                    // 三：修改 fusion12hModified 表
                    List<Fusion12hModified> fusion12hModifieds = fusion12hMapper.selectModifiedByPara(Long.parseLong(baseTime), type, forecastCount);
                    if (fusion12hModifieds.size() == 1) {
                        for (Fusion12hModified fusion12hModified : fusion12hModifieds) {
                            fusion12hModified.setModifyType(Fusion12hModifiedTypeEnum.TYPE_POINT_MODIFIED.getType());
                            size = fusion12hMapper.updateModifiedByEntity(fusion12hModified);
                        }
                    } else {
                        return CommonResult.failed("fusion12hModified 数据必须只能存在一条，请重新检查。");
                    }
                    // 将经纬度解析成 bean
                    DicLonLat dicLonLat = JSONUtil.toBean(lonLat, DicLonLat.class, true);
                    // 四： 替换文件擦操作
                    DicLonLat dicLonLatFinal = readFusion12hLonLatJson(dicLonLat);
                    Integer lonLatIndex = dicLonLatFinal.getId();
                    // 确保 ID 不为空
                    if (lonLatIndex != null) {
                        // 将文件解析为对象
                        if (fileNew == null) {
                            for (Fusion12h fusion12h : fusion12hs) {
                                name = fusion12h.getName();
                                if (name.contains(modifiedSuffix)) {
                                    nameNew = commonPath.getDisk() + commonPath.getCommonPath() + "/" + posFile + "/" + name;
                                    fileNew = new File(nameNew);
                                    break;
                                }
                            }
                        }
                        if (fileNew != null) {
                            JSONObject jsonObject = JSONUtil.readJSONObject(fileNew, StandardCharsets.UTF_8);
                            Fusion12hParseJson fusion12hParseJsonSource = JSONUtil.toBean(jsonObject, Fusion12hParseJson.class);
                            List<Double> dataSource = fusion12hParseJsonSource.getData();
                            String typeValue = weatherInformation.getTypeValue();
                            if ("2504".equals(type)) {
                                typeValue = (Double.parseDouble(typeValue) * 100) + "";
                            }
                            // 只有当不是修改云量和风的时候替换转化后的 json 值
                            if (!type.equals(ProductEnum.FUSION12H_CLOUD.getValue()) || !type.equals(ProductEnum.FUSION12H_WIND.getValue())) {
                                dataSource.set(lonLatIndex, Double.parseDouble(typeValue));
                            }
                            fusion12hParseJsonSource.setData(dataSource);
                            JSONObject jsonObjectNew = JSONUtil.parseObj(fusion12hParseJsonSource);
                            try {
                                BufferedWriter bw = new BufferedWriter(new FileWriter(fileNew));
                                bw.write(jsonObjectNew.toString());
                                bw.flush();
                                bw.close();
                            } catch (IOException e) {
                                return CommonResult.failed("修改文件出错，请重新检查： " + e.getMessage());
                            }
                            // 五：修改天气情况
                            Fusion12hWeatherInformation weatherInform = fusion12hMapper.selectWeatherInformationByParam(forecastTime, forecastCount, Long.parseLong(baseTime), Integer.parseInt(type), lonLatIndex);
                            if (weatherInform == null) {
                                Fusion12hWeatherInformation weatherInformationNew = new Fusion12hWeatherInformation();
                                weatherInformationNew.setType(type);
                                weatherInformationNew.setTypeValue(typeValue);
                                weatherInformationNew.setForecastCount(forecastCount);
                                weatherInformationNew.setForecastTime(forecastTime);
                                weatherInformationNew.setBaseTime(Long.parseLong(baseTime));
                                weatherInformationNew.setLonLatIndex(lonLatIndex);
                                fusion12hMapper.insertWeatherInformation(weatherInformationNew);
                            } else {
                                weatherInform.setTypeValue(typeValue);
                                fusion12hMapper.updateWeatherInformation(weatherInform);
                            }
                        } else {
                            return CommonResult.failed("修改后的文件为空，请重新检查。");
                        }
                    }
                } else {
                    return CommonResult.failed("数据为空，请重新检查。");
                }
            }
        } else {
            return CommonResult.failed("数据为空，请重新检查。");
        }
        return CommonResult.success();
    }

    /**
     * 读取 fusion12h 的 fusion12h_lonlat_array.json
     *
     * @param dicLonLat 页面传来的实际经纬度
     */
    public DicLonLat readFusion12hLonLatJson(DicLonLat dicLonLat) {
        String lonLatArrayFusion12hPath = commonPath.getLonlatArrayFusion12hPath();
        URL resourceArray = ResourceUtil.getResource(lonLatArrayFusion12hPath);
        DicLonLat lonLatFinal = new DicLonLat();
        try {
            File file = ResourceUtils.getFile(resourceArray);
            JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
            // 遍历得出经纬度相等的 DicLonLat：由于遍历整个 json 效率不高，所以改用简化后的数组 json
            Fusion12hLonLatArray lonLatArray = JSONUtil.toBean(jsonObject, Fusion12hLonLatArray.class);
            List<Double> lons = lonLatArray.getLon();
            int lonIndex = 0;
            List<Double> lats = lonLatArray.getLat();
            int latIndex = 0;

            double min = Double.MAX_VALUE;
            Double lonValue = null;
            for (int i = 0; i < lons.size(); i++) {
                Double lon = lons.get(i);
                final double diff = Math.abs(lon - dicLonLat.getLon());
                if (diff < min) {
                    min = diff;
                    lonValue = lon;
                    lonIndex = i;
                }
            }
            lonLatFinal.setLon(lonValue);
            System.out.println(dicLonLat.getLon() + " 最接近的值是：" + MathUtil.nearest(dicLonLat.getLon(), lons));
            double max = Double.MAX_VALUE;
            Double latValue = null;
            for (int j = 0; j < lats.size(); j++) {
                Double lat = lats.get(j);
                final double diff = Math.abs(lat - dicLonLat.getLat());
                if (diff < max) {
                    max = diff;
                    latValue = lat;
                    latIndex = j;
                }
            }
            lonLatFinal.setLat(latValue);
            // 最终根据经纬度求出 id = lat[j] * lon.size() + lon[i]
            lonLatFinal.setId(latIndex * lons.size() + lonIndex);
            System.out.println("lon******: " + lonIndex + " , lat**********: " + latIndex);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return lonLatFinal;
    }

    /**
     * 解析对应 json 文件，获取对应 ID 下的值：产品制作-获取时间范围内天气信息
     *
     * @param lonLat   经纬度
     * @param baseTime 起报时间
     * @param typeEnum 产品制作的时候用到：过滤部分时次的数据
     * @return CommonResult
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> getWeaInformByTimeRange(String lonLat, String baseTime, DataProductTypeEnum typeEnum) {
        List<Fusion12hWeatherInformation> fusion12hWeatherInformations = new ArrayList<>(12);
        long baseTimeLong = Long.parseLong(baseTime);
        // 将经纬度解析成 bean
        DicLonLat dicLonLat = JSONUtil.toBean(lonLat, DicLonLat.class, true);
        // ① 替换文件擦操作
        DicLonLat dicLonLatFinal = readFusion12hLonLatJson(dicLonLat);
        Integer lonLatIndex = dicLonLatFinal.getId();
        // 确保 ID 不为空
        if (lonLatIndex != null) {
            Map<String, Object> result = new HashMap<>(36);
            // 找到文件
            List<Fusion12hNewestFile> fusion12hNewestFiles = fusion12hMapper.selectNewestFileByTimeRange(baseTimeLong);
            if (fusion12hNewestFiles.size() != 0) {
                for (Fusion12hNewestFile newestFile : fusion12hNewestFiles) {
                    String fileName = newestFile.getName();
                    Integer type = newestFile.getType();
                    DicLevel dicLevel = dicLevelMapper.selectByParentcodeAndCode(TYPE.getType(), type);
                    String nameEn = dicLevel.getNameEn();
                    String timeUtc = TimeStampUtil.timeStampToUtcTimeStr(baseTimeLong, "yyyyMMddHHmmss");
                    String pathNameByPara = commonPathName.getPathNameByPara(TYPE, null, nameEn, timeUtc, commonTable.getSufParse());
                    Long forecastTime = newestFile.getForecastTime();
                    Integer forecastCount = newestFile.getForecastCount();
                    if (typeEnum.equals(DataProductTypeEnum.PRODUCT_TYPE_GRID_FORECAST) ||
                            typeEnum.equals(DataProductTypeEnum.PRODUCT_TYPE_SHORT_FORECAST)) {
                        if (forecastCount >= 13) {
                            continue;
                        }
                    }
                    try {
                        File file = new File(pathNameByPara + "/" + fileName);
                        // 将文件解析为对象
                        JSONObject jsonObject = JSONUtil.readJSONObject(file, StandardCharsets.UTF_8);
                        Fusion12hParseJson fusion12hParseJson = JSONUtil.toBean(jsonObject, Fusion12hParseJson.class);
                        List<Double> dataS = fusion12hParseJson.getData();
                        // 找到对应 ID 的值
                        Double value = dataS.get(lonLatIndex);
                        // 查询对应元素对应天气信息
                        Fusion12hWeatherInformation fusion12hWeatherInformation = fusion12hMapper.selectWeatherInformationByParam(forecastTime, forecastCount, baseTimeLong, type, lonLatIndex);
                        if (fusion12hWeatherInformation == null) {
                            Fusion12hWeatherInformation weatherInformationNew = new Fusion12hWeatherInformation();
                            weatherInformationNew.setType(String.valueOf(type));
                            if (type.equals(ProductEnum.FUSION12H_CLOUD.getValue())) {
                                String cloudCharByValue = Fusion12hWeatherUtil.getCloudCharByValue(String.valueOf(value));
                                weatherInformationNew.setTypeValue(cloudCharByValue);
                            } else if (type.equals(ProductEnum.FUSION12H_WIND.getValue())) {
                                String windCharByValue = Fusion12hWeatherUtil.getWindCharByValue(String.valueOf(value));
                                weatherInformationNew.setTypeValue(windCharByValue);
                            } else {
                                weatherInformationNew.setTypeValue(String.valueOf(value));
                            }
                            weatherInformationNew.setForecastCount(forecastCount);
                            weatherInformationNew.setForecastTime(forecastTime);
                            weatherInformationNew.setBaseTime(baseTimeLong);
                            weatherInformationNew.setLonLatIndex(lonLatIndex);
                            fusion12hWeatherInformations.add(weatherInformationNew);
                        } else {
                            fusion12hWeatherInformations.add(fusion12hWeatherInformation);
                        }
                    } catch (IORuntimeException e) {
                        return CommonResult.failed("文件查询异常： " + e.getMessage() + " 请仔细检查文件是否完整。");
                    }
                }
                result.put("fusion12hWeatherInformation", fusion12hWeatherInformations);
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("数据库未查询到 Fusion12hNewestFile ，请仔细检查数据是否完整。");
            }
        } else {
            return CommonResult.failed("所传经度： " + dicLonLat.getLon() + " ，纬度： " + dicLonLat.getLat() + " 未找到对应 ID，请重新检查。");
        }
    }
}
