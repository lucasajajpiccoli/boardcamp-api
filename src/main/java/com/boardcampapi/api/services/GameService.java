package com.boardcampapi.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcampapi.api.models.GameModel;
import com.boardcampapi.api.repositories.GameRepository;

@Service
public class GameService {

    final GameRepository gameRepository;

    GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameModel> findAll() {
        return gameRepository.findAll();
    }

}