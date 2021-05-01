package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.User;
import com.github.andygo298.gameshop.service.GameService;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.request.GameRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private final UserService userService;
    private final GameService gameService;

    private Supplier<ResponseStatusException> gameNotFound = () -> {
        log.error("Game id is invalid or game not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game id is invalid or game not found");
    };
    private Supplier<ResponseStatusException> userNotFound = () -> {
        log.error("User id is invalid or user not found");
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User id is invalid or user not found");
    };

    public GameController(GameService gameService, UserService userService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/games")
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("/users/{id}/games")
    public ResponseEntity<List<Game>> getUserAllGames(@PathVariable("id") Integer userId) {
        List<Game> games = gameService.getAllGamesByUserId(userId);
        return ResponseEntity.ok(games);
    }

    @PostMapping("/users/{userId}/games/{gameId}")
    public ResponseEntity<String> addGameForTrader(@PathVariable("userId") Integer userId, @PathVariable("gameId") Integer gameId) {
        User user = userService.getUserById(userId).orElseThrow(userNotFound);
        Game game = gameService.getGameById(gameId).orElseThrow(gameNotFound);
        gameService.addGameToTrader(user, game);
        log.info("user - {} was successfully saved the game with id - {}.", userId, gameId);
        return ResponseEntity.ok("Game was added to user - " + user.getFirstName());
    }

    @PostMapping("/admin/games")
    public ResponseEntity<Game> saveGame(@RequestBody GameRequest gameRequest) {
        Game gameToSave = new Game.GameBuilder()
                .withGameName(gameRequest.getGameName())
                .withTitle(gameRequest.getTitle())
                .withPrice(gameRequest.getPrice())
                .build();
        log.info("Game - {} was successfully added by admin.",gameRequest.getGameName());
        return ResponseEntity.ok(gameService.saveGame(gameToSave));
    }

    @PutMapping("/games/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable("id") Integer gameId, @RequestBody GameRequest gameRequest) {
        Game gameToUpdate = gameService.getGameById(gameId)
                .orElseThrow(gameNotFound);
        gameToUpdate.setGameName(gameRequest.getGameName());
        gameToUpdate.setTitle(gameRequest.getTitle());
        gameToUpdate.setPrice(gameRequest.getPrice());
        return ResponseEntity.ok(gameService.updateGame(gameToUpdate));
    }

    @DeleteMapping("/users/{userId}/games/{gameId}")
    public ResponseEntity<String> removeGameFromTrader(@PathVariable("userId") Integer userId, @PathVariable("gameId") Integer gameId) {
        User user = userService.getUserById(userId).orElseThrow(userNotFound);
        Game game = gameService.getGameById(gameId).orElseThrow(gameNotFound);
        gameService.removeGameFromTrader(user, game);
        log.info("Game - {} was successfully removed from user - {}",game.getGameName(),user.getEmail());
        return ResponseEntity.ok("Game was removed from user - " + user.getFirstName());
    }

    @DeleteMapping("/admin/games/{id}")
    public ResponseEntity<Game> deleteGame(@PathVariable("id") Integer gameId) {
        log.info("Game with id - {} was successfully removed by admin",gameId);
        return ResponseEntity.ok(gameService.deleteGame(gameId));
    }
}
