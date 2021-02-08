package com.cdyw.swsw.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jovi
 */
@SpringBootApplication
@EnableAdminServer
public class SwswAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwswAdminApplication.class, args);
    }

}
