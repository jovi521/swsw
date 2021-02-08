package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicTableName;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DicTableNameMapper {

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
    int insert(DicTableName record);

    /**
     * 根据id查询数据
     *
     * @param id Integer
     * @return Object
     */
    DicTableName selectByPrimaryKey(Integer id);

    /**
     * 查询全部
     *
     * @return List
     */
    List<DicTableName> selectAll();

    /**
     * 根据id更新指定数据
     *
     * @param record Object
     * @return int
     */
    int updateByPrimaryKey(DicTableName record);
}