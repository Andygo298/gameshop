package com.github.andygo298.gameshop.service.impl;

import com.github.andygo298.gameshop.dao.GameDao;
import com.github.andygo298.gameshop.dao.UserDao;
import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private GameDao gameDao;
    @Mock
    private UserDao userDao;
    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    void saveGameTest() {
        Game game = new Game.GameBuilder()
                .withGameName("World of warCraft")
                .withTitle("RPG game")
                .withPrice(24)
                .build();
        when(gameDao.save(game)).thenReturn(game);
        Game actualGame = gameService.saveGame(game);
        assertNotNull(actualGame);
        assertEquals(game.getGameName(),actualGame.getGameName());
    }

    @Test
    void updateGameTest() {
        Game gameUpd = new Game.GameBuilder()
                .withGameName("World of warCraft")
                .withTitle("update RPG game")
                .withPrice(22)
                .build();
        when(gameDao.save(gameUpd)).thenReturn(gameUpd);
        Game actualGame = gameService.saveGame(gameUpd);
        assertNotNull(actualGame);
        assertEquals(gameUpd.getGameName(),actualGame.getGameName());
    }

    @Test
    void getGameByIdTest() {
        Game game = new Game.GameBuilder()
                .withGameName("World of warCraft")
                .withTitle("RPG game")
                .withPrice(24)
                .build();
        game.setGameId(1);
        when(gameDao.findById(game.getGameId())).thenReturn(Optional.of(game));
        Optional<Game> actualGameById = gameService.getGameById(game.getGameId());
        assertTrue(actualGameById.isPresent());
        assertEquals(game.getGameName(),actualGameById.get().getGameName());
    }

    @Test
    void getAllGamesByUserIdTest() {
        Game game1 = new Game.GameBuilder()
                .withGameName("World of warCraft")
                .withTitle("RPG game")
                .withPrice(24)
                .build();
        game1.setGameId(1);
        Game game2 = new Game.GameBuilder()
                .withGameName("World of Tanks")
                .withTitle("RPG war game")
                .withPrice(20)
                .build();
        game2.setGameId(2);
        List<Game> games = new ArrayList<>(Arrays.asList(game1, game2));
        User user = new User();
        user.setFirstName("test");
        user.setUserId(1);
        user.getGames().addAll(games);
        when(userDao.findById(user.getUserId())).thenReturn(Optional.of(user));
        List<Game> allGamesByUserId = gameService.getAllGamesByUserId(user.getUserId());
        assertFalse(allGamesByUserId.isEmpty());
        assertEquals(games.get(0).getGameName(),allGamesByUserId.get(0).getGameName());
    }

    @Test
    void getAllGamesTest() {
        Game game1 = new Game.GameBuilder()
                .withGameName("World of warCraft")
                .withTitle("RPG game")
                .withPrice(24)
                .build();
        game1.setGameId(1);
        Game game2 = new Game.GameBuilder()
                .withGameName("World of Tanks")
                .withTitle("RPG war game")
                .withPrice(20)
                .build();
        game2.setGameId(2);
        List<Game> games = new ArrayList<>(Arrays.asList(game1, game2));
        when(gameDao.findAll()).thenReturn(games);
        List<Game> allGames = gameService.getAllGames();
        assertFalse(allGames.isEmpty());
        assertEquals(game1.getGameName(),allGames.get(0).getGameName());
    }

    @Test
    void deleteGameTest() {
        Game game = new Game.GameBuilder()
                .withGameName("World of warCraft")
                .withTitle("RPG game")
                .withPrice(24)
                .build();
        game.setGameId(1);
        Game gameToDel = new Game.GameBuilder()
                .withGameName("World of warCraft")
                .withTitle("RPG game")
                .withPrice(24)
                .build();
        gameToDel.setGameId(1);
        gameToDel.setDelete(true);
        when(gameDao.findById(gameToDel.getGameId())).thenReturn(Optional.of(game));
        when(gameDao.save(game)).thenReturn(gameToDel);
        Game actualDelGame = gameService.deleteGame(game.getGameId());
        assertNotNull(actualDelGame);
        assertEquals(gameToDel.isDelete(),actualDelGame.isDelete());
    }

}