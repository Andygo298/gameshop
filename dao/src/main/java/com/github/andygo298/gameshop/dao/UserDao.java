package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> getUserByEmail(String email);
    Optional<List<User>> findAllByRole(Role role);
}
