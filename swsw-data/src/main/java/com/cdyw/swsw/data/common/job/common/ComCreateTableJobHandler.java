package com.cdyw.swsw.data.common.job.common;

import com.cdyw.swsw.data.domain.service.common.ComCreateTableService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定期创建表的定时器
 *
 * @author jovi
 */
@Slf4j
@Component
public class ComCreateTableJobHandler extends IJobHandler {

    private final ComCreateTableService comCreateTableService;

    @Autowired
    public ComCreateTableJobHandler(ComCreateTableService comCreateTableService) {
        this.comCreateTableService = comCreateTableService;
    }

    /**
     * 创建所有数据表、日志表
     */
    @XxlJob("comCreateTableJobHandler")
    @Override
    public ReturnT<String> execute(String param) {
        int size = comCreateTableService.createTables();
        XxlJobLogger.log(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE) + " 总共创建了 " + size + " 张表。");
        return ReturnT.SUCCESS;
    }
}
