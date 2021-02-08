package com.cdyw.swsw.system.service.product;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.component.CommonPathName;
import com.cdyw.swsw.common.common.util.DateUtils;
import com.cdyw.swsw.common.domain.ao.dic.DicDataProductFormColumn;
import com.cdyw.swsw.common.domain.ao.dic.DicDataProductFormColumnSelect;
import com.cdyw.swsw.common.domain.ao.enums.*;
import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.entity.fusion12h.Fusion12hWeatherInformation;
import com.cdyw.swsw.common.domain.entity.product.*;
import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.app.util.*;
import com.cdyw.swsw.system.dao.dic.DicDataProductContentMapper;
import com.cdyw.swsw.system.dao.dic.DicDataProductFormColumnMapper;
import com.cdyw.swsw.system.dao.dic.DicDataProductFormColumnSelectMapper;
import com.cdyw.swsw.system.dao.product.DataProductColumnMapper;
import com.cdyw.swsw.system.dao.product.DataProductContentParamMapper;
import com.cdyw.swsw.system.dao.product.DataProductMapper;
import com.cdyw.swsw.system.security.util.AuthenticationUtil;
import com.cdyw.swsw.system.service.fusion12h.Fusion12hService;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author jovi
 */
@CacheConfig(keyGenerator = "keyGenerator")
@Scope(value = "prototype")
@Service
public class DataProductService {
    private final CommonPath commonPath;
    private final CommonPathName commonPathName;
    private final DataProductMapper dataProductMapper;
    private final DicDataProductContentMapper dicDataProductContentMapper;
    private final DataProductContentParamMapper dataProductContentParamMapper;
    private final DataProductColumnMapper dataProductColumnMapper;
    private final DicDataProductFormColumnMapper dicDataProductFormColumnMapper;
    private final DicDataProductFormColumnSelectMapper dicDataProductFormColumnSelectMapper;
    private final FilePythonConvertUtil filePythonConvertUtil;
    private final FilePoiConvertUtil filePoiConvertUtil;
    private final FileOpenOfficeConvertUtil fileOpenOfficeConvertUtil;
    private final FileLibreOfficeConvertUtil fileLibreOfficeConvertUtil;
    private final AuthenticationUtil authenticationUtil;
    private final Fusion12hService fusion12hService;
    private final Integer previewFlag;
    private final Integer commitFlag;
    private final Integer downloadFlag;
    private final Integer flag6;
    private final Integer flag12;
    private final Integer textFlag6;
    private final Integer textFlag12;
    // TODO: remove this in future
    private final List<Fusion12hWeatherInformation> fusion12hWeatherInformation = new ArrayList<>();
    @JsonPropertyOrder(value = {"columnKey", "columnName", "columnSelect", "selectFlag"})
    private final Map<String, Object> dataProductFormMap = new HashMap<>(100);
    // TODO: remove this in future
    private List<Fusion12hWeatherInformation> fusion12hWeatherInformations = new ArrayList<>();

    {
        previewFlag = 1;
        commitFlag = 2;
        downloadFlag = 3;
        flag6 = 1;
        flag12 = 2;
        textFlag6 = 4;
        textFlag12 = 5;
    }

    @Autowired
    public DataProductService(CommonPath commonPath, CommonPathName commonPathName, DicDataProductContentMapper dicDataProductContentMapper, DataProductMapper dataProductMapper, DataProductContentParamMapper dataProductContentParamMapper, DataProductColumnMapper dataProductColumnMapper, DicDataProductFormColumnMapper dicDataProductFormColumnMapper, DicDataProductFormColumnSelectMapper dicDataProductFormColumnSelectMapper, FilePythonConvertUtil filePythonConvertUtil, FilePoiConvertUtil filePoiConvertUtil, FileOpenOfficeConvertUtil fileOpenOfficeConvertUtil, FileLibreOfficeConvertUtil fileLibreOfficeConvertUtil, AuthenticationUtil authenticationUtil, Fusion12hService fusion12hService) {
        this.commonPath = commonPath;
        this.commonPathName = commonPathName;
        this.dicDataProductContentMapper = dicDataProductContentMapper;
        this.dataProductMapper = dataProductMapper;
        this.dataProductContentParamMapper = dataProductContentParamMapper;
        this.dataProductColumnMapper = dataProductColumnMapper;
        this.dicDataProductFormColumnMapper = dicDataProductFormColumnMapper;
        this.dicDataProductFormColumnSelectMapper = dicDataProductFormColumnSelectMapper;
        this.filePythonConvertUtil = filePythonConvertUtil;
        this.filePoiConvertUtil = filePoiConvertUtil;
        this.fileOpenOfficeConvertUtil = fileOpenOfficeConvertUtil;
        this.fileLibreOfficeConvertUtil = fileLibreOfficeConvertUtil;
        this.authenticationUtil = authenticationUtil;
        this.fusion12hService = fusion12hService;
    }

    public CommonResult<?> getDataByParam(DataProduct dataProduct) {
        List<DataProduct> dataProducts;
        List<String> paraWarningLevels = new ArrayList<>(2);
        dataProducts = dataProductMapper.selectDataByParam(dataProduct);
        if (dataProducts.size() != 0) {
            for (DataProduct dp : dataProducts) {
                DataProductContentParam dataProductContentParam = dataProductContentParamMapper.selectById(dp.getId());
                Optional.ofNullable(dataProductContentParam).map(para -> {
                    paraWarningLevels.add(para.getParaWarningLevel());
                    paraWarningLevels.add(para.getParaWarningLevel2());
                    return paraWarningLevels;
                });
                dp.setParaWarningLevels(paraWarningLevels);
            }
            return CommonResult.success(dataProducts);
        }
        return CommonResult.failed();
    }

    public CommonResult<?> getDataProductDocById(Integer id, HttpServletResponse response, boolean isOnLine) throws IOException, InterruptedException {
        File filePdfById = null;
        DataProduct dataProduct = dataProductMapper.selectByPrimaryKey(id);
        if (dataProduct != null) {
            filePdfById = getFilePdfById(dataProduct.getId());
        }
        // 如果查询出来没有该pdf文件则继续转换
        if (!filePdfById.exists()) {
            File fileById = getFileById(dataProduct.getId());
            filePdfById = filePythonConvertUtil.file2Pdf(fileById, fileById.getParent());
        }
        if (filePdfById.exists()) {
            com.cdyw.swsw.system.vo.common.FileUtil.downloadOrOnlineFile(filePdfById, response, isOnLine);
            return CommonResult.success();
        }
        return CommonResult.failed("该文件不存在");
    }

    public CommonResult<?> updateDataProduct(DataProduct dataProduct) {
        int update = dataProductMapper.updateByPrimaryKey(dataProduct);
        if (update != 0) {
            return CommonResult.success(dataProduct);
        }
        return CommonResult.failed();
    }

    public CommonResult<?> preview(DataProductContentParam dataProductContentParam, HttpServletResponse response, boolean isOnLine) throws IOException, InterruptedException {
        FileStatusEnum fileStatusEnum = FileStatusEnum.FILE_STATUS_TEMP;
        File file2Pdf = handleFile2Pdf(fileStatusEnum, dataProductContentParam);
        if (file2Pdf.exists()) {
            com.cdyw.swsw.system.vo.common.FileUtil.downloadOrOnlineFile(file2Pdf, response, isOnLine);
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    public CommonResult<?> closePreview(Integer id) {
        boolean flag = false;
        FileStatusEnum fileStatusEnum = FileStatusEnum.FILE_STATUS_TEMP;
        DataProduct dataProduct = dataProductMapper.selectByPrimaryKey(id);
        String productPath = dataProduct.getProductPath();
        String[] productPaths = productPath.split("/");
        String pathEnd = productPaths[productPaths.length - 1];
        productPath = productPath.replace(pathEnd, fileStatusEnum.getStatus());
        File destFilePath = new File(productPath);
        // 删除整个临时文件夹下面的文件
        if (destFilePath.isFile()) {
            flag = destFilePath.delete();
        } else if (destFilePath.isDirectory()) {
            flag = FileUtil.clean(destFilePath);
        }
        if (flag) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    public CommonResult<?> publish(DataProductContentParam dataProductContentParam) throws IOException, InterruptedException {
        // 获取当前用户信息
        SysUserEntity currentUser = authenticationUtil.getCurrentUser();
        int numFinal = 4;
        int num = 0;
        // ① 保存文件
        FileStatusEnum fileStatusEnum = FileStatusEnum.FILE_STATUS_FINAL;
        File file2Pdf = handleFile2Pdf(fileStatusEnum, dataProductContentParam);
        if (file2Pdf != null) {
            num += 1;
        }
        // ② 保存参数 DataProductContentParam （因为产品的列不会随着参数而改变，所以只保存列对应的参数就可以）；新建表保存
        num += dataProductContentParamMapper.insertFinal(dataProductContentParam);
        // ③ 更新产品的发布状态和发布时间字段
        DataProduct dataProduct = dataProductMapper.selectByPrimaryKey(dataProductContentParam.getDataProductId());
        dataProduct.setPublishStatus(DataProductStatusEnum.STATUS_SWSW_APPROVING.getStatus());
        num += dataProductMapper.updateByPrimaryKey(dataProduct);
        // ④ 增加审批功能
        DataProductTypeEnum[] values = DataProductTypeEnum.values();
        Integer type = dataProduct.getProductType();
        String typeName = null;
        for (DataProductTypeEnum typeEnum : values) {
            if (type.equals(typeEnum.getType())) {
                typeName = typeEnum.getTypeName();
                break;
            }
        }
        String category = type + "-" + typeName;
        String deploymentName = dataProduct.getId() + "-" + category;
        // 1： 部署流程
//        Deployment deployment = activityService.createDeployment(deploymentName, category);
        // 2： 启动流程
//        ProcessInstance processInstance = null;
//        if (deployment != null) {
//            processInstance = activityService.startProcessInstance(currentUser, deployment.getId());
//        }
//        if (processInstance != null) {
//            num += 1;
//        }
        // 必须完成 4 个步骤，将最终生成的流程实例返回给前端即可
        if (num == numFinal) {
//            return CommonResult.success(processInstance);
        }
        return CommonResult.failed();
    }

    public CommonResult<?> getDataProductFormById(Integer id) {
        DataProduct dataProduct = dataProductMapper.selectByPrimaryKey(id);
        Integer productType = dataProduct.getProductType();
        // 先申明一个 id 下存放的 column
        List<Map<String, Object>> dataProductFormColumnList = new ArrayList<>();
        DataProductColumn dataProductColumn = dataProductColumnMapper.selectByDataProductId(id);
        String[] columnIds = dataProductColumn.getColumnIds().split(",");
        List<String> columnIdList = ListUtil.toList(columnIds);
        String[] columnIdRemoves = {"22", "23", "24", "25", "26", "27"};
        List<String> columnIdRemoveList = ListUtil.toList(columnIdRemoves);
        // Arrays 慎用！！！
        ListUtil.toList(columnIdRemoves);
        // 排除跟“人”相关的列，交由另外接口实现组装
        columnIdList.removeAll(columnIdRemoveList);
        for (String colId : columnIdList) {
            Map<String, Object> dataProductFormColumnMap = new HashMap<>(21);
            DicDataProductFormColumn dicDataProductFormColumn;
            DicDataProductFormColumnSelect dicDataProductFormColumnSelect;
            dicDataProductFormColumn = dicDataProductFormColumnMapper.selectByPrimaryKey(Integer.valueOf(colId));
            String columnKey = dicDataProductFormColumn.getColumnKey();
            dataProductFormColumnMap.put("columnKey", columnKey);
            dataProductFormColumnMap.put("columnName", dicDataProductFormColumn.getColumnName());
            dicDataProductFormColumnSelect = dicDataProductFormColumnSelectMapper.selectByColumnId(colId);
            String columnSelect = dicDataProductFormColumnSelect.getColumnSelect();
            String columnStadiumSelect = dicDataProductFormColumnSelect.getColumnStadiumSelect();
            String[] columnSelects = null;
            if (!"".equals(columnSelect) && "".equals(columnStadiumSelect)) {
                columnSelects = columnSelect.split(",");
            } else if (columnSelect == null && columnStadiumSelect != null) {
                columnSelects = columnStadiumSelect.split(",");
            }
            dataProductFormColumnMap.put("columnSelect", columnSelects == null ? "" : columnSelects);
            String selectFlag = dicDataProductFormColumnSelect.getSelectFlag();
            // 暂时只有“受影响的体育场馆”为空，因为存在两种可能：单选或者多选
            if (selectFlag == null) {
                if (productType.equals(DataProductTypeEnum.TYPE_SWSW_2_6.getType())) {
                    selectFlag = "2";
                } else {
                    selectFlag = "1";
                }
            }
            dataProductFormColumnMap.put("selectFlag", selectFlag);
            DataProductContentParam dataProductContentParam = dataProductContentParamMapper.selectById(id);
            List<String> columnDefaults = new ArrayList<>();
            DataProductContentParamEnum[] values = DataProductContentParamEnum.values();
            for (DataProductContentParamEnum paramEnum : values) {
                String temKey = StrUtil.subBetween(paramEnum.getKey(), "${", "}");
                if (columnKey.equals(temKey)) {
                    if (paramEnum.getKey().contains("paraPublishTime")) {
                        columnDefaults.add(dataProductContentParam.getParaPublishTime());
                    } else if (paramEnum.getKey().contains("paraWarningStartTime")) {
                        columnDefaults.add(dataProductContentParam.getParaWarningStartTime());
                    } else if (paramEnum.getKey().contains("paraWarningEndTime")) {
                        columnDefaults.add(dataProductContentParam.getParaWarningEndTime());
                    } else {
                        columnDefaults.add(paramEnum.getValue());
                    }
                    break;
                }
            }
            dataProductFormColumnMap.put("columnDefault", columnDefaults);
            dataProductFormColumnList.add(dataProductFormColumnMap);
        }
        dataProductFormMap.put("form", dataProductFormColumnList);
        return CommonResult.success(dataProductFormMap);
    }

    @Transactional(rollbackFor = {Exception.class})
    public CommonResult<?> insert() throws IOException, InterruptedException {
        int size = 1;
        int insert = 0;
        FileStatusEnum fileStatusEnum = FileStatusEnum.FILE_STATUS_DEFAULT;
        File file2Pdf = null;
        String path = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getDataProductPath();
        LocalDateTime currentDate = LocalDateTime.now();
        String minFormat = DateUtil.format(currentDate, "yyyy-MM-dd 00:00:00");
        String maxFormat = DateUtil.format(currentDate, "yyyy-MM-dd 23:59:59");
        long minTime = LocalDateTime.parse(minFormat, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toEpochSecond(ZoneOffset.of("+8"));
        long maxTime = LocalDateTime.parse(maxFormat, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toEpochSecond(ZoneOffset.of("+8"));
        for (int i = 0; i < size; i++) {
            DataProduct dataProduct = new DataProduct();
            DataProductContentParam dataProductContentParam = new DataProductContentParam();
//            int productType = RandomUtil.randomInt(DataProductTypeEnum.TYPE_SWSW_2_1.getType(), DataProductTypeEnum.TYPE_SWSW_12_12.getType());
            int productType = 1;
            long randomTime = RandomUtil.randomLong(minTime, maxTime);
            dataProduct.setProductType(productType);
            // 文档存储路径为：D:Data + yyyyMMdd + productType
            dataProduct.setProductPath(path + TimeStampUtil.timeStampToYyyyMmDd(Long.toString(randomTime)) + "/" + productType + "/" + fileStatusEnum.getStatus() + "/");
            if (!FileUtil.exist(dataProduct.getProductPath())) {
                FileUtil.mkdir(dataProduct.getProductPath());
            }
            dataProduct.setCreateTime(randomTime);
            // 发布状态只跟那些“人”和“单位”参数相关，其他不受影响
            if (true) {
                dataProduct.setPublishStatus(DataProductStatusEnum.STATUS_SWSW_NO_PUBLISH.getStatus());
            } else {
                dataProduct.setPublishStatus(DataProductStatusEnum.STATUS_SWSW_PUBLISHED.getStatus());
                dataProductContentParam.setParaPublishTime(TimeStampUtil.timeStampToTimeStr(randomTime, DataProductContentParamEnum.PARA_PUBLISH_TIME.getValue()));
                dataProductContentParam.setParaPublishDept(DataProductContentParamEnum.PARA_PUBLISH_DEPT.getValue());
                dataProductContentParam.setParaSendDept(DataProductContentParamEnum.PARA_SEND_DEPT.getValue());
                dataProductContentParam.setParaForecaster(DataProductContentParamEnum.PARA_FORECASTER.getValue());
                dataProductContentParam.setParaApprover(DataProductContentParamEnum.PARA_APPROVER.getValue());
                dataProductContentParam.setParaSigner(DataProductContentParamEnum.PARA_SIGNER.getValue());
            }
            // 接下来按照产品类型赋值（总体分为 6 大类类型）
            if (productType == DataProductTypeEnum.TYPE_SWSW_2_1.getType() ||
                    productType == DataProductTypeEnum.TYPE_SWSW_2_2.getType() ||
                    productType == DataProductTypeEnum.TYPE_SWSW_2_3.getType() ||
                    productType == DataProductTypeEnum.TYPE_SWSW_2_4.getType()) {
                dataProductContentParam.setParaDisasterType(DataProductContentParamEnum.PARA_DISASTER_TYPE.getValue());
                dataProductContentParam.setParaWarningLevel(DataProductContentParamEnum.PARA_WARNING_LEVEL.getValue());
                dataProductContentParam.setParaAffectedArea(DataProductContentParamEnum.PARA_AFFECTED_AREA.getValue());
                dataProductContentParam.setParaAppearedWeather(DataProductContentParamEnum.PARA_APPEARED_WEATHER.getValue());
                dataProductContentParam.setParaAppearingTime(DataProductContentParamEnum.PARA_APPEARING_TIME.getValue());
                dataProductContentParam.setParaAffectingArea(DataProductContentParamEnum.PARA_AFFECTING_AREA.getValue());
                dataProductContentParam.setParaDisasterPhenomenon1(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON1.getValue());
                dataProductContentParam.setParaDisasterPhenomenon2(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON2.getValue());
            } else if (productType == DataProductTypeEnum.TYPE_SWSW_2_5.getType() ||
                    productType == DataProductTypeEnum.TYPE_SWSW_2_6.getType()) {
                dataProductContentParam.setParaAppearedWeather(DataProductContentParamEnum.PARA_APPEARED_WEATHER.getValue());
                dataProductContentParam.setParaAppearingTime(DataProductContentParamEnum.PARA_APPEARING_TIME.getValue());
                dataProductContentParam.setParaDisasterPhenomenon1(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON1.getValue());
                dataProductContentParam.setParaDisasterPhenomenon2(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON2.getValue());
                dataProductContentParam.setParaAffectedStadium(DataProductContentParamEnum.PARA_AFFECTED_STADIUM.getValue());
                dataProductContentParam.setParaWarningStartTime(TimeStampUtil.timeStampToTimeStr(randomTime, DataProductContentParamEnum.PARA_WARNING_START_TIME.getValue()));
                dataProductContentParam.setParaWarningEndTime(TimeStampUtil.timeStampToTimeStr(randomTime, DataProductContentParamEnum.PARA_WARNING_END_TIME.getValue()));
            } else if (productType == DataProductTypeEnum.TYPE_SWSW_2_7.getType()) {
                dataProductContentParam.setParaAppearedWeather(DataProductContentParamEnum.PARA_APPEARED_WEATHER.getValue());
                dataProductContentParam.setParaAppearingTime(DataProductContentParamEnum.PARA_APPEARING_TIME.getValue());
                dataProductContentParam.setParaAffectingArea(DataProductContentParamEnum.PARA_AFFECTING_AREA.getValue());
                dataProductContentParam.setParaDisasterPhenomenon1(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON1.getValue());
                dataProductContentParam.setParaDisasterPhenomenon2(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON2.getValue());
                dataProductContentParam.setParaAffectedEvent("铁人三项赛事");
            } else if (productType == DataProductTypeEnum.TYPE_SWSW_2_8.getType()) {
                dataProductContentParam.setParaAppearedWeather(DataProductContentParamEnum.PARA_APPEARED_WEATHER.getValue());
                dataProductContentParam.setParaAppearingTime(DataProductContentParamEnum.PARA_APPEARING_TIME.getValue());
                dataProductContentParam.setParaAffectingArea(DataProductContentParamEnum.PARA_AFFECTING_AREA.getValue());
                dataProductContentParam.setParaDisasterPhenomenon1(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON1.getValue());
                dataProductContentParam.setParaDisasterPhenomenon2(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON2.getValue());
                dataProductContentParam.setParaAffectedEvent("火炬传递运动");
            } else if (productType == DataProductTypeEnum.TYPE_SWSW_12_9.getType() ||
                    productType == DataProductTypeEnum.TYPE_SWSW_12_10.getType()) {
                dataProductContentParam.setParaAppearingTime(DataProductContentParamEnum.PARA_APPEARING_TIME.getValue());
                dataProductContentParam.setParaAffectedStadium(DataProductContentParamEnum.PARA_AFFECTED_STADIUM.getValue());
                dataProductContentParam.setParaAffectedEvent(DataProductContentParamEnum.PARA_AFFECTED_EVENT.getValue());
            } else if (productType == DataProductTypeEnum.TYPE_SWSW_12_11.getType()) {
                dataProductContentParam.setParaDisasterType(DataProductContentParamEnum.PARA_DISASTER_TYPE.getValue());
                dataProductContentParam.setParaWarningLevel(DataProductContentParamEnum.PARA_WARNING_LEVEL.getValue());
                dataProductContentParam.setParaDisasterType2(DataProductContentParamEnum.PARA_DISASTER_TYPE2.getValue());
                dataProductContentParam.setParaWarningLevel2(DataProductContentParamEnum.PARA_WARNING_LEVEL2.getValue());
                dataProductContentParam.setParaAffectedArea(DataProductContentParamEnum.PARA_AFFECTED_AREA.getValue());
                dataProductContentParam.setParaAppearedWeather(DataProductContentParamEnum.PARA_APPEARED_WEATHER.getValue());
                dataProductContentParam.setParaAppearingTime(DataProductContentParamEnum.PARA_APPEARING_TIME.getValue());
                dataProductContentParam.setParaAffectingArea(DataProductContentParamEnum.PARA_AFFECTING_AREA.getValue());
                dataProductContentParam.setParaDisasterPhenomenon1(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON1.getValue());
                dataProductContentParam.setParaDisasterPhenomenon2(DataProductContentParamEnum.PARA_DISASTER_PHENOMENON2.getValue());
            } else {
                dataProductContentParam.setParaDisasterType(DataProductContentParamEnum.PARA_DISASTER_TYPE.getValue());
                dataProductContentParam.setParaWarningLevel(DataProductContentParamEnum.PARA_WARNING_LEVEL.getValue());
            }
            dataProductContentParam.setParaNumber(Integer.parseInt(DataProductContentParamEnum.PARA_NUMBER.getValue()));
            // 根据产品类型和所有参数，替换所对应模板的每个参数然后组合成一个字符串返回
            String content = getReplaceContent(productType, dataProductContentParam);
            dataProduct.setProductContent(content);
            insert += dataProductMapper.insert(dataProduct);
            // 新增完 dataProduct 后生成 id，再生成 dataProduct 参数表
            dataProductContentParam.setDataProductId(dataProduct.getId());
            insert += dataProductContentParamMapper.insert(dataProductContentParam);
            // 继续生成 dataProduct 对应的表单列（需要传递给前端渲染成页面表单）
            DataProductColumn dataProductColumn = new DataProductColumn();
            dataProductColumn.setDataProductId(dataProduct.getId());
            List<DicDataProductFormColumn> dicDataProductFormColumns = dicDataProductFormColumnMapper.selectByType(dataProduct.getProductType().toString());
            // 将查询到的列名集合拼接成字符串
            StringBuilder stringBuilder = new StringBuilder();
            if (dicDataProductFormColumns.size() != 0) {
                for (DicDataProductFormColumn dicDataProductFormColumn : dicDataProductFormColumns) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(dicDataProductFormColumn.getId());
                }
            }
            dataProductColumn.setColumnIds(stringBuilder.toString());
            insert += dataProductColumnMapper.insert(dataProductColumn);
            file2Pdf = handleFile2Pdf(fileStatusEnum, dataProductContentParam);
        }
        // 必须创建 3 条数据才算正确
        if (insert != 3 && !file2Pdf.exists()) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(insert);
        }
    }

    /**
     * 专门用来替换概况的参数
     *
     * @param dataProductContentParam DataProductContentParam
     * @return String
     * @modified 修改后：滚动展示和文档展示内容改为一致
     */
    private String getReplaceContent(Integer productType, DataProductContentParam dataProductContentParam) {
        List<String> paras = new ArrayList<>(21);
        String content = dicDataProductContentMapper.selectByType(productType);
        Matcher matcher = matcher(content);
        while (matcher.find()) {
            paras.add(matcher.group());
        }
        DataProductContentParamEnum[] values = DataProductContentParamEnum.values();
        if (paras.size() != 0) {
            // 根据对应 key 匹配对应实际 value
            for (String para : paras) {
                for (DataProductContentParamEnum paramEnum : values) {
                    if (para.equals(paramEnum.getKey())) {
                        // 单独处理时间(避免未发布的产品)
                        if (para.contains("paraPublishTime")) {
                            // 避免直接使用 String 的 replace() 方法，因为会出现空指针，所以用 hutool 工具类
                            content = StrUtil.replace(content, para, dataProductContentParam.getParaPublishTime());
                        } else if (para.contains("paraWarningStartTime")) {
                            content = StrUtil.replace(content, para, dataProductContentParam.getParaWarningStartTime());
                        } else if (para.contains("paraWarningEndTime")) {
                            content = StrUtil.replace(content, para, dataProductContentParam.getParaWarningEndTime());
                        } else {
                            content = StrUtil.replace(content, para, paramEnum.getValue());
                        }
                        break;
                    }
                }
            }
        }
        return content;
    }

    /**
     * 正则匹配字符串： ${xxx}
     */
    private Matcher matcher(String str) {
        String reg = "\\$\\{[^{}]+\\}";
        String reg1 = "\\$\\{[a-zA-Z0-9]+\\}";
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str);
    }

    /**
     * 处理文档相关，最终转化成 pdf
     *
     * @param fileStatusEnum 标志，1：default 新增-默认 2：temp 浏览-临时 3：final 发布-最终
     */
    private File handleFile2Pdf(FileStatusEnum fileStatusEnum, DataProductContentParam dataProductContentParam) throws IOException, InterruptedException {
        DataProduct dataProduct = dataProductMapper.selectByPrimaryKey(dataProductContentParam.getDataProductId());
        String productPath = dataProduct.getProductPath();
        String[] productPaths = productPath.split("/");
        String pathEnd = productPaths[productPaths.length - 1];
        productPath = productPath.replace(pathEnd, fileStatusEnum.getStatus());
        dataProduct.setProductPath(productPath);
        boolean fileFlag = false;
        File file2Pdf = null;
        String templatePath = commonPath.getDisk() + commonPath.getCommonPath() + commonPath.getDataProductTemplatePath();
        // ① 复制模板并根据产品　ID　重命名
        File srcTemplatePathFile = new File(templatePath);
        String srcPathFileName = null;
        File srcFile = null;
        if (srcTemplatePathFile.exists()) {
            if (srcTemplatePathFile.isDirectory()) {
                String[] srcFileNames = srcTemplatePathFile.list();
                if (srcFileNames != null) {
                    for (String srcFileName : srcFileNames) {
                        // 确保模板名称不能随意修改
                        if (srcFileName.split("\\.")[0].equals(String.valueOf(dataProduct.getProductType()))) {
                            srcPathFileName = srcFileName;
                            break;
                        }
                    }
                }
            }
        }
        if (srcPathFileName != null) {
            srcFile = new File(templatePath + srcPathFileName);
        }
        File destFilePath = new File(dataProduct.getProductPath());
        if (!destFilePath.exists()) {
            fileFlag = destFilePath.mkdir();
        }
        // copy方法得到的是dest路径，并不是dest文件
        File copyDestFile = FileUtil.copy(srcFile, destFilePath, false);
        copyDestFile = new File(copyDestFile.getPath() + "/" + srcPathFileName);
        File renameCopyDestFile = new File(copyDestFile.getParent() + "/" + dataProduct.getId() + "_" + copyDestFile.getName());
        fileFlag = copyDestFile.renameTo(renameCopyDestFile);
        // ②　替换该产品对应文档内的参数
        File dest = new File(renameCopyDestFile.getPath());
        Map<String, Object> map = new HashMap<>(21);
        // 利用反射
        Field[] fields = dataProductContentParam.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            Object fieldValueByName = getFieldValueByName(fieldName, dataProductContentParam);
            if (fieldValueByName != null) {
                map.put("${" + fieldName + "}", fieldValueByName);
            }
        }
        File fileModified = com.cdyw.swsw.system.vo.common.FileUtil.modifyFileText(renameCopyDestFile, dest, map, null);
        // ③　替换完成后将　word　转化成　pdf
        if (fileModified != null) {
//            File file2Pdf = fileOpenOfficeConvertUtil.file2Pdf(fileById, fileById.getParent());
//            File file2Pdf = fileLibreOfficeConvertUtil.file2Pdf(fileById, fileById.getParent());
            file2Pdf = filePythonConvertUtil.file2Pdf(fileModified, fileModified.getParent());
        }
        return file2Pdf;
    }

    /**
     * 根据产品Id查询其所对应的实体 docx 文档全路径
     *
     * @param id Integer
     * @return String
     */
    private File getFileById(Integer id) {
        File dataProductFilePath;
        File dataProductFile = null;
        DataProduct dataProduct = dataProductMapper.selectByPrimaryKey(id);
        if (dataProduct != null) {
            dataProductFilePath = new File(dataProduct.getProductPath());
            if (dataProductFilePath.exists()) {
                if (dataProductFilePath.isDirectory()) {
                    File[] files = dataProductFilePath.listFiles();
                    if (files != null && files.length != 0) {
                        for (File file : files) {
                            if (file.getName().startsWith(String.valueOf(id)) && file.getName().endsWith(FileTypeEnum.FILE_TYPE_DOCX.getSuffix())) {
                                dataProductFile = file;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return dataProductFile;
    }

    /**
     * 根据产品Id查询其所对应的实体 pdf 文档全路径
     *
     * @param id Integer
     * @return String
     */
    private File getFilePdfById(Integer id) {
        File dataProductFilePath;
        File dataProductFile = null;
        DataProduct dataProduct = dataProductMapper.selectByPrimaryKey(id);
        if (dataProduct != null) {
            dataProductFilePath = new File(dataProduct.getProductPath());
            if (dataProductFilePath.exists()) {
                if (dataProductFilePath.isDirectory()) {
                    File[] files = dataProductFilePath.listFiles();
                    if (files != null && files.length != 0) {
                        for (File file : files) {
                            if (file.getName().startsWith(String.valueOf(id)) && file.getName().endsWith(FileTypeEnum.FILE_TYPE_PDF.getSuffix())) {
                                dataProductFile = file;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return dataProductFile;
    }

    /**
     * 根据属性名获取属性值（主要针对 get 方法）
     */
    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Cacheable(value = {"dataProduct"})
    public CommonResult<?> getDataProductByParam(String productType, String baseTime, String lonLat, Integer flag, String line) {
        Map<String, Object> result = new HashMap<>(3);
        Integer type = Integer.valueOf(productType);
        DataProductTypeEnum typeEnum = getDataProductTypeEnum(type);
        if (typeEnum != null) {
            DataProductBase dataProductBase = getDataProductBase(baseTime, type);
            List<DataProductBase> dataProductHistories = dataProductMapper.getHistoryDataProductByTime(Long.parseLong(baseTime), type);
            switch (typeEnum) {
                case PRODUCT_TYPE_SHORT_FORECAST:
                    dataProductBase.setSubtitle(DataProductBaseEnum.PRODUCT_BASE_SUBTITLE.getDefaultValue());
                    dataProductBase.setForecaster(DataProductBaseEnum.PRODUCT_BASE_FORECASTER.getDefaultValue());
                    dataProductBase.setSigner(DataProductBaseEnum.PRODUCT_BASE_SIGNER.getDefaultValue());
                    dataProductBase.setHours(DataProductBaseEnum.PRODUCT_BASE_HOURS.getDefaultValue());
                    result.put("dataProductBase", dataProductBase);
                    result.put("dataProductHistory", dataProductHistories);
                    DataProductWeaShortForecast dataProductWeaShortForecast = new DataProductWeaShortForecast();
                    // TODO 因为目前没有云量，所以提前看看暂时默认取值。后期有值后，假如说查询14点0-6H天气情况，则取中间值17点数据
                    dataProductWeaShortForecast.setText6(DataProductBaseEnum.PRODUCT_WEA_TEXT.getDefaultValue());
                    dataProductWeaShortForecast.setText12(DataProductBaseEnum.PRODUCT_WEA_TEXT.getDefaultValue());
                    long parseLong = Long.parseLong(baseTime);
                    Float typeValueDefault = 0.0f;
                    // 如果默认置空或者按钮类型为 1 ，则表示起报时间为 0-6H
                    if (flag == null) {
                        dataProductWeaShortForecast.setTemMin6(typeValueDefault);
                        dataProductWeaShortForecast.setTemMax6(typeValueDefault);
                        dataProductWeaShortForecast.setTemMin12(typeValueDefault);
                        dataProductWeaShortForecast.setTemMax12(typeValueDefault);
                        dataProductWeaShortForecast.setTextFusion6("");
                        dataProductWeaShortForecast.setTextFusion12("");
                    } else if (flag12.equals(flag)) {
                        baseTime = String.valueOf(DateUtils.getTimePlus(parseLong, 6));
                    }
                    fusion12hWeatherInformations = getWeaInforms(typeEnum, baseTime, lonLat);
                    List<Float> temValues = new LinkedList<>();
                    List<Float> windValues = new LinkedList<>();
                    if (fusion12hWeatherInformations != null) {
                        for (Fusion12hWeatherInformation information : fusion12hWeatherInformations) {
                            String typeCurrent = information.getType();
                            String typeValueCurrent = information.getTypeValue();
                            if (Integer.valueOf(typeCurrent).equals(ProductEnum.FUSION12H_TEM.getValue())) {
                                temValues.add(Float.parseFloat(typeValueCurrent));
                            } else if (Integer.valueOf(typeCurrent).equals(ProductEnum.FUSION12H_WIND.getValue())) {
                                windValues.add(Float.parseFloat(typeValueCurrent));
                            }
                        }
                    }
                    if (!temValues.isEmpty()) {
                        temValues.sort(Float::compareTo);
                        if (flag6.equals(flag)) {
                            dataProductWeaShortForecast.setTemMin6(temValues.get(0));
                            dataProductWeaShortForecast.setTemMax6(temValues.get(temValues.size() - 1));
                        } else if (flag12.equals(flag)) {
                            dataProductWeaShortForecast.setTemMin12(temValues.get(0));
                            dataProductWeaShortForecast.setTemMax12(temValues.get(temValues.size() - 1));
                        }
                    } else {
                        dataProductWeaShortForecast.setTemMin6(typeValueDefault);
                        dataProductWeaShortForecast.setTemMax6(typeValueDefault);
                        dataProductWeaShortForecast.setTemMin12(typeValueDefault);
                        dataProductWeaShortForecast.setTemMax12(typeValueDefault);
                    }
                    if (!windValues.isEmpty()) {
                        windValues.sort(Float::compareTo);
                        if (flag6.equals(flag)) {
                            dataProductWeaShortForecast.setWinSpeedMin6(windValues.get(0));
                            dataProductWeaShortForecast.setWinSpeedMax6(windValues.get(windValues.size() - 1));
                            // TODO 目前没有风的数据，所以风向暂时置空，后期有数据后，单独写程序根据风速值进行判断风向
                            dataProductWeaShortForecast.setWinDirection6("");
                        } else if (flag12.equals(flag)) {
                            dataProductWeaShortForecast.setWinSpeedMin12(windValues.get(0));
                            dataProductWeaShortForecast.setWinSpeedMax12(windValues.get(windValues.size() - 1));
                            dataProductWeaShortForecast.setWinDirection12("");
                        }
                    } else {
                        dataProductWeaShortForecast.setWinSpeedMin6(typeValueDefault);
                        dataProductWeaShortForecast.setWinSpeedMax6(typeValueDefault);
                        dataProductWeaShortForecast.setWinSpeedMin12(typeValueDefault);
                        dataProductWeaShortForecast.setWinSpeedMax12(typeValueDefault);
                        dataProductWeaShortForecast.setWinDirection6("");
                        dataProductWeaShortForecast.setWinDirection12("");
                    }
                    result.put("dataProductWeaShortForecast", dataProductWeaShortForecast);
                    break;
                case PRODUCT_TYPE_GRID_FORECAST:
                    // 修改逻辑： 刚进入页面不获取最新的产品信息，全部显示默认的值就可以
                    dataProductBase.setSubtitle(DataProductBaseEnum.PRODUCT_BASE_SUBTITLE.getDefaultValue());
                    result.put("dataProductBase", dataProductBase);
                    result.put("dataProductHistory", dataProductHistories);
                    // 再次修改逻辑：期数比较特殊，必须是数据库最新的数据
                    DataProductWeaGridForecast dataProductWeaGridForecast = new DataProductWeaGridForecast();
                    dataProductWeaGridForecast.setText(DataProductBaseEnum.PRODUCT_WEA_TEXT.getDefaultValue());
                    // 修改逻辑： 显示默认的天气情况
                    result.put("dataProductWeaGridForecast", dataProductWeaGridForecast);
                    // 加过滤标识：需要0点时次的，但是不要 13、14 时次的（目前仅限于产品类型-2-精细化网格预报）
                    fusion12hWeatherInformations = getWeaInforms(typeEnum, baseTime, lonLat);
                    fusion12hWeatherInformation.clear();
                    for (Fusion12hWeatherInformation information : fusion12hWeatherInformations) {
                        // 0点时刻的单独存储
                        if (information.getForecastCount() == 0) {
                            fusion12hWeatherInformation.add(information);
                        }
                    }
                    fusion12hWeatherInformations.removeAll(fusion12hWeatherInformation);
                    result.put("fusion12hWeatherInformation", fusion12hWeatherInformation);
                    result.put("fusion12hWeatherInformations", fusion12hWeatherInformations);
                    break;
                case PRODUCT_TYPE_LINE_FORECAST:
                    if (line == null) {
                        line = DataProductBaseEnum.PRODUCT_BASE_LINE1.getDefaultValue() +
                                "," +
                                DataProductBaseEnum.PRODUCT_BASE_LINE2.getDefaultValue() +
                                "," +
                                DataProductBaseEnum.PRODUCT_BASE_LINE3.getDefaultValue();
                    }
                    dataProductBase.setLine(line);
                    result.put("dataProductBase", dataProductBase);
                    result.put("dataProductHistory", dataProductHistories);
                    // 加过滤标识：需要0点时次的，但是不要 13、14 时次的（目前仅限于产品类型-2-精细化网格预报）
                    DataProductBaseEnum[] values = DataProductBaseEnum.values();
                    List<String> lonLats = new LinkedList<>();
                    // TODO 当前默认每个线路的格点都一样，后期若有改动，此处加逻辑判断即可
                    for (DataProductBaseEnum baseEnum : values) {
                        if (baseEnum.name().contains("LON_LAT")) {
                            lonLats.add(baseEnum.getDefaultValue());
                        }
                    }
                    // 每次请求前端不加经纬度参数，全部按照5个格点处理
                    fusion12hWeatherInformation.clear();
                    fusion12hWeatherInformations.clear();
                    for (int i = 0; i < lonLats.size(); i++) {
                        DataProductWeaGridForecast dataProductWeaGridForecastLine = new DataProductWeaGridForecast();
                        dataProductWeaGridForecastLine.setText(DataProductBaseEnum.PRODUCT_WEA_TEXT.getDefaultValue());
                        // 修改逻辑： 显示默认的天气情况
                        result.put("dataProductWeaGridForecast" + (i + 1), dataProductWeaGridForecastLine);
                        lonLat = lonLats.get(i);
                        fusion12hWeatherInformations.addAll(getWeaInforms(typeEnum, baseTime, lonLat));
                        for (Fusion12hWeatherInformation information : fusion12hWeatherInformations) {
                            // 0点时刻的单独存储
                            if (information.getForecastCount() == 0) {
                                fusion12hWeatherInformation.add(information);
                            }
                        }
                        fusion12hWeatherInformations.removeAll(fusion12hWeatherInformation);
                        result.put("fusion12hWeatherInformation" + (i + 1), fusion12hWeatherInformation);
                        result.put("fusion12hWeatherInformations" + (i + 1), fusion12hWeatherInformations);
                    }
                    break;
                default:
                    return CommonResult.failed("产品类型参数： " + productType + " 不正确，请仔细检查参数。");
            }
            return CommonResult.success(result);
        } else {
            return CommonResult.failed("产品类型参数不能为空： " + productType + " ，请仔细检查参数。");
        }
    }

    /**
     * 处理产品数据： 预览、提交、下载
     */
    @Transactional(rollbackFor = {Exception.class})
    public CommonResult<?> handleData(Object data, HttpServletResponse response) {
        JSONObject jsonObject = JSONUtil.parseObj(data);
        DataProductBase dataProductBase;
        Integer flag;
        int size;
        try {
            dataProductBase = jsonObject.getBean("dataProductBase", DataProductBase.class);
            flag = jsonObject.getInt("flag", 0);
        } catch (Exception e) {
            return CommonResult.failed("解析json出错： " + e.getMessage());
        }
        Map<String, Object> map = new HashMap<>(21);
        Long publishTime = dataProductBase.getPublishTime();
        String publishTimeFormat = DateUtils.getTimeChineseMinute(publishTime);
        Long baseTime = dataProductBase.getBaseTime();
        String baseTimeFormat = DateUtils.getTimeChineseMinute(baseTime);
        map.put("publishTime", publishTimeFormat);
        map.put("publishDept", dataProductBase.getPublishDept());
        map.put("number", dataProductBase.getNumber());
        map.put("baseTime", baseTimeFormat);
        Integer productType = dataProductBase.getProductType();
        // 确定产品类型
        DataProductTypeEnum productTypeEnum = getDataProductTypeEnum(productType);
        // 准备参数，产品类型不同，替换的参数不同
        if (productTypeEnum != null) {
            DataProductWeaShortForecast dataProductWeaShortForecast = null;
            DataProductWeaGridForecast dataProductWeaGridForecast = null;
            switch (productTypeEnum) {
                case PRODUCT_TYPE_SHORT_FORECAST:
                    try {
                        dataProductWeaShortForecast = jsonObject.getBean("dataProductWeaShortForecast", DataProductWeaShortForecast.class);
                    } catch (Exception e) {
                        return CommonResult.failed("解析json出错： " + e.getMessage());
                    }
                    StringBuilder text = new StringBuilder("预计我市" + (textFlag6.equals(flag) ? dataProductWeaShortForecast.getText6() : dataProductWeaShortForecast.getText12()) + "，气温");
                    text.append(textFlag6.equals(flag) ? dataProductWeaShortForecast.getTemMin6() : dataProductWeaShortForecast.getTemMin12());
                    text.append("-");
                    text.append(textFlag6.equals(flag) ? dataProductWeaShortForecast.getTemMax6() : dataProductWeaShortForecast.getTemMax12());
                    text.append("℃，");
                    text.append(textFlag6.equals(flag) ? dataProductWeaShortForecast.getWinDirection6() : dataProductWeaShortForecast.getWinDirection12());
                    text.append(textFlag6.equals(flag) ? dataProductWeaShortForecast.getWinSpeedMin6() : dataProductWeaShortForecast.getWinSpeedMin12());
                    text.append("-");
                    text.append(textFlag6.equals(flag) ? dataProductWeaShortForecast.getWinSpeedMax6() : dataProductWeaShortForecast.getWinSpeedMax12());
                    text.append("级。");
                    productTypeEnum = DataProductTypeEnum.PRODUCT_TYPE_SHORT_FORECAST;
                    if (textFlag6.equals(flag)) {
                        dataProductWeaShortForecast.setTextFusion6(text.toString());
                        return CommonResult.success(dataProductWeaShortForecast);
                    } else if (textFlag12.equals(flag)) {
                        dataProductWeaShortForecast.setTextFusion12(text.toString());
                        return CommonResult.success(dataProductWeaShortForecast);
                    }
                    map.put("year", dataProductBase.getYear());
                    map.put("forecaster", dataProductBase.getForecaster());
                    map.put("signer", dataProductBase.getSigner());
                    map.put("hours", dataProductBase.getHours());
                    map.put("text6", dataProductWeaShortForecast.getText6());
                    map.put("text12", dataProductWeaShortForecast.getText12());
                    String hoursFormat1 = DateUtils.getTimeChineseHours(baseTime);
                    map.put("forecastTime1", hoursFormat1);
                    long timePlus6 = DateUtils.getTimePlus(baseTime, 6);
                    String hoursFormat2 = DateUtils.getTimeChineseHours(timePlus6);
                    if (DateUtils.isSameDay(baseTime, timePlus6)) {
                        hoursFormat2 = StrUtil.subAfter(hoursFormat2, "日", true);
                    }
                    map.put("forecastTime2", hoursFormat2);
                    String hoursFormat3 = DateUtils.getTimeChineseHours(timePlus6);
                    map.put("forecastTime3", hoursFormat3);
                    long timePlus61 = DateUtils.getTimePlus(timePlus6, 6);
                    String hoursFormat4 = DateUtils.getTimeChineseHours(timePlus61);
                    if (DateUtils.isSameDay(timePlus61, timePlus6)) {
                        hoursFormat4 = StrUtil.subAfter(hoursFormat4, "日", true);
                    }
                    map.put("forecastTime4", hoursFormat4);
                    break;
                case PRODUCT_TYPE_GRID_FORECAST:
                    try {
                        dataProductWeaGridForecast = jsonObject.getBean("dataProductWeaGridForecast", DataProductWeaGridForecast.class);
                    } catch (Exception e) {
                        return CommonResult.failed("解析json出错： " + e.getMessage());
                    }
                    productTypeEnum = DataProductTypeEnum.PRODUCT_TYPE_GRID_FORECAST;
                    map.put("subtitle", dataProductBase.getSubtitle());
                    map.put("weaGridForecastText", dataProductWeaGridForecast.getText());
                    map.put("fusion12hWeatherInformation", fusion12hWeatherInformation);
                    map.put("fusion12hWeatherInformations", fusion12hWeatherInformations);
                    break;
                case PRODUCT_TYPE_LINE_FORECAST:
                    // 默认线路预报格点数为5
                    int gridSize = 5;
                    for (int i = 0; i < gridSize; i++) {
                        try {
                            dataProductWeaGridForecast = jsonObject.getBean("dataProductWeaGridForecast" + (i + 1), DataProductWeaGridForecast.class);
                        } catch (Exception e) {
                            return CommonResult.failed("解析json出错： " + e.getMessage());
                        }
                        productTypeEnum = DataProductTypeEnum.PRODUCT_TYPE_GRID_FORECAST;
                        map.put("line", dataProductBase.getLine());
                        map.put("weaGridForecastText" + (i + 1), dataProductWeaGridForecast.getText());
                        map.put("fusion12hWeatherInformation" + (i + 1), fusion12hWeatherInformation);
                        map.put("fusion12hWeatherInformations" + (i + 1), fusion12hWeatherInformations);
                    }
                    break;
                default:
                    break;
            }
            // 处理文件
            File handleFile;
            try {
                handleFile = handleFile(productTypeEnum, publishTime, map, flag);
            } catch (Exception e) {
                return CommonResult.failed("处理文件过程出错。");
            }
            // 处理结果：预览、提交、下载
            if (handleFile != null) {
                if (previewFlag.equals(flag)) {
                    try {
                        com.cdyw.swsw.system.vo.common.FileUtil.downloadOrOnlineFile(handleFile, response, true);
                        return CommonResult.success("预览成功");
                    } catch (IOException e) {
                        return CommonResult.failed("预览出错： " + e.getMessage());
                    }
                } else if (commitFlag.equals(flag)) {
                    FileEntity fileEntity = getFileEntity(dataProductBase, handleFile);
                    size = dataProductMapper.insertDataProduct(fileEntity);
                    FileEntity dataProduct = dataProductMapper.getDataProductByEntity(fileEntity);
                    Integer productId = dataProduct.getId();
                    if (productId != null) {
                        dataProductBase.setProductId(productId);
                        dataProductBase.setCreateTime(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
                        size += dataProductMapper.insertDataProductBase(dataProductBase);
                        // 存入天气情况文本信息，产品类型不同，替换的参数不同
                        switch (productTypeEnum) {
                            case PRODUCT_TYPE_SHORT_FORECAST:
                                dataProductWeaShortForecast.setProductId(productId);
                                size += dataProductMapper.insertWeaShortForecast(dataProductWeaShortForecast);
                                break;
                            case PRODUCT_TYPE_GRID_FORECAST:
                                dataProductWeaGridForecast.setProductId(productId);
                                size += dataProductMapper.insertWeaGridForecast(dataProductWeaGridForecast);
                                break;
                            default:
                                break;
                        }
                        return CommonResult.success("成功生成 " + size + " 条数据。");
                    } else {
                        return CommonResult.failed("主表 data_product 插入失败，请重新检查。");
                    }
                } else if (downloadFlag.equals(flag)) {
                    try {
                        com.cdyw.swsw.system.vo.common.FileUtil.downloadOrOnlineFile(handleFile, response, false);
                        return CommonResult.success("预览成功");
                    } catch (IOException e) {
                        return CommonResult.failed("预览出错： " + e.getMessage());
                    }
                } else {
                    return CommonResult.failed("处理类型对应值不正确： " + flag);
                }
            }
        } else {
            return CommonResult.failed("产品类型不正确。");
        }
        return null;
    }

    /**
     * 支持多个文件并且携带参数上传
     */
    public CommonResult<?> upload(HttpServletRequest httpServletRequest) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) httpServletRequest;
        String dataProductBaseJson = multipartHttpServletRequest.getParameter("dataProductBase");
        JSONObject jsonObject = JSONUtil.parseObj(dataProductBaseJson);
        DataProductBase dataProductBase = jsonObject.toBean(DataProductBase.class);
        MultiValueMap<String, MultipartFile> multiFileMap = multipartHttpServletRequest.getMultiFileMap();
        if (!multiFileMap.isEmpty()) {
            // 确定产品类型
            Integer productType = dataProductBase.getProductType();
            DataProductTypeEnum productTypeEnum = getDataProductTypeEnum(productType);
            Long publishTime = dataProductBase.getPublishTime();
            // 根据相关参数找到对应的文件存储路径
            String finalFilePath = commonPathName.getDataProductPathByPara(productTypeEnum, publishTime, FileStatusEnum.FILE_STATUS_FINAL);
            Set<Map.Entry<String, List<MultipartFile>>> entries = multiFileMap.entrySet();
            for (Map.Entry<String, List<MultipartFile>> entry : entries) {
                List<MultipartFile> multipartFiles = entry.getValue();
                for (MultipartFile multipartFile : multipartFiles) {
                    try {
                        String originalFilename = multipartFile.getOriginalFilename();
                        if (originalFilename != null) {
                            if (originalFilename.endsWith(FileTypeEnum.FILE_TYPE_DOCX.getSuffix()) ||
                                    originalFilename.endsWith(FileTypeEnum.FILE_TYPE_DOC.getSuffix())) {
                                InputStream inputStream = multipartFile.getInputStream();
                                String fullFilePath = finalFilePath + "/" + originalFilename;
                                File destFileName = FileUtil.writeFromStream(inputStream, fullFilePath);
                                int size = 0;
                                if (destFileName.exists() && destFileName.isFile()) {
                                    FileEntity fileEntity = getFileEntity(dataProductBase, destFileName);
                                    size = dataProductMapper.insertDataProduct(fileEntity);
                                    FileEntity dataProduct = dataProductMapper.getDataProductByEntity(fileEntity);
                                    Integer productId = dataProduct.getId();
                                    if (productId != null) {
                                        dataProductBase.setProductId(productId);
                                        dataProductBase.setCreateTime(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
                                        size += dataProductMapper.insertDataProductBase(dataProductBase);
                                        if (productTypeEnum != null) {
                                            switch (productTypeEnum) {
                                                case PRODUCT_TYPE_SHORT_FORECAST:
                                                    DataProductWeaShortForecast dataProductWeaShortForecast = new DataProductWeaShortForecast();
                                                    dataProductWeaShortForecast.setProductId(productId);
                                                    size += dataProductMapper.insertWeaShortForecast(dataProductWeaShortForecast);
                                                    break;
                                                case PRODUCT_TYPE_GRID_FORECAST:
                                                    DataProductWeaGridForecast dataProductWeaGridForecast = new DataProductWeaGridForecast();
                                                    dataProductWeaGridForecast.setProductId(productId);
                                                    size += dataProductMapper.insertWeaGridForecast(dataProductWeaGridForecast);
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                    }
                                }
                                if (size == 3) {
                                    return CommonResult.success("成功生成 " + size + " 条数据。");
                                } else {
                                    return CommonResult.failed("记录写入失败。");
                                }
                            } else {
                                return CommonResult.failed("文件格式不正确，请仔细检查，目前仅支持word格式： .doc 或者 .docx");
                            }
                        } else {
                            return CommonResult.failed("文件名称非法，请仔细检查。");
                        }
                    } catch (IOException e) {
                        return CommonResult.failed("获取文件失败，请仔细检查： " + e.getMessage());
                    }
                }
            }
            return CommonResult.success("文件上传成功。");
        } else {
            return CommonResult.failed("文件为空，请仔细检查。");
        }
    }

    private DataProductTypeEnum getDataProductTypeEnum(Integer productType) {
        DataProductTypeEnum[] dataProductTypeEnums = DataProductTypeEnum.values();
        for (DataProductTypeEnum dataProductTypeEnum : dataProductTypeEnums) {
            Integer productTypeEnumType = dataProductTypeEnum.getType();
            String productTypeEnumTypeName = dataProductTypeEnum.name();
            if (productTypeEnumType.equals(productType) && productTypeEnumTypeName.startsWith("PRODUCT")) {
                return dataProductTypeEnum;
            }
        }
        return null;
    }

    /**
     * 获取 产品基础信息 默认实体类
     *
     * @param baseTime 起报时间
     * @param type     产品类型
     * @return 文件实体类
     */
    private DataProductBase getDataProductBase(String baseTime, Integer type) {
        DataProductBase dataProductBase = new DataProductBase();
        dataProductBase.setBaseTime(Long.parseLong(baseTime));
        dataProductBase.setProductType(type);
        dataProductBase.setPublishDept(DataProductBaseEnum.PRODUCT_BASE_PUBLISH_DEPT.getDefaultValue());
        dataProductBase.setPublishTime(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
        dataProductBase.setYear(LocalDate.now().getYear());
        // 期数为最新数据 + 1
        DataProductBase dataProductBaseNew = dataProductMapper.getDataProductBaseByTimeAndType(dataProductBase);
        if (dataProductBaseNew != null) {
            dataProductBase.setNumber(dataProductBaseNew.getNumber() + 1);
        } else {
            dataProductBase.setNumber(1);
        }
        return dataProductBase;
    }

    /**
     * 根据 目标文件（名称或路径）获取 文件实体类
     *
     * @param dataProductBase 产品类型-基础信息
     * @param handleFile      目标文件
     * @return 文件实体类
     */
    private FileEntity getFileEntity(DataProductBase dataProductBase, File handleFile) {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setName(handleFile.getName());
        fileEntity.setPosFile(handleFile.getParent());
        fileEntity.setFileSize(Integer.parseInt(String.valueOf(handleFile.length())));
        fileEntity.setType(dataProductBase.getProductType());
        fileEntity.setTime(dataProductBase.getBaseTime());
        fileEntity.setCreateTime(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
        return fileEntity;
    }

    /**
     * 根据相关参数获取全部天气信息，便于不同类型产品解析
     *
     * @param typeEnum 类型
     * @param baseTime 起报时间
     * @param lonLat   经纬度
     * @return 全部天气信息
     */
    @SuppressWarnings("unchecked")
    private List<Fusion12hWeatherInformation> getWeaInforms(DataProductTypeEnum typeEnum, String baseTime, String lonLat) {
        CommonResult<?> commonResult;
        switch (typeEnum) {
            case PRODUCT_TYPE_SHORT_FORECAST:
                commonResult = fusion12hService.getWeaInformByTimeRange(lonLat, baseTime, typeEnum);
                break;
            case PRODUCT_TYPE_GRID_FORECAST:
            case PRODUCT_TYPE_LINE_FORECAST:
                commonResult = fusion12hService.previewPointModified(lonLat, baseTime, typeEnum);
                break;
            default:
                commonResult = new CommonResult<>(500, "error......");
        }
        Object data = commonResult.getData();
        if (data != null) {
            Map<String, List<Fusion12hWeatherInformation>> weaInformationMap = (Map<String, List<Fusion12hWeatherInformation>>) data;
            fusion12hWeatherInformations = weaInformationMap.get("fusion12hWeatherInformation");
        }
        return fusion12hWeatherInformations;
    }

    /**
     * 根据产品类型处理文档：复制模板-替换模板-转化为PDF
     *
     * @param productType 产品类型
     * @return File
     */
    private File handleFile(DataProductTypeEnum productType, Long publishTime, Map<String, Object> paramMap, Integer flag) {
        String templatePathName = commonPathName.getDataProductTemplatePathByPara();
        FileStatusEnum statusEnum;
        File finalFile;
        if (previewFlag.equals(flag)) {
            statusEnum = FileStatusEnum.FILE_STATUS_TEMP;
        } else if (commitFlag.equals(flag)) {
            statusEnum = FileStatusEnum.FILE_STATUS_FINAL;
        } else {
            statusEnum = FileStatusEnum.FILE_STATUS_DEFAULT;
        }
        // 复制模板
        Path templatePath = Paths.get(templatePathName);
        File[] files = templatePath.toFile().listFiles();
        Path templateSourcePath = null;
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(productType.getType().toString())) {
                    templateSourcePath = file.toPath();
                    break;
                }
            }
        }
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String finalFilePath = commonPathName.getDataProductPathByPara(productType, publishTime, statusEnum);
        File finalPath = new File(finalFilePath);
        try {
            if (templateSourcePath != null) {
                boolean mkdirs = finalPath.exists();
                if (!mkdirs) {
                    mkdirs = finalPath.mkdirs();
                }
                if (mkdirs) {
                    finalFilePath += "/" + uuid + "-" + templateSourcePath.getFileName();
                    Path finalDestPath = Paths.get(finalFilePath);
                    Path finalDestCopyPath = Files.copy(templateSourcePath, finalDestPath);
                    File finalDestFile = finalDestCopyPath.toFile();
                    finalFile = com.cdyw.swsw.system.vo.common.FileUtil.modifyFileText(finalDestFile, finalDestFile, paramMap, productType);
                    if (finalFile != null) {
                        // 提交和下载不需要转 pdf
                        if (commitFlag.equals(flag) || downloadFlag.equals(flag)) {
                            return finalFile;
                        } else {
                            return filePythonConvertUtil.file2Pdf(finalFile, finalFile.getParent());
                        }
//                        String pdfName = finalFile.getName().replace(FileTypeEnum.FILE_TYPE_DOCX.getSuffix(), FileTypeEnum.FILE_TYPE_PDF.getSuffix());
//                        File pdfFile = new File(finalFile.getParent() + "/" + pdfName);
//                        file2Pdf = filePoiConvertUtil.file2Pdf(finalFile, pdfFile);
//                        file2Pdf = fileOpenOfficeConvertUtil.file2Pdf(finalFile, finalFile.getParent());
//                        file2Pdf = fileLibreOfficeConvertUtil.file2Pdf(finalFile, finalFile.getParent());
                    } else {
                        return null;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
