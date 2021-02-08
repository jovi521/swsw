package com.cdyw.swsw.system.vo.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author jovi
 */
@Data
@Component
@ConfigurationProperties(prefix = "knife4j.contact")
public class DeveloperApiContact {

    private String name;

    private String url;

    private String email;

}
