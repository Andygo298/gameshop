package com.github.andygo298.gameshop.dao;

public interface RedisDao {
    public boolean saveActivateCode(String activateCode, String userEmail);
    public String getByActivateCode(String activateCode);
    public boolean saveForgotPasswordCode(String forgotPasswordCode, String userEmail);
    public String getByForgotPasswordCode(String forgotPasswordCode);
}
