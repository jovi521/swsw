package com.cdyw.swsw.system.dao.api;

import com.cdyw.swsw.common.domain.entity.api.SysApi;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 系统url地址
 *
 * @author cdyw
 */
@Repository
public interface SysApiMapper {

    /**
     * 通过roleid查询url地址
     *
     * @param roleId
     * @return
     */
    List<SysApi> getApiUrlByRoleid(Integer roleId);

    /**
     * 通过userid查询url地址
     *
     * @param userId
     * @return
     */
    List<SysApi> getApiUrlByUserid(Integer userId);

    /**
     * 通过父id查询url地址
     *
     * @param pid
     * @return
     */
    List<SysApi> getApiUrlByPid(Integer pid);
}
