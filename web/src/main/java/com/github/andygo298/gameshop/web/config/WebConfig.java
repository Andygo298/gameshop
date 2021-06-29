package com.github.andygo298.gameshop.web.config;

import com.github.andygo298.gameshop.service.config.ServiceConfig;
import com.github.andygo298.gameshop.web.controller.CommentController;
import com.github.andygo298.gameshop.web.controller.GameController;
import com.github.andygo298.gameshop.web.controller.UserController;
import com.github.andygo298.gameshop.web.jwt.JwtAuthenticationEntryPoint;
import com.github.andygo298.gameshop.web.jwt.JwtRequestFilter;
import com.github.andygo298.gameshop.web.jwt.JwtTokenUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
@EnableWebMvc
//@ComponentScan(basePackages = "com.github.andygo298.gameshop")
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
    public JwtRequestFilter jwtRequestFilter(){
        return new JwtRequestFilter(serviceConfig.jwtUserDetailsService(),jwtTokenUtil());
    }
    @Bean
    public JwtTokenUtil jwtTokenUtil(){
        return new JwtTokenUtil();
    }

    @Bean
    @Description("Spring Message Resolver")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }
}
