package com.cdyw.swsw.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author jovi
 */
@MapperScan(value = "com.cdyw.swsw.system.dao")
@SpringBootApplication(scanBasePackages = {"com.cdyw.swsw"})
@EnableRedisHttpSession
public class SwswSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwswSystemApplication.class, args);
    }

}
