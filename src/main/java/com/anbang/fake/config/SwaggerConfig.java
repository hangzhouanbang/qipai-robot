package com.anbang.fake.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/14 11:47 AM
 * @Version 1.0
 */
@Configuration
@EnableSwagger2
@EnableWebMvc
@ComponentScan(basePackages = {"com.anbang.fake.controller"})
public class SwaggerConfig {
    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Tom", "", "");
        return new ApiInfoBuilder()
                .title("机器人测试")
                .description("Tom的测试工具")
                .contact(contact)
                .version("1.1.0")
                .build();
    }
}