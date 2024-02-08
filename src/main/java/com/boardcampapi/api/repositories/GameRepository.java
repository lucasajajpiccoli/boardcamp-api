package com.boardcampapi.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boardcampapi.api.models.GameModel;

@Repository
public interface GameRepository extends JpaRepository<GameModel, Long> {
    
}
