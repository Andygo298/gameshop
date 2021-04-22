package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.entity.User;

import java.util.Optional;

public interface UserService {
    boolean saveActivateCode(User user);
    boolean saveForgotPasswordCode(String email);
    String getActivateCode(String activateCode);
    String getByForgotPasswordCode(String forgotPasswordCode);
    User saveUser(User user);
    Optional<User> activateUserByCode(String activateCode);
    Optional<User> findByEmail(String email);
    Optional<User> login(String email, String password);
}
