package com.cdyw.swsw.system.dao.product;

import com.cdyw.swsw.common.domain.entity.file.FileEntity;
import com.cdyw.swsw.common.domain.entity.product.DataProduct;
import com.cdyw.swsw.common.domain.entity.product.DataProductBase;
import com.cdyw.swsw.common.domain.entity.product.DataProductWeaGridForecast;
import com.cdyw.swsw.common.domain.entity.product.DataProductWeaShortForecast;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author jovi
 */
@Repository
public interface DataProductMapper {

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
     * @param dataProduct DataProduct
     * @return int
     */
    int insert(DataProduct dataProduct);

    /**
     * 根据主键查询对象
     *
     * @param id id
     * @return DataProduct
     */
    DataProduct selectByPrimaryKey(Integer id);

    /**
     * 查询所有
     *
     * @return List<DataProduct>
     */
    List<DataProduct> selectAll();

    /**
     * 根据条件查询所有
     *
     * @param dataProduct DataProduct
     * @return List<DataProduct>
     */
    List<DataProduct> selectDataByParam(@Param("dataProduct") DataProduct dataProduct);

    /**
     * 更新
     *
     * @param dataProduct DataProduct
     * @return int
     */
    int updateByPrimaryKey(DataProduct dataProduct);

    /**
     * 根据动态参数更新
     *
     * @param dataProduct DataProduct
     * @param list        List
     * @return int
     */
    int updateByParam(DataProduct dataProduct, List<Map<String, String>> list);

    /**
     * 测试查询 List<Map<String, String>>
     *
     * @param list List
     * @return List
     */
    List<String> testSelectListMap(List<Map<String, String>> list);

    /**
     * 新增基础信息
     *
     * @param dataProductBase DataProductBase
     * @return size
     */
    int insertDataProductBase(@Param("dataProductBase") DataProductBase dataProductBase);

    /**
     * 根据起报时间查询基础信息(只返回最新的数据)
     *
     * @param dataProductBase DataProductBase
     * @return DataProductBase
     */
    DataProductBase getDataProductBaseByTimeAndType(@Param("dataProductBase") DataProductBase dataProductBase);

    /**
     * 根据起报时间查询天气情况文本信息
     *
     * @param productId productId
     * @return DataProductWeaGridForecast
     */
    DataProductWeaGridForecast getWeaGridForecast(@Param("productId") Integer productId);

    /**
     * 生成产品
     *
     * @param fileEntity FileEntity
     * @return size
     */
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertDataProduct(@Param("fileEntity") FileEntity fileEntity);

    /**
     * 通过起报时间查询产品
     *
     * @param baseTime 起报时间
     * @param type     产品类型
     * @return FileEntity
     */
    List<DataProductBase> getHistoryDataProductByTime(@Param("baseTime") Long baseTime, @Param("type") Integer type);

    /**
     * 通过实体类查询数据库对应的实体数据
     *
     * @param fileEntity 实体
     * @return 实体数据
     */
    FileEntity getDataProductByEntity(@Param("fileEntity") FileEntity fileEntity);

    /**
     * 生成天气情况文本信息-产品1-常规短临预报
     *
     * @param dataProductWeaShortForecast DataProductWeaShortForecast
     * @return size
     */
    int insertWeaShortForecast(@Param("dataProductWeaShortForecast") DataProductWeaShortForecast dataProductWeaShortForecast);

    /**
     * 生成天气情况文本信息-产品2-精细化网格预报
     *
     * @param dataProductWeaGridForecast DataProductWeaGridForecast
     * @return size
     */
    int insertWeaGridForecast(@Param("dataProductWeaGridForecast") DataProductWeaGridForecast dataProductWeaGridForecast);

}