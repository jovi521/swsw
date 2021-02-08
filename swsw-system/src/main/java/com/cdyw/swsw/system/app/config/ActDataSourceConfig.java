package com.cdyw.swsw.system.app.config;

import org.activiti.spring.ProcessEngineFactoryBean;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 工作流 Activity 数据源的配置类，与主业务数据库分开
 *
 * @author jovi
 */
@Configuration
public class ActDataSourceConfig extends AbstractProcessEngineAutoConfiguration {

    private final DataSource dataSource;

    @Autowired
    public ActDataSourceConfig(@Qualifier("actDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Primary
    @Bean
    @Override
    public ProcessEngineFactoryBean springProcessEngineBean(SpringProcessEngineConfiguration configuration) {
        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        configuration.setDataSource(dataSource);
        processEngineFactoryBean.setProcessEngineConfiguration(configuration);
        return processEngineFactoryBean;
    }
}
