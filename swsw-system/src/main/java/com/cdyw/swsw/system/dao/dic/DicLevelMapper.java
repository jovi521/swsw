package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicLevel;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DicLevelMapper {

    /**
     * 通过id删除数据
     *
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入
     *
     * @param record Object
     * @return int
     */
    int insert(DicLevel record);

    /**
     * 根据id查询数据
     *
     * @param id Integer
     * @return Object
     */
    DicLevel selectByPrimaryKey(Integer id);

    /**
     * 查询全部
     *
     * @return List
     */
    List<DicLevel> selectAll();

    /**
     * 根据id更新指定数据
     *
     * @param record Object
     * @return int
     */
    int updateByPrimaryKey(DicLevel record);

    /**
     * 根据父id和等级查询产品
     *
     * @param parentCode String
     * @param level      Integer
     * @return list
     */
    List<HashMap<String, Object>> selectByParentcodeAndLevel(@Param("parentCode") String parentCode, @Param("level") Integer level);

    /**
     * 根据根据父id和产品编号查询信息
     *
     * @param parentCode 父ID
     * @param code       产品编码
     * @return DicLevel
     */
    DicLevel selectByParentcodeAndCode(@Param("parentCode") Integer parentCode, @Param("code") Integer code);
}