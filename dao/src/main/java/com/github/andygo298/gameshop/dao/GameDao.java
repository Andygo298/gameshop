package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameDao extends JpaRepository<Game, Integer> {
}
