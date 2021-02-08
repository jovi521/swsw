package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicTransparency;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DicTransparencyMapper {

    /**
     * 根据id删除元素
     *
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入元素
     *
     * @param record DicTransparency
     * @return int
     */
    int insert(DicTransparency record);

    /**
     * 根据id查询元素
     *
     * @param id Integer
     * @return DicTransparency
     */
    DicTransparency selectByPrimaryKey(Integer id);

    /**
     * 查询所有元素
     *
     * @return List
     */
    List<DicTransparency> selectAll();

    /**
     * 更新元素
     *
     * @param record DicTransparency
     * @return int
     */
    int updateByPrimaryKey(DicTransparency record);
}