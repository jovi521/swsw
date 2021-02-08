package com.cdyw.swsw.system.service.menu;

import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.dao.menu.SysFrontMenuMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SysFrontMenuService {

    private final SysFrontMenuMapper sysFrontMenuMapper;

    public SysFrontMenuService(SysFrontMenuMapper sysFrontMenuMapper) {
        this.sysFrontMenuMapper = sysFrontMenuMapper;
    }

    public CommonResult<?> getFrontMenu() {
        List<Map<String, Object>> menuList = sysFrontMenuMapper.selectFrontMenu();
        Set<String> menuParentSet = new HashSet<>();
        for (Map<String, Object> temMap : menuList) {
            String parentMenu = (String) temMap.get("parent_name");
            menuParentSet.add(parentMenu);
        }
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String menuStr : menuParentSet) {
            List<Map<String, Object>> everyMenuList = new ArrayList<>();
            Map<String, Object> resultMap = new HashMap<>();
            for (Map<String, Object> temMap : menuList) {
                String parentMenu = (String) temMap.get("parent_name");
                if (Objects.equals(menuStr, parentMenu)) {
                    HashMap<String, Object> requiredMap = new HashMap<>(temMap);
                    String typeStr = (String) requiredMap.get("type");
                    if (!StrUtil.isEmpty(typeStr) && typeStr.contains(",")) {
                        String[] typeArr = typeStr.split(",");
                        int[] typeIntArr = new int[typeArr.length];
                        for (int i = 0; i < typeArr.length; i++) {
                            typeIntArr[i] = Integer.parseInt(typeArr[i]);
                        }
                        requiredMap.put("type", typeIntArr);
                    } else if (!StrUtil.isEmpty(typeStr)) {
                        int typeInt = Integer.parseInt(typeStr);
                        requiredMap.put("type", typeInt);
                    }
                    requiredMap.remove("parent_name");
                    everyMenuList.add(requiredMap);
                }
            }
            resultMap.put(menuStr, everyMenuList);
            resultList.add(resultMap);
        }
        return CommonResult.success(resultList);

    }


}
