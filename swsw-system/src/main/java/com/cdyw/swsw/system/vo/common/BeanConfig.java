package com.cdyw.swsw.system.vo.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 配置类，获取风廓线雷达的文件存储地址
 *
 * @author 刘冬
 */
@Component
@ConfigurationProperties(prefix = "windprofileradar")
public class BeanConfig {

    private String path;

    public BeanConfig() {
        super();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


}
