package com.github.andygo298.gameshop.service.config;

import com.github.andygo298.gameshop.dao.config.DaoConfig;
import com.github.andygo298.gameshop.service.CommentService;
import com.github.andygo298.gameshop.service.GameService;
import com.github.andygo298.gameshop.service.MailSenderService;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.service.impl.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Properties;

@Configuration
@PropertySource("classpath:mail.properties")
public class ServiceConfig {

    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String userName;
    @Value("${spring.mail.password}")
    private String password;

    private DaoConfig daoConfig;

    public ServiceConfig(DaoConfig daoConfig) {
        this.daoConfig = daoConfig;
    }

    @Bean
    public JwtUserDetailsServiceImpl jwtUserDetailsService(){
        return new JwtUserDetailsServiceImpl();
    }

    @Bean
    public UserService userService() {
        return new UserServiceImpl(daoConfig.redisDao(), mailSenderService(), passwordEncoder());
    }

    @Bean
    public CommentService commentService() {
        return new CommentServiceImpl();
    }

    @Bean
    public GameService gameService() {
        return new GameServiceImpl();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public MailSenderService mailSenderService() {
        return new MailSenderServiceImpl(getJavaMailSender());
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(userName);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }
}
