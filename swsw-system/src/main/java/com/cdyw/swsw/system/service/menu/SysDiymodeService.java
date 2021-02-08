package com.cdyw.swsw.system.service.menu;

import cn.hutool.core.util.StrUtil;
import com.cdyw.swsw.common.domain.entity.menu.Diymode;
import com.cdyw.swsw.common.domain.vo.result.CommonResult;
import com.cdyw.swsw.system.dao.menu.SysDiymodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SysDiymodeService {

    private final SysDiymodeMapper sysDiymodeMapper;

    public SysDiymodeService(SysDiymodeMapper sysDiymodeMapper) {
        this.sysDiymodeMapper = sysDiymodeMapper;
    }

    public CommonResult<?> getDiymode(Integer userId) {
        if (userId == null) {
            return CommonResult.failed();
        }
        List<Map<String, Object>> resultList = sysDiymodeMapper.selectDiymode(userId);
        for (Map<String, Object> temMap : resultList) {
            String containId = (String) temMap.get("containId");
            if (!StrUtil.isEmpty(containId) && containId.contains(",")) {
                String[] contaynIdArrayStr = containId.split(",");
                int[] containIdArraInt = new int[contaynIdArrayStr.length];
                for (int i = 0; i < contaynIdArrayStr.length; i++) {
                    containIdArraInt[i] = Integer.parseInt(contaynIdArrayStr[i]);
                }
                temMap.put("containId", containIdArraInt);
            } else if (!StrUtil.isEmpty(containId)) {
                int containIdInt = Integer.parseInt(containId);
                temMap.put("containId", containIdInt);
            }
        }
        return CommonResult.success(resultList);
    }

    @Transactional(rollbackFor = Exception.class)
    public CommonResult<?> saveDiymode(Diymode diymode, String userId) {
        // 获得将要保存的菜单id列表
        List<String> idList = diymode.getIdList();
        String containId = "";
        // 将菜单id列表拼接为字符串形式
        for (String id : idList) {
            containId += id;
            containId += ",";
        }
        containId = containId.substring(0, containId.length() - 1);
        // 将传过来的参数保存到自定义模式表
        sysDiymodeMapper.insertDiymode(diymode, containId);
        // 得到插入后的自增modeId
        Integer modeId = diymode.getModeId();
        Integer userIdInt = Integer.parseInt(userId);
        // 如果是快捷方式，就直接更改用户和模式关系表对应数据
        if (diymode.getIsShortcut()) {
            sysDiymodeMapper.updateByUseridAndNum(userIdInt, 0, modeId);
        } else {
            // 先查询出用户已经定义好的自定义模式个数
            Map<String, Object> numMap = sysDiymodeMapper.selectNumByUserid(userIdInt);
            Long num = (Long) numMap.get("num");
            // 再插入到用户和模式关系表
            sysDiymodeMapper.insertByUserid(userIdInt, num, modeId);
        }
        return CommonResult.success();
    }

}
