package com.cdyw.swsw.system.dao.product;

import com.cdyw.swsw.common.domain.entity.product.DataProductColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DataProductColumnMapper {

    /**
     * 根据主键删除指定对象
     *
     * @param id id
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入对象
     *
     * @param dataProductColumn DataProductColumn
     * @return int
     */
    int insert(DataProductColumn dataProductColumn);

    /**
     * 根据主键查询对象
     *
     * @param id id
     * @return DataProductColumn
     */
    DataProductColumn selectByPrimaryKey(Integer id);

    /**
     * 根据 dataProductId 查询对象
     *
     * @param dataProductId Integer
     * @return DataProductColumn
     */
    DataProductColumn selectByDataProductId(Integer dataProductId);

    /**
     * 查询所有
     *
     * @return List<DataProductColumn>
     */
    List<DataProductColumn> selectAll();

    /**
     * 更新
     *
     * @param dataProductColumn DataProductColumn
     * @return int
     */
    int updateByPrimaryKey(DataProductColumn dataProductColumn);
}
