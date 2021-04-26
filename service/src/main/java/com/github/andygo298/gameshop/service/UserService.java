package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean saveActivateCode(User user);
    boolean saveForgotPasswordCode(String email);
    Optional<User> activateUserByCode(String activateCode);
    String getByForgotPasswordCode(String forgotPasswordCode);
    User saveUser(User user);
    Optional<User> getUserById(Integer userId);
    Optional<User> findByEmail(String email);
    Optional<User> login(String email, String password);
    Optional<List<User>> findAllByRole(Role role);
 }
