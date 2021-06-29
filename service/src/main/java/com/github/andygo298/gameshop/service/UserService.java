package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.model.enums.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    boolean saveActivateCode(UserEntity userEntity);
    boolean saveForgotPasswordCode(String email);
    Optional<UserEntity> activateUserByCode(String activateCode);
    String getByForgotPasswordCode(String forgotPasswordCode);
    UserEntity saveUser(UserEntity userEntity);
    Optional<UserEntity> getUserById(Integer userId);
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> login(String email, String password);
    Optional<List<UserEntity>> findAllByRole(Role role);
 }
