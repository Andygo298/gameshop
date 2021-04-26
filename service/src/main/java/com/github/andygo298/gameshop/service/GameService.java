package com.github.andygo298.gameshop.service;

import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Game saveGame(Game game);
    Game updateGame(Game gameToUpdate);
    Optional<Game> getGameById(Integer gameId);
    void addGameToTrader(User user, Game game);
    Optional<List<Game>> getAllGamesByUserId(Integer userId);
    List<Game> getAllGames();
    Game deleteGame(Integer gameId);
    void removeGameFromTrader(User user, Game game);
}
