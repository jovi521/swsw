package com.cdyw.swsw.system.dao.dic;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface DicHeightMapper {

    /**
     * 查询所有高度
     *
     * @param productCode String
     * @param parentCode  String
     * @return List
     */
    List<Map<String, Object>> selectAllHeight(@Param("productCode") String productCode,
                                              @Param("parentCode") String parentCode);

}
