package com.cdyw.swsw.system.app.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.cdyw.swsw.system.app.component.ActDataSourceProperties;
import com.cdyw.swsw.system.app.component.BusinessDataSourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 所有数据源的配置类
 *
 * @author jovi
 */
@Configuration
public class DataSourceConfig {

    private final BusinessDataSourceProperties businessProperties;

    private final ActDataSourceProperties actProperties;

    @Autowired
    public DataSourceConfig(BusinessDataSourceProperties businessProperties, ActDataSourceProperties actProperties) {
        this.businessProperties = businessProperties;
        this.actProperties = actProperties;
    }

    /**
     * 此处切记切换为 Druid ，spring 的 DataSourceBuilder 目前默认支持
     * "com.zaxxer.hikari.HikariDataSource",
     * "org.apache.tomcat.jdbc.pool.DataSource",
     * "org.apache.commons.dbcp2.BasicDataSource"
     */
    @Primary
    @Bean("businessDataSource")
    public DataSource businessDataSource() {
        return getDataSource(businessProperties.getDriverClassName(), businessProperties.getUsername(), businessProperties.getUrl(), businessProperties.getPassword());
    }

    @Bean("actDataSource")
    public DataSource actDataSource() {
        return getDataSource(actProperties.getDriverClassName(), actProperties.getUsername(), actProperties.getUrl(), actProperties.getPassword());
    }

    private DataSource getDataSource(String driverClassName, String username, String url, String password) {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.type(DruidDataSource.class);
        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.username(username);
        dataSourceBuilder.url(url);
        dataSourceBuilder.password(password);
        return dataSourceBuilder.build();
    }
}
