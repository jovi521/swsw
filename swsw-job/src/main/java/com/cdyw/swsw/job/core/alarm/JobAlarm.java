package com.cdyw.swsw.job.core.alarm;

import com.cdyw.swsw.job.core.model.XxlJobInfo;
import com.cdyw.swsw.job.core.model.XxlJobLog;

/**
 * @author xuxueli 2020-01-19
 */
public interface JobAlarm {

    /**
     * job alarm
     *
     * @param info
     * @param jobLog
     * @return
     */
    boolean doAlarm(XxlJobInfo info, XxlJobLog jobLog);

}
