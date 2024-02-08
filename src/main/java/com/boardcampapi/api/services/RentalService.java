package com.boardcampapi.api.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcampapi.api.models.RentalModel;
import com.boardcampapi.api.repositories.RentalRepository;

@Service
public class RentalService {
    
    final RentalRepository rentalRepository;

    RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<RentalModel> findAll() {
        return rentalRepository.findAll();
    }
    
}
