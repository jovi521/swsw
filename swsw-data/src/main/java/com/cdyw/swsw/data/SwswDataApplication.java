package com.cdyw.swsw.data;

import cn.shuibo.annotation.EnableSecurity;
import com.cdyw.swsw.common.common.component.CommonPath;
import com.cdyw.swsw.common.domain.ao.enums.FileMonitorTypeEnum;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jovi
 */
@EnableSecurity
@MapperScan(value = {"com.cdyw.swsw.data.domain.dao"})
@SpringBootApplication(scanBasePackages = {"com.cdyw.swsw"})
public class SwswDataApplication {

    private final FileMonitorTypeEnum fileMonitorTypeEnum = FileMonitorTypeEnum.TYPE_IO;

    private final CommonPath commonPath;

    public SwswDataApplication(CommonPath commonPath) {
        this.commonPath = commonPath;
    }

    public static void main(String[] args) {
        SpringApplication.run(SwswDataApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        String fileMonitorPath = commonPath.getDisk() + commonPath.getCommonPath();
//        FileMonitorUtil.fileMonitor(fileMonitorTypeEnum, fileMonitorPath);
//    }
}
