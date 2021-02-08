package com.cdyw.swsw.system.dao.user;


import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统用户查询
 *
 * @author cdyw
 */
@Repository
public interface SysUserMapper {

    /**
     * 通过用户名查询用户
     *
     * @param userName String
     * @return SysUserEntity
     */
    SysUserEntity selectUserByUserName(@Param("userName") String userName);

    /**
     * 根据部门id和角色id查询对应的用户
     *
     * @param departmentId
     * @param roleId
     * @return
     */
    List<HashMap<String, Object>> selectUserByDepartmentidAndRoleid(@Param("departmentId") Integer departmentId,
                                                                    @Param("roleId") Integer roleId);

    /**
     * 根据部门id和角色名称查询对应的用户
     *
     * @param departmentId
     * @param roleName
     * @return
     */
    List<HashMap<String, Object>> selectUserByDepartmentidAndRolename(@Param("departmentId") Integer departmentId,
                                                                      @Param("roleName") String roleName);

    /**
     * 根据userid查询对应的阈值
     *
     * @param userId
     * @return
     */
    List<Map<String, Object>> selectThresholdByUserid(@Param("userId") Integer userId);

    /**
     * 修改用户的阈值
     *
     * @param userId
     * @param type
     * @param threshold
     * @param flag
     * @return
     */
    int updateThresholdByUserid(@Param("userId") Integer userId, @Param("type") Integer type,
                                @Param("threshold") String threshold, @Param("flag") Integer flag);

}
