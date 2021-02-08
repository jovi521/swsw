package com.cdyw.swsw.system.service.user;


import com.cdyw.swsw.system.dao.user.SysRoleTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (SysRoleTable)表服务实现类
 *
 * @author cdyw
 */
@Service("sysRoleTableService")
public class SysRoleTableService {

    @Autowired
    private SysRoleTableMapper sysRoleTableMapper;

    /**
     * 根据用户名称查询角色
     *
     * @param userName
     * @return
     */
    public List<String> getRolesByUserName(String userName) {
        return sysRoleTableMapper.getRolesByUserName(userName);
    }


}