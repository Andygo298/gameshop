package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.GameDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    @Autowired
    private GameDao gameDao;
    @Autowired
    private UserDao userDao;

    @Override
    public Game saveGame(Game game) {
        return gameDao.save(game);
    }

    @Override
    public void addGameToTrader(UserEntity userEntity, Game game) {
        userEntity.getGames().add(game);
        game.getUserEntities().add(userEntity);
        userDao.save(userEntity);
        gameDao.save(game);
        log.info("Game - {} was successfully added to User: {}.", game.getGameName(), userEntity.getEmail());
    }

    @Override
    public Game updateGame(Game gameToUpdate) {
        return gameDao.save(gameToUpdate);
    }

    @Override
    public Optional<Game> getGameById(Integer gameId) {
        return gameDao.findById(gameId);
    }

    @Override
    public List<Game> getAllGamesByUserId(Integer userId) {
        Optional<UserEntity> userById = userDao.findById(userId);
        if (userById.isPresent()) {
            return new ArrayList<>(userById.get().getGames());
        } else {
            log.error("User with id: {} not found.", userId);
            throw new EntityNotFoundException("user not found");
        }

    }

    @Override
    public List<Game> getAllGames() {
        return gameDao.findAll();
    }

    @Override
    public Game deleteGame(Integer gameId) {
        Game game = gameDao.findById(gameId)
                .orElseThrow(() -> {
                    log.error("Game with id: {} not found.", gameId);
                    return new EntityNotFoundException("game not Found");
                });
        game.setDelete(true);
        return gameDao.save(game);
    }

    @Override
    public void removeGameFromTrader(UserEntity userEntity, Game game) {
        userEntity.getGames().remove(game);
        game.getUserEntities().remove(userEntity);
        userDao.save(userEntity);
        gameDao.save(game);
        log.warn("Game - {} was successfully removed from User: {}.", game.getGameName(), userEntity.getEmail());
    }
}
