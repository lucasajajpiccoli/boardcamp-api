package com.boardcampapi.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcampapi.api.dtos.GameDTO;
import com.boardcampapi.api.exceptions.GameNameConflictException;
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

    public GameModel save(GameDTO dto) {
        if (gameRepository.existsByName(dto.getName())) {
            throw new GameNameConflictException("A game with this name already exists!");
        }

        GameModel game = new GameModel(dto);
        return gameRepository.save(game);
    }
    
}