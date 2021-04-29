package com.github.andygo298.gameshop.dao;


import com.github.andygo298.gameshop.dao.config.DaoConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DaoConfig.class)
public class RedisDaoImplTest {

    @Autowired
    private RedisDao redisDao;

    @Test
    void saveActivateCodeTest() {
        final String testEmail = "test1@gmail.com";
        final String testActivateCode = "2021java";
        boolean saveResult = redisDao.saveActivateCode(testActivateCode, testEmail);
        assertTrue(saveResult);
    }

    @Test
    void getActivateCodeTest() {
        final String testEmail = "test2@gmail.com";
        final String testActivateCode = "2022java";
        redisDao.saveActivateCode(testActivateCode, testEmail);
        String actualEmail = redisDao.getByActivateCode(testActivateCode);
        assertEquals(testEmail, actualEmail);
    }

    @Test
    void saveForgotPasswordCodeTest() {
        final String testEmail = "test3@gmail.com";
        final String testForgotPasswordCode = "2023java";
        boolean saveResult = redisDao.saveForgotPasswordCode(testForgotPasswordCode, testEmail);
        assertTrue(saveResult);
    }

    @Test
    void getForgotPasswordCodeTest() {
        final String testEmail = "test4@gmail.com";
        final String testForgotPasswordCode = "2024java";
        redisDao.saveActivateCode(testForgotPasswordCode, testEmail);
        String actualEmail = redisDao.getByActivateCode(testForgotPasswordCode);
        assertEquals(testEmail, actualEmail);
    }
}
