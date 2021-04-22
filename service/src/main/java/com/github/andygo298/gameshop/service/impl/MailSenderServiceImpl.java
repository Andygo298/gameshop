package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.service.MailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class MailSenderServiceImpl implements MailSenderService {

   private final JavaMailSender javaMailSender;

    public MailSenderServiceImpl(JavaMailSender mailSender) {
        this.javaMailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void send(String emailTo, String subject, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(username);
        mailMessage.setTo(emailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);
    }
}