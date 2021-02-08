package com.cdyw.swsw.system.dao.dic;

import com.cdyw.swsw.common.domain.ao.dic.DicWarningSignal;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author jovi
 */
@Repository
@Deprecated
public interface DicWarningSignalMapper {

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
     * @param dicWarningSignal DicWarningSignal
     * @return int
     */
    int insert(DicWarningSignal dicWarningSignal);

    /**
     * 根据主键查询对象
     *
     * @param id id
     * @return DataProduct
     */
    DicWarningSignal selectByPrimaryKey(Integer id);

    /**
     * 查询所有
     *
     * @return List<DicWarningSignal>
     */
    List<DicWarningSignal> selectAll();

    /**
     * 更新
     *
     * @param dicWarningSignal DataProduct
     * @return int
     */
    int updateByPrimaryKey(DicWarningSignal dicWarningSignal);

    /**
     * 根据四种特殊天气类型type查询所有
     *
     * @param signalType Integer
     * @return List<DicWarningSignal>
     */
    List<DicWarningSignal> selectBySignalType(Integer signalType);
}