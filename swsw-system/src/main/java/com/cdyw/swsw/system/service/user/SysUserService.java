package com.cdyw.swsw.system.service.user;


import com.cdyw.swsw.common.domain.ao.dic.DicDataProductFormColumn;
import com.cdyw.swsw.common.domain.ao.dic.DicDataProductFormColumnSelect;
import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import com.cdyw.swsw.system.app.config.UsernameOrPasswordError;
import com.cdyw.swsw.system.dao.dic.DicDataProductFormColumnMapper;
import com.cdyw.swsw.system.dao.dic.DicDataProductFormColumnSelectMapper;
import com.cdyw.swsw.system.dao.user.SysDepartmentMapper;
import com.cdyw.swsw.system.dao.user.SysUserMapper;
import com.cdyw.swsw.system.security.config.BCryptPasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务
 */
@Service
public class SysUserService {

    //用户激活状态
    private static final int USER_STATE = 1;
    @Autowired
    BCryptPasswordEncoderUtil bCryptPasswordEncoderUtil;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    SysDepartmentMapper sysDepartmentMapper;
    @Autowired
    DicDataProductFormColumnMapper dicDataProductFormColumnMapper;
    @Autowired
    DicDataProductFormColumnSelectMapper dicDataProductFormColumnSelectMapper;

    public SysUserEntity getUserByUserName(String username) {
        return sysUserMapper.selectUserByUserName(username);
    }

    /**
     * 个性化验证登录
     *
     * @param username    账号
     * @param rawPassword 原始密码
     * @return
     */
    public boolean checkLogin(String username, String rawPassword) throws Exception {
        SysUserEntity userEntity = this.getUserByUserName(username);
        System.out.println("userEntity = " + userEntity);
        if (userEntity == null) {
            //设置友好提示
            throw new UsernameOrPasswordError("账号不存在，请重新尝试！");
        } else {
            //加密的密码
            String encodedPassword = userEntity.getPassWord();
            //和加密后的密码进行比配
            if (!bCryptPasswordEncoderUtil.matches(rawPassword, encodedPassword)) {
                //System.out.println("checkLogin--------->密码不正确！");
                //设置友好提示
                throw new UsernameOrPasswordError("密码不正确！");
            } else {
                return true;
            }
        }
    }

    public Map<String, Object> getUserByDepartmentId(Integer departmentId) {
        if (departmentId == null || departmentId < 0) {
            // 给一个默认值
            departmentId = 1;
        }
        // 查询所有的部门信息
        List<HashMap<String, Object>> departmentList = sysDepartmentMapper.selectDepartmentAll();
        // 根据部门id查询部门每个角色所对应的人
        String roleForecast = "ROLE_FORECAST";
        String roleApprove = "ROLE_APPROVE";
        String roleSign = "ROLE_SIGN";
        List<HashMap<String, Object>> forecastList = sysUserMapper.selectUserByDepartmentidAndRolename(departmentId, roleForecast);
        List<HashMap<String, Object>> approveList = sysUserMapper.selectUserByDepartmentidAndRolename(departmentId, roleApprove);
        List<HashMap<String, Object>> signList = sysUserMapper.selectUserByDepartmentidAndRolename(departmentId, roleSign);
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> formList = new ArrayList<>();
        String[] columnKeyArray = {"23", "24", "25", "26", "27"};
        for (String columnkey : columnKeyArray) {
            Map<String, Object> dataMap = new HashMap<>();
            DicDataProductFormColumn dicDataProductFormColumn = dicDataProductFormColumnMapper.selectByPrimaryKey(Integer.parseInt(columnkey));
            dataMap.put("columnKey", dicDataProductFormColumn.getColumnKey());
            dataMap.put("columnName", dicDataProductFormColumn.getColumnName());
            DicDataProductFormColumnSelect dicDataProductFormColumnSelect = dicDataProductFormColumnSelectMapper.selectByColumnId(columnkey);
            String flag = dicDataProductFormColumnSelect.getSelectFlag();
            dataMap.put("selectFlag", Integer.parseInt(flag));
            switch (columnkey) {
                case "23":
                    dataMap.put("columnSelect", departmentList);
                    dataMap.put("columnDefault", departmentList.get(0).get("name"));
                    break;
                case "24":
                    dataMap.put("columnSelect", departmentList);
                    dataMap.put("columnDefault", departmentList.get(departmentList.size() - 1).get("name"));
                    break;
                case "25":
                    dataMap.put("columnSelect", forecastList);
                    dataMap.put("columnDefault", forecastList.get(0).get("name"));
                    break;
                case "26":
                    dataMap.put("columnSelect", approveList);
                    dataMap.put("columnDefault", approveList.get(0).get("name"));
                    break;
                case "27":
                    dataMap.put("columnSelect", signList);
                    dataMap.put("columnDefault", signList.get(0).get("name"));
                    break;
                default:
                    dataMap.put("columnSelect", "");
                    dataMap.put("columnDefault", "");
            }
            formList.add(dataMap);
        }
        resultMap.put("form", formList);
        return resultMap;
    }


}
