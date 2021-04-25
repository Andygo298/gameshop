package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.RatingTraderDto;
import com.github.andygo298.gameshop.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean saveActivateCode(User user);
    boolean saveForgotPasswordCode(String email);
    Optional<User> activateUserByCode(String activateCode);
    String getByForgotPasswordCode(String forgotPasswordCode);
    //    String getActivateCode(String activateCode);
    User saveUser(User user);
    Optional<User> findByEmail(String email);
    Optional<User> login(String email, String password);

}
