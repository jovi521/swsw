package com.cdyw.swsw.data.pwd;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 用来存放配置文件中所有需要加密得值
 *
 * @author jovi
 */
@Getter
@Component
public class PasswordListComponent {

    @Value(value = "${spring.datasource.druid.password}")
    private String mysqlOriginPswd;

    @Value(value = "${spring.datasource.druid.stat-view-servlet.login-password}")
    private String druidOriginPswd;

    //    @Value(value = "${spring.redis.password}")
    private String redisOriginPswd;

    @Value(value = "${spring.mail.password}")
    private String mailOriginPswd;

}
