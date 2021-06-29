package com.github.andygo298.gameshop.dao;

import com.github.andygo298.gameshop.model.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameDao extends JpaRepository<Game, Integer> {
}
