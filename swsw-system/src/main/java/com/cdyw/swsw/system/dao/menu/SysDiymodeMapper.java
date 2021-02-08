package com.cdyw.swsw.system.dao.menu;

import com.cdyw.swsw.common.domain.entity.menu.Diymode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 前台自定义菜单
 *
 * @author cdyw
 */
@Repository
public interface SysDiymodeMapper {

    /**
     * 通过用户查询自定义模式
     *
     * @param userId
     * @return
     */
    List<Map<String, Object>> selectDiymode(Integer userId);

    /**
     * 插入自定义模式
     *
     * @param diymode
     * @param containId
     * @return
     */
    int insertDiymode(@Param("diymode") Diymode diymode, @Param("containId") String containId);

    /**
     * 更改自定义模式
     *
     * @param userId
     * @param num
     * @param modeId
     * @return
     */
    int updateByUseridAndNum(@Param("userId") Integer userId, @Param("num") Integer num, @Param("modeId") Integer modeId);

    /**
     * 通过userid插入
     *
     * @param userId
     * @param num
     * @param modeId
     * @return
     */
    int insertByUserid(@Param("userId") Integer userId, @Param("num") Long num, @Param("modeId") Integer modeId);

    /**
     * 通过userid查询用户自定义模式个数
     *
     * @param userId
     * @return
     */
    Map<String, Object> selectNumByUserid(Integer userId);
}
