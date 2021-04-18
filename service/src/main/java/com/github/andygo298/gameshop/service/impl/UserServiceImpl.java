package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.RedisDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.MailSenderService;
import com.github.andygo298.gameshop.service.UserService;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private RedisDao redisDao;
    @Autowired
    private UserDao userDao;
    private MailSenderService mailSenderService;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(RedisDao redisDao, MailSenderService mailSenderService, PasswordEncoder passwordEncoder) {
        this.redisDao = redisDao;
        this.mailSenderService = mailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public boolean saveActivateCode(User user) {
        String activateCode = UUID.randomUUID().toString();
        sendMessage(activateCode, user);
        return redisDao.saveActivateCode(activateCode, user.getEmail());
    }

    @Override
    @Transactional
    public String getActivateCode(String activateCode) {
        return redisDao.getByActivateCode(activateCode);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userDao.save(user);
    }

    @Override
    @Transactional
    public Optional<User> findByEmail(String userEmail) {
        return userDao.getUserByEmail(userEmail);
    }

    @Override
    @Transactional
    public Optional<User> login(String email, String password) {
        Optional<User> userByEmail = userDao.getUserByEmail(email);
        if (userByEmail.isPresent()) {
            String encodePass = userByEmail.get().getPassword();
            if (passwordEncoder.matches(password, encodePass)) {
                return userByEmail;
            } else return Optional.empty();
        } else return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<User> activateUserByCode(String activateCode) {
        String userEmail = redisDao.getByActivateCode(activateCode);
        if (Objects.nonNull(userEmail)) {
            return userDao.getUserByEmail(userEmail);
        } else {
            return Optional.empty();
        }
    }

    private void sendMessage(String activateCode, User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to GAMESHOP. Please, visit next link: http://%s/registration?activateCode=%s",
                    user.getFirstName(),
                    "localhost:80/gameshop",
                    activateCode
            );
            mailSenderService.send(user.getEmail(), "Activation code", message);
        }
    }
}
