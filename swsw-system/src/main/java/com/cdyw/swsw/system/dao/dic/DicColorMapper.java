package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicColor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DicColorMapper {

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
    int insert(DicColor record);

    /**
     * 根据id查询数据
     *
     * @param id Integer
     * @return Object
     */
    DicColor selectByPrimaryKey(Integer id);

    /**
     * 查询全部
     *
     * @return List
     */
    List<DicColor> selectAll();

    /**
     * 根据id更新指定数据
     *
     * @param record Object
     * @return int
     */
    int updateByPrimaryKey(DicColor record);

    /**
     * 根据产品类型和父类型查询数据
     *
     * @param productType String
     * @param parentCode  String
     * @return List
     */
    List<HashMap<String, Object>> selectByProcutType(@Param("productType") String productType, @Param("parentCode") String parentCode);
}