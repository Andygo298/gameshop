package com.github.andygo298.gameshop.service;

public interface MailSenderService {
    void send(String emailTo, String subject, String message);
}
