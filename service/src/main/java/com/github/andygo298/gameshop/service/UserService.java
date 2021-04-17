package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.entity.User;

import java.util.Optional;

public interface UserService {
    boolean saveActivateCode(User user);
    String getActivateCode(String activateCode);
    public User saveUser(User user);
    Optional<User> findByEmail(String email);
    Optional<User> login(String email, String password);
}
