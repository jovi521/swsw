package com.cdyw.swsw.system.app.config;

import com.cdyw.swsw.system.security.config.SwswConfig;
import com.cdyw.swsw.system.vo.common.DeveloperApiInfo;
import com.cdyw.swsw.system.vo.common.DeveloperApiInfoExtension;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

/**
 * @author jovi
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    private final SwswConfig swswConfig;

    @Value(value = "${knife4j.contact.name}")
    private String name;

    @Value(value = "${knife4j.contact.url}")
    private String url;

    @Value(value = "${knife4j.contact.email}")
    private String email;

    @Autowired
    public SwaggerConfig(SwswConfig swswConfig) {
        this.swswConfig = swswConfig;
    }

    @Bean
    public Docket defaultApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(true)
                .apiInfo(apiInfo())
                //分组名称
                .groupName("2.X版本")
                .select()
                //这里指定扫描包路径
                .apis(RequestHandlerSelectors.basePackage(swswConfig.getBasePackage()))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 展示主页基础信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swswConfig.getTitle())
                .description(swswConfig.getDescription())
                .termsOfServiceUrl("http://www.swswsystem.com:8585/SWSWSystem/doc.html")
                .contact(new Contact(name, url, email))
                .version(swswConfig.getVersion())
                .license("Apache License, Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }

    private ApiInfo groupApiInfo() {
        DeveloperApiInfoExtension apiInfoExtension = new DeveloperApiInfoExtension();

        apiInfoExtension.addDeveloper(new DeveloperApiInfo("张三", "zhangsan@163.com", "Java"))
                .addDeveloper(new DeveloperApiInfo("李四", "lisi@163.com", "Java"));

        return new ApiInfoBuilder()
                .title("swagger-bootstrap-ui很棒~~~！！！")
                .description("<div style='font-size:14px;color:red;'>swagger-bootstrap-ui-demo RESTful APIs</div>")
                .termsOfServiceUrl("http://www.group.com/")
                .contact(new Contact(name, url, email))
                .version("1.0.0")
                //.extensions(Lists.newArrayList(apiInfoExtension))
                .build();
    }

    private Iterable<? extends SecurityScheme> apiKey() {
        return (Iterable<? extends SecurityScheme>) new ApiKey("BearerToken", "Authorization", "header");
    }

    private ApiKey apiKey1() {
        return new ApiKey("BearerToken1", "Authorization-x", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    private SecurityContext securityContext1() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth1())
                .forPaths(PathSelectors.regex("/.*"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("BearerToken", authorizationScopes));
    }

    List<SecurityReference> defaultAuth1() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("BearerToken1", authorizationScopes));
    }
}
