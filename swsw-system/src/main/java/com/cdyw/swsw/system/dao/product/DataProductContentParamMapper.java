package com.cdyw.swsw.system.dao.product;

import com.cdyw.swsw.common.domain.entity.product.DataProductContentParam;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DataProductContentParamMapper {

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
     * @param dataProductContentParam DataProductContentParam
     * @return int
     */
    int insert(DataProductContentParam dataProductContentParam);

    /**
     * 插入对象
     *
     * @param dataProductContentParam DataProductContentParam
     * @return int
     */
    int insertFinal(DataProductContentParam dataProductContentParam);

    /**
     * 根据主键查询对象
     *
     * @param id id
     * @return DataProductContentParam
     */
    DataProductContentParam selectByPrimaryKey(Integer id);

    /**
     * 根据dataProductId查询对象
     *
     * @param id id
     * @return DataProductContentParam
     */
    DataProductContentParam selectById(Integer id);

    /**
     * 查询所有
     *
     * @return List<DataProductContentParam>
     */
    List<DataProductContentParam> selectAll();

    /**
     * 更新
     *
     * @param dataProductContentParam DataProductContentParam
     * @return int
     */
    int updateByPrimaryKey(DataProductContentParam dataProductContentParam);
}
