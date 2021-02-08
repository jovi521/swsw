package com.cdyw.swsw.common.common.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;
import java.util.Properties;

/**
 * 引入外部配置文件（默认是properties文件，此处设置成也支持yaml/yml文件）
 *
 * @author jovi
 */
public class YamlPropertySourceFactory extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        String sourceName = (name == null) ? resource.getResource().getFilename() : name;
        assert sourceName != null;
        String sourceName1 = ".yml";
        String sourceName2 = ".yaml";
        if (sourceName.endsWith(sourceName1) || sourceName.endsWith(sourceName2)) {
            YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
            factory.setResources(resource.getResource());
            factory.afterPropertiesSet();
            Properties properties = factory.getObject();
            assert properties != null;
            return new PropertiesPropertySource(sourceName, properties);
        }
        return super.createPropertySource(name, resource);
    }

}
