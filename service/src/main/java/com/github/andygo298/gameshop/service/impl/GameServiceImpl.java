package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.GameDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.User;
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
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);
    @Autowired
    private GameDao gameDao;
    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public Game saveGame(Game game) {
        return gameDao.save(game);
    }

    @Override
    @Transactional
    public void addGameToTrader(User user, Game game) {
        user.getGames().add(game);
        game.getUsers().add(user);
        userDao.save(user);
        gameDao.save(game);
        log.info("Game - {} was successfully added to User: {}.", game.getGameName(), user.getEmail());
    }

    @Override
    @Transactional
    public Game updateGame(Game gameToUpdate) {
        return gameDao.save(gameToUpdate);
    }

    @Override
    @Transactional
    public Optional<Game> getGameById(Integer gameId) {
        return gameDao.findById(gameId);
    }

    @Override
    @Transactional
    public List<Game> getAllGamesByUserId(Integer userId) {
        Optional<User> userById = userDao.findById(userId);
        if (userById.isPresent()) {
            return new ArrayList<>(userById.get().getGames());
        } else {
            log.error("User with id: {} not found.", userId);
            throw new EntityNotFoundException("user not found");
        }

    }

    @Override
    @Transactional
    public List<Game> getAllGames() {
        return gameDao.findAll();
    }

    @Override
    @Transactional
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
    @Transactional
    public void removeGameFromTrader(User user, Game game) {
        user.getGames().remove(game);
        game.getUsers().remove(user);
        userDao.save(user);
        gameDao.save(game);
        log.warn("Game - {} was successfully removed from User: {}.", game.getGameName(), user.getEmail());
    }
}
