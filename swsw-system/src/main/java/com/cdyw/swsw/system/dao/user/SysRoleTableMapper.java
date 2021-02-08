package com.cdyw.swsw.system.dao.user;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统角色查询
 *
 * @author cdyw
 */
@Repository
public interface SysRoleTableMapper {

    /**
     * 通过用户名查询角色
     *
     * @param userName String
     * @return List
     */
    List<String> getRolesByUserName(@Param("userName") String userName);

}