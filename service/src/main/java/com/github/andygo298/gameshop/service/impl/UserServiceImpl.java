package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.RedisDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.model.enums.Role;
import com.github.andygo298.gameshop.service.MailSenderService;
import com.github.andygo298.gameshop.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    private final RedisDao redisDao;
    private final MailSenderService mailSenderService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(RedisDao redisDao, MailSenderService mailSenderService) {
        this.redisDao = redisDao;
        this.mailSenderService = mailSenderService;
    }

    @Override
    public boolean saveActivateCode(UserEntity userEntity) {
        String activateCode = UUID.randomUUID().toString();
        activateCodeSendMessage(activateCode, userEntity);
        return redisDao.saveActivateCode(activateCode, userEntity.getEmail());
    }

    @Override
    public boolean saveForgotPasswordCode(String email) {
        String forgotPasswordCode = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999 + 1));
        forgotPasswordSendMessage(forgotPasswordCode, email);
        return redisDao.saveForgotPasswordCode(forgotPasswordCode, email);
    }

    @Override
    public String getByForgotPasswordCode(String forgotPasswordCode) {
        return redisDao.getByForgotPasswordCode(forgotPasswordCode);
    }

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        return userDao.save(userEntity);
    }

    @Override
    public Optional<UserEntity> findByEmail(String userEmail) {
        return userDao.getUserByEmail(userEmail);
    }

    @Override
    public Optional<UserEntity> activateUserByCode(String activateCode) {
        String userEmail = redisDao.getByActivateCode(activateCode);
        if (Objects.nonNull(userEmail)) {
            return userDao.getUserByEmail(userEmail);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<UserEntity>> findAllByRole(Role role) {
        return userDao.findAllByRole(role);
    }

    @Override
    public Optional<UserEntity> getUserById(Integer userId) {
        return userDao.findById(userId);
    }

    private void activateCodeSendMessage(String activateCode, UserEntity userEntity) {
        if (!StringUtils.isEmpty(userEntity.getEmail())) {
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to GAMESHOP.\nPlease, visit next link: http://%s/auth/confirm?activateCode=%s",
                    userEntity.getFirstName(),
                    "localhost:80/gameshop",
                    activateCode
            );
            mailSenderService.send(userEntity.getEmail(), "Activation code", message);
            log.info("Email with activateCode: {} was sent to: {}.", activateCode, userEntity.getEmail());
        }
    }

    private void forgotPasswordSendMessage(String forgotPasswordCode, String email) {
        if (!StringUtils.isEmpty(email)) {
            String message = "Hello! \nYou told us you forgot your password.\n Your password reset code:" + forgotPasswordCode
                    + "\nThis code will be active for 1 hour";
            mailSenderService.send(email, "Forgot password code", message);
            log.info("Email with forgotPasswordCode: {} was sent to: {}.", forgotPasswordCode, email);
        }
    }
}
