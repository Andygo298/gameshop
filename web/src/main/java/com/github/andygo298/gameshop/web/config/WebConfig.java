package com.github.andygo298.gameshop.web.config;

import com.github.andygo298.gameshop.service.config.ServiceConfig;
import com.github.andygo298.gameshop.web.controller.CommentController;
import com.github.andygo298.gameshop.web.controller.GameController;
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
    UserController userController() {
        return new UserController(serviceConfig.userService(), passwordEncoder());
    }

    @Bean
    public CommentController commentController() {
        return new CommentController(serviceConfig.commentService(), serviceConfig.userService());
    }

    @Bean
    public GameController gameController() {
        return new GameController(serviceConfig.gameService(), serviceConfig.userService());
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
