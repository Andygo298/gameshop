package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
    public Optional<User> getUserByEmail(String email);
    public Optional<User> getUserByEmailAndPassword(String email, String password);
}
