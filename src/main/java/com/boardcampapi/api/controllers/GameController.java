package com.boardcampapi.api.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.boardcampapi.api.models.GameModel;
import com.boardcampapi.api.services.GameService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/games")
public class GameController {
    
    final GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<List<GameModel>> getGames() {
        List<GameModel> games = gameService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(games);
    }

}
