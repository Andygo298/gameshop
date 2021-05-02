package com.github.andygo298.gameshop.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfig implements WebMvcConfigurer {

    public static final String TAG_1 = "User controller - ";
    public static final String TAG_2 = "Comment controller - ";
    public static final String TAG_3 = "Game controller - ";

    @Bean
    public Docket moviesAPI() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.andygo298.gameshop.web.controller"))
                .paths(PathSelectors.any())
                .build()
                .tags(new Tag(TAG_1, "Registration and authorization operations."))
                .tags(new Tag(TAG_2, "Comments operations."))
                .tags(new Tag(TAG_3, "Games operations."))
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Andrew Lozouski", "https://www.linkedin.com/in/andrei-lozouski/", "andygo298@gmail.com");
        StringVendorExtension listVendorExtension = new StringVendorExtension("Game SHOP", "Candidate");
        return new ApiInfo("Game shop RestFul Service API",
                "Game shop app",
                "1.0",
                "",
                (Contact) contact,
                "GameShop - Source Code"
                , "https://github.com/andygo298",
                Collections.singletonList(listVendorExtension));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
