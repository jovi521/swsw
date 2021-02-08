package com.cdyw.swsw.system.service.common;

import cn.hutool.json.JSONUtil;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.common.util.FileUtils;
import com.cdyw.swsw.common.domain.ao.dic.DicLonLat;
import com.cdyw.swsw.common.domain.ao.dic.DicRoad;
import com.cdyw.swsw.common.domain.ao.dic.DicTransparency;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.dao.dic.DicHeightMapper;
import com.cdyw.swsw.system.dao.dic.DicLonLatMapper;
import com.cdyw.swsw.system.dao.dic.DicRoadMapper;
import com.cdyw.swsw.system.dao.dic.DicTransparencyMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 所有字典表的操作，使用redis
 *
 * @author jovi
 */
@CacheConfig(keyGenerator = "keyGenerator")
@Service
public class CommonDicService {

    private final DicLonLatMapper dicLonLatMapper;

    private final DicTransparencyMapper dicTransparencyMapper;

    private final DicHeightMapper dicHeightMapper;

    private final DicRoadMapper dicRoadMapper;

    private final CommonPath commonPath;

    @Autowired
    public CommonDicService(DicLonLatMapper dicLonLatMapper, DicTransparencyMapper dicTransparencyMapper,
                            DicHeightMapper dicHeightMapper, DicRoadMapper dicRoadMapper, CommonPath commonPath) {
        this.dicLonLatMapper = dicLonLatMapper;
        this.dicTransparencyMapper = dicTransparencyMapper;
        this.dicHeightMapper = dicHeightMapper;
        this.dicRoadMapper = dicRoadMapper;
        this.commonPath = commonPath;
    }

    @Cacheable(value = {"lonLat"})
    public CommonResult<?> getLonLatByPara(String lon, String lat, String location, Integer type, String parentCode) {
        List<DicLonLat> dicLonLats = dicLonLatMapper.getLonLatByPara(lon, lat, location, type, parentCode);
        if (dicLonLats.size() == 0) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(dicLonLats);
        }
    }

    @Cacheable(value = {"allTrans"})
    public CommonResult<?> getAllTrans() {
        List<DicTransparency> dicLonLats = dicTransparencyMapper.selectAll();
        if (dicLonLats.size() == 0) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(dicLonLats);
        }
    }

    @Cacheable(value = {"allHeight"})
    public CommonResult<?> getAllHeight(String productCode, String parentCode) {
        List<Map<String, Object>> heightLists = dicHeightMapper.selectAllHeight(productCode, parentCode);
        if (heightLists.size() == 0) {
            return CommonResult.failed();
        } else {
            return CommonResult.success(heightLists);
        }
    }

    public CommonResult<?> getRoadByStation(Integer startStation, Integer endStation) {
        List<DicRoad> dicRoadList = dicRoadMapper.selectRoad(startStation, endStation);
        return CommonResult.success(dicRoadList);
    }

    public void getLonlatGrid(String productType, HttpServletResponse response) throws IOException {
        String lonlatGridPath = "";
        if ("25".equals(productType)) {
            // 获得文件路径
            lonlatGridPath = commonPath.getLonlatFusion12hPath();
        } else if ("19".equals(productType)) {
            lonlatGridPath = commonPath.getLonlatCldas1kmPath();
        } else if ("28".equals(productType)) {
            lonlatGridPath = commonPath.getLonlatWindfieldInversionPath();
        }
        if (!StringUtils.isEmpty(lonlatGridPath)) {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(lonlatGridPath);
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            PrintWriter writer = response.getWriter();
            char[] buff = new char[1024];
            int len = 0;
            while ((len = isr.read(buff)) != -1) {
                writer.write(buff, 0, len);
            }
        }
    }

    public CommonResult<?> getColorFile(String parentCode, String productCode, HttpServletResponse response) throws IOException {
        Map<String, Object> datamap = new HashMap<>();
        switch (parentCode) {
            case "25":
                if (StringUtils.isEmpty(productCode)) {
                    //查询所有色标
                    String colorTemPath = commonPath.getColorTemFusion12hPath();
                    String colorRhuPath = commonPath.getColorRhuFusion12hPath();
                    String colorPrsPath = commonPath.getColorPrsFusion12hPath();
                    InputStream isTem = this.getClass().getResourceAsStream(colorTemPath);
                    InputStream isRhu = this.getClass().getResourceAsStream(colorRhuPath);
                    InputStream isPrs = this.getClass().getResourceAsStream(colorPrsPath);
                    String colorTemContent = FileUtils.readCharByPath(isTem);
                    String colorRhuContent = FileUtils.readCharByPath(isRhu);
                    String colorPrsContent = FileUtils.readCharByPath(isPrs);
                    HashMap colorTemMap = JSONUtil.toBean(colorTemContent, HashMap.class);
                    HashMap colorPrsMap = JSONUtil.toBean(colorPrsContent, HashMap.class);
                    HashMap colorRhuMap = JSONUtil.toBean(colorRhuContent, HashMap.class);
                    datamap.put("tem", colorTemMap);
                    datamap.put("prs", colorPrsMap);
                    datamap.put("rhu", colorRhuMap);
                } else {
                    String colorPath;
                    String colorContent;
                    HashMap colorMap;
                    switch (productCode) {
                        case "2501":
                            colorPath = commonPath.getColorTemFusion12hPath();
                            InputStream isTem = this.getClass().getResourceAsStream(colorPath);
                            colorContent = FileUtils.readCharByPath(isTem);
                            colorMap = JSONUtil.toBean(colorContent, HashMap.class);
                            datamap.put("tem", colorMap);
                            break;
                        case "2503":
                            colorPath = commonPath.getColorTemFusion12hPath();
                            InputStream isRhu = this.getClass().getResourceAsStream(colorPath);
                            colorContent = FileUtils.readCharByPath(isRhu);
                            colorMap = JSONUtil.toBean(colorContent, HashMap.class);
                            datamap.put("rhu", colorMap);
                            break;
                        case "2504":
                            colorPath = commonPath.getColorTemFusion12hPath();
                            InputStream isPrs = this.getClass().getResourceAsStream(colorPath);
                            colorContent = FileUtils.readCharByPath(isPrs);
                            colorMap = JSONUtil.toBean(colorContent, HashMap.class);
                            datamap.put("prs", colorMap);
                            break;
                        case "2505":
                            break;
                    }
                    break;
                }
        }
        return CommonResult.success(datamap);
    }

}
