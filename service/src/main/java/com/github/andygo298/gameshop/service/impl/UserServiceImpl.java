package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.RedisDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.MailSenderService;
import com.github.andygo298.gameshop.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    private final RedisDao redisDao;
    private final MailSenderService mailSenderService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(RedisDao redisDao, MailSenderService mailSenderService, PasswordEncoder passwordEncoder) {
        this.redisDao = redisDao;
        this.mailSenderService = mailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public boolean saveActivateCode(User user) {
        String activateCode = UUID.randomUUID().toString();
        activateCodeSendMessage(activateCode, user);
        return redisDao.saveActivateCode(activateCode, user.getEmail());
    }

    @Override
    @Transactional
    public boolean saveForgotPasswordCode(String email) {
        String forgotPasswordCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999 + 1));
        forgotPasswordSendMessage(forgotPasswordCode, email);
        return redisDao.saveForgotPasswordCode(forgotPasswordCode, email);
    }

//    @Override
//    @Transactional
//    public String getActivateCode(String activateCode) {
//        return redisDao.getByActivateCode(activateCode);
//    }

    @Override
    @Transactional
    public String getByForgotPasswordCode(String forgotPasswordCode) {
        return redisDao.getByForgotPasswordCode(forgotPasswordCode);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userDao.saveAndFlush(user);
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



    private void activateCodeSendMessage(String activateCode, User user) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to GAMESHOP.\nPlease, visit next link: http://%s/auth/confirm?activateCode=%s",
                    user.getFirstName(),
                    "localhost:80/gameshop",
                    activateCode
            );
            mailSenderService.send(user.getEmail(), "Activation code", message);
        }
    }

    private void forgotPasswordSendMessage(String forgotPasswordCode, String email) {
        if (!StringUtils.isEmpty(email)) {
            String message = "Hello! \nYou told us you forgot your password.\n Your password reset code:"+ forgotPasswordCode
                    +"\nThis code will be active for 1 hour";
            mailSenderService.send(email, "Activation code", message);
        }
    }
}
