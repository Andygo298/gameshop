package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Game saveGame(Game game);
    Game updateGame(Game gameToUpdate);
    Optional<Game> getGameById(Integer gameId);
    void addGameToTrader(UserEntity userEntity, Game game);
    List<Game> getAllGamesByUserId(Integer userId);
    List<Game> getAllGames();
    Game deleteGame(Integer gameId);
    void removeGameFromTrader(UserEntity userEntity, Game game);
}
