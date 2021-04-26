package com.github.andygo298.gameshop.dao;

public interface RedisDao {
    boolean saveActivateCode(String activateCode, String userEmail);
    String getByActivateCode(String activateCode);
    boolean saveForgotPasswordCode(String forgotPasswordCode, String userEmail);
    String getByForgotPasswordCode(String forgotPasswordCode);
}
