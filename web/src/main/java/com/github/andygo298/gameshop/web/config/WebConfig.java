package com.github.andygo298.gameshop.web.config;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.CommentService;
import com.github.andygo298.gameshop.service.config.ServiceConfig;
import com.github.andygo298.gameshop.web.controller.CommentController;
import com.github.andygo298.gameshop.web.controller.TestController;
import com.github.andygo298.gameshop.web.controller.UserController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        return new UserController(serviceConfig.userService(), passwordEncoder());
    }

    @Bean
    public CommentController commentController() {
        return new CommentController(serviceConfig.commentService(), serviceConfig.userService());
    }
    //others:

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    @Description("Spring Message Resolver")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }
}
