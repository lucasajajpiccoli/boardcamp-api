package com.boardcampapi.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.boardcampapi.api.models.RentalModel;

@Repository
public interface RentalRepository extends JpaRepository<RentalModel, Long> {
    
}
