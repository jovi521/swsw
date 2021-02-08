package com.cdyw.swsw.system.dao.log;

import com.cdyw.swsw.common.domain.entity.log.DataMonitorLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 50699
 */
@Repository
public interface DataMonitorLogMapper {

    /**
     * 根据id删除
     *
     * @param id Integer
     * @return int
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增
     *
     * @param record DataMonitorLog
     * @return int
     */
    int insert(DataMonitorLog record);

    /**
     * 根据id查询对象
     *
     * @param id Integer
     * @return DataMonitorLog
     */
    DataMonitorLog selectByPrimaryKey(Integer id);

    /**
     * 查询所有对象
     *
     * @return List
     */
    List<DataMonitorLog> selectll();

    /**
     * 根据id更新对象
     *
     * @param record DataMonitorLog
     * @return int
     */
    int updateByPrimaryKey(DataMonitorLog record);
}