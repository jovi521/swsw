package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicDataProductFormColumnSelect;
import org.springframework.stereotype.Repository;

/**
 * @author jovi
 */
@Repository
public interface DicDataProductFormColumnSelectMapper {

    /**
     * 根据 dataProductType 查询对应的 DicDataProductFormColumn 集合
     *
     * @param columnId String
     * @return DicDataProductFormColumnSelect
     */
    DicDataProductFormColumnSelect selectByColumnId(String columnId);
}
