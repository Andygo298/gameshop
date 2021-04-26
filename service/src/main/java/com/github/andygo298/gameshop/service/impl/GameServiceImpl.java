package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.GameDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class GameServiceImpl implements GameService {

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
    }

    @Override
    @Transactional
    public Game updateGame(Game gameToUpdate) {
        return gameDao.save(gameToUpdate);
    }

    @Override
    @Transactional
    public Optional<Game> getGameById(Integer gameId){
        return gameDao.findById(gameId);
    }

    @Override
    @Transactional
    public Optional<List<Game>> getAllGamesByUserId(Integer userId) {
        return Optional.empty();
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
                .orElseThrow(() -> new EntityNotFoundException("game not Found"));
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
    }
}
