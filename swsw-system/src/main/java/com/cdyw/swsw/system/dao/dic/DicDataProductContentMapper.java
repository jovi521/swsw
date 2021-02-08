package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicDataProductContent;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DicDataProductContentMapper {

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
     * @param record DicColor
     * @return int
     */
    int insert(DicDataProductContent record);

    /**
     * 根据id查询数据
     *
     * @param id Integer
     * @return Object
     */
    DicDataProductContent selectByPrimaryKey(Integer id);

    /**
     * 查询全部
     *
     * @return List
     */
    List<DicDataProductContent> selectAll();

    /**
     * 根据id更新指定数据
     *
     * @param record Object
     * @return int
     */
    int updateByPrimaryKey(DicDataProductContent record);

    /**
     * 根据模板类型查询模板内容
     *
     * @param type Integer
     * @return Object
     */
    String selectByType(Integer type);
}