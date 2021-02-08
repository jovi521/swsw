package com.cdyw.swsw.data.domain.dao.thunder;

import com.cdyw.swsw.common.domain.entity.thunder.Thunder;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface ThunderMapper {

    /**
     * 根据主键id删除对象
     *
     * @param tableName String
     * @param id        id
     * @return int
     */
    int deleteByPrimaryKey(String tableName, Integer id);

    /**
     * 插入对象
     *
     * @param tableName String
     * @param record    Thunder
     * @return int
     */
    int insert(String tableName, Thunder record);

    /**
     * 根据主键id查询对象
     *
     * @param tableName String
     * @param id        id
     * @return Thunder
     */
    Thunder selectByPrimaryKey(String tableName, Integer id);

    /**
     * 查询所有对象
     *
     * @param tableName String
     * @return List<Thunder>
     */
    List<Thunder> selectAll(String tableName);

    /**
     * 更新指定对象
     *
     * @param tableName String
     * @param record    Thunder
     * @return int
     */
    int updateByPrimaryKey(String tableName, Thunder record);

    /**
     * 根据层次序号和时间查询雷电对象
     *
     * @param tableName String
     * @param layerNum  Integer
     * @param dateTime  String
     * @return List<Thunder>
     */
    List<Thunder> getThunderByNumAndTime(String tableName, Integer layerNum, String dateTime);
}