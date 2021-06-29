package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> getUserByEmail(String email);
    Optional<List<UserEntity>> findAllByRole(Role role);
}
