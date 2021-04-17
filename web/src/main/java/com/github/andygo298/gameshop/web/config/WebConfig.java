package com.github.andygo298.gameshop.web.config;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.config.ServiceConfig;
import com.github.andygo298.gameshop.web.controller.TestController;
import com.github.andygo298.gameshop.web.controller.UserController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class WebConfig {

    private ServiceConfig serviceConfig;

    public WebConfig(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    //controllers:
    @Bean
    TestController testController() {
        return new TestController(serviceConfig.userService());
    }

    @Bean
    UserController userController() {
        return new UserController(serviceConfig.userService());
    }
    //others:

    @Bean
    @Description("Spring Message Resolver")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }
}
