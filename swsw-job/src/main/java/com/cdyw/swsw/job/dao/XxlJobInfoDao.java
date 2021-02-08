package com.cdyw.swsw.job.dao;

import com.cdyw.swsw.job.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * job info
 *
 * @author xuxueli 2016-1-12 18:03:45
 */
@Mapper
public interface XxlJobInfoDao {

    List<XxlJobInfo> pageList(@Param("offset") int offset,
                              @Param("pagesize") int pagesize,
                              @Param("jobGroup") int jobGroup,
                              @Param("triggerStatus") int triggerStatus,
                              @Param("jobDesc") String jobDesc,
                              @Param("executorHandler") String executorHandler,
                              @Param("author") String author);

    int pageListCount(@Param("offset") int offset,
                      @Param("pagesize") int pagesize,
                      @Param("jobGroup") int jobGroup,
                      @Param("triggerStatus") int triggerStatus,
                      @Param("jobDesc") String jobDesc,
                      @Param("executorHandler") String executorHandler,
                      @Param("author") String author);

    int save(XxlJobInfo info);

    XxlJobInfo loadById(@Param("id") int id);

    int update(XxlJobInfo xxlJobInfo);

    int delete(@Param("id") long id);

    List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

    int findAllCount();

    List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize);

    int scheduleUpdate(XxlJobInfo xxlJobInfo);


}
