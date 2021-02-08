package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicDataProductFormColumn;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
public interface DicDataProductFormColumnMapper {

    /**
     * 根据 dataProductType 查询对应的 DicDataProductFormColumn 集合
     *
     * @param dataProductType String
     * @return DicDataProductFormColumn
     */
    List<DicDataProductFormColumn> selectByType(String dataProductType);

    /**
     * 根据主键查询对象
     *
     * @param id id
     * @return DataProductColumn
     */
    DicDataProductFormColumn selectByPrimaryKey(Integer id);

    /**
     * 根据ColumnKey查询对象
     *
     * @param columnKey String
     * @return DataProductColumn
     */
    DicDataProductFormColumn selectByColumnKey(String columnKey);
}
