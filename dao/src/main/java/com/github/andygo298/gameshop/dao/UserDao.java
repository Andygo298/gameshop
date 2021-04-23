package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> getUserByEmail(String email);
}
