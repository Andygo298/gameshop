package com.github.andygo298.gameshop.dao.impl;

import com.github.andygo298.gameshop.dao.RedisDao;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisDaoImpl implements RedisDao {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisDaoImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean saveActivateCode(String activateCode, String userEmail) {
        try {
            redisTemplate.opsForHash().put(activateCode, activateCode, userEmail);
            redisTemplate.expire(activateCode, 1, TimeUnit.DAYS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getByActivateCode(String activateCode) {
        return (String) redisTemplate.opsForHash().get(activateCode, activateCode);
    }

    @Override
    public boolean saveForgotPasswordCode(String forgotPasswordCode, String userEmail) {
        try {
            redisTemplate.opsForHash().put(forgotPasswordCode, forgotPasswordCode, userEmail);
            redisTemplate.expire(forgotPasswordCode, 1, TimeUnit.HOURS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String getByForgotPasswordCode(String forgotPasswordCode) {
        return (String) redisTemplate.opsForHash().get(forgotPasswordCode, forgotPasswordCode);
    }

}
