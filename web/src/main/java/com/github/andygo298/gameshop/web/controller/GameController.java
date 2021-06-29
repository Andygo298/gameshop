package com.github.andygo298.gameshop.web.controller;

import com.github.andygo298.gameshop.model.entity.Game;
import com.github.andygo298.gameshop.model.entity.UserEntity;
import com.github.andygo298.gameshop.service.GameService;
import com.github.andygo298.gameshop.service.UserService;
import com.github.andygo298.gameshop.web.config.SwaggerConfig;
import com.github.andygo298.gameshop.web.controller.util.ExceptionMessagesUtil;
import com.github.andygo298.gameshop.web.request.GameRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Api(tags = { SwaggerConfig.TAG_3 })
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private final UserService userService;
    private final GameService gameService;


    public GameController(GameService gameService, UserService userService) {
        this.userService = userService;
        this.gameService = gameService;
    }

    @ApiOperation("Returns all games.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List with games was successfully returned.")
            }
    )
    @GetMapping("/games")
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.getAllGames();
        return ResponseEntity.ok(games);
    }

    @ApiOperation("Returns user's games using User ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "List with user's games was successfully returned."),
                    @ApiResponse(code = 404, message = "User not found.")

            }
    )
    @GetMapping("/users/{userId}/games")
    public ResponseEntity<List<Game>> getUserAllGames(@PathVariable("userId") Integer userId) {
        List<Game> games = gameService.getAllGamesByUserId(userId);
        return ResponseEntity.ok(games);
    }

    @ApiOperation("Add game to Trader.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Game was successfully added to Trader."),
                    @ApiResponse(code = 404, message = "User or game not found.")

            }
    )
    @PostMapping("/users/{userId}/games/{gameId}")
    public ResponseEntity<String> addGameForTrader(@PathVariable("userId") Integer userId, @PathVariable("gameId") Integer gameId) {
        UserEntity userEntity = userService.getUserById(userId)
                .orElseThrow(ExceptionMessagesUtil.userNotFound);
        Game game = gameService.getGameById(gameId)
                .orElseThrow(ExceptionMessagesUtil.gameNotFound);
        gameService.addGameToTrader(userEntity, game);
        log.info("user - {} was successfully saved the game with id - {}.", userId, gameId);
        return ResponseEntity.ok("Game was added to user - " + userEntity.getFirstName());
    }

    @ApiOperation("Create game.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Game was successfully created."),
            }
    )
    @PostMapping("/admin/games")
    public ResponseEntity<Game> saveGame(@RequestBody GameRequest gameRequest) {
        Game gameToSave = new Game.GameBuilder()
                .withGameName(gameRequest.getGameName())
                .withTitle(gameRequest.getTitle())
                .withPrice(gameRequest.getPrice())
                .build();
        log.info("Game - {} was successfully added by admin.",gameRequest.getGameName());
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.saveGame(gameToSave));
    }

    @ApiOperation("Updates game using game ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Game was successfully updated."),
                    @ApiResponse(code = 404, message = "Game not found.")
            }
    )
    @PutMapping("/games/{gameId}")
    public ResponseEntity<Game> updateGame(@PathVariable("gameId") Integer gameId, @RequestBody GameRequest gameRequest) {
        Game gameToUpdate = gameService.getGameById(gameId)
                .orElseThrow(ExceptionMessagesUtil.gameNotFound);
        gameToUpdate.setGameName(gameRequest.getGameName());
        gameToUpdate.setTitle(gameRequest.getTitle());
        gameToUpdate.setPrice(gameRequest.getPrice());
        return ResponseEntity.ok(gameService.updateGame(gameToUpdate));
    }

    @ApiOperation("Removes game from trader using game ID & user ID")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Game was successfully removed."),
                    @ApiResponse(code = 404, message = "Game or user not found.")
            }
    )
    @DeleteMapping("/users/{userId}/games/{gameId}")
    public ResponseEntity<String> removeGameFromTrader(@PathVariable("userId") Integer userId, @PathVariable("gameId") Integer gameId) {
        UserEntity userEntity = userService.getUserById(userId).orElseThrow(ExceptionMessagesUtil.userNotFound);
        Game game = gameService.getGameById(gameId).orElseThrow(ExceptionMessagesUtil.gameNotFound);
        gameService.removeGameFromTrader(userEntity, game);
        log.info("Game - {} was successfully removed from user - {}",game.getGameName(), userEntity.getEmail());
        return ResponseEntity.ok("Game was removed from user - " + userEntity.getFirstName());
    }
    @ApiOperation("Deletes game.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Game was successfully deleted."),
                    @ApiResponse(code = 404, message = "Game or user not found.")
            }
    )
    @DeleteMapping("/admin/games/{id}")
    public ResponseEntity<Game> deleteGame(@PathVariable("id") Integer gameId) {
        log.info("Game with id - {} was successfully removed by admin",gameId);
        return ResponseEntity.ok(gameService.deleteGame(gameId));
    }
}
