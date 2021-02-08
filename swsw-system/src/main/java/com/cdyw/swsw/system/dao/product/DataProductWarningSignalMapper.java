package com.cdyw.swsw.system.dao.product;

import com.cdyw.swsw.common.domain.entity.product.DataProductWarningSignal;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
@Deprecated
public interface DataProductWarningSignalMapper {

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
     * @param dataProductWarningSignal DataProduct
     * @return int
     */
    int insert(DataProductWarningSignal dataProductWarningSignal);

    /**
     * 根据主键查询对象
     *
     * @param id id
     * @return DataProduct
     */
    DataProductWarningSignal selectByPrimaryKey(Integer id);

    /**
     * 查询所有
     *
     * @return List<DataWarningSignal>
     */
    List<DataProductWarningSignal> selectAll();

    /**
     * 更新
     *
     * @param dataProductWarningSignal DataProduct
     * @return int
     */
    int updateByPrimaryKey(DataProductWarningSignal dataProductWarningSignal);

    /**
     * 根据dataProductId查询所有预警信号
     *
     * @param dataProductId Integer
     * @return List<DataWarningSignal>
     */
    List<DataProductWarningSignal> selectByDataProductId(Integer dataProductId);
}