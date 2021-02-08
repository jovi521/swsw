package com.cdyw.swsw.data.domain.dao.colorvalue;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Repository
public interface ColorValueMapper {

    /**
     * 根据类型查询色标值
     *
     * @param type
     * @return
     */
    public List<HashMap<String, Object>> getByType(Integer type);

    /**
     * 插入色标
     *
     * @param type
     * @param criticalvalue
     * @param colorvalue
     * @return
     */
    public int insert(@Param("type") String type, @Param("criticalvalue") Float criticalvalue, @Param("colorvalue") String colorvalue);

    /**
     * 插入色标
     *
     * @param type
     * @param criticalvalue
     * @param colorvalue
     * @return
     */
    public int insert2(@Param("type") String type, @Param("criticalvalue") Float criticalvalue, @Param("colorvalue") String colorvalue,
                       @Param("station_code") String station_code, @Param("parent_code") String parent_code);
}
