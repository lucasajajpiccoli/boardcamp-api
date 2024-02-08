package com.boardcampapi.api.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boardcampapi.api.dtos.RentalDTO;
import com.boardcampapi.api.exceptions.CustomerNotFoundException;
import com.boardcampapi.api.exceptions.GameNotFoundException;
import com.boardcampapi.api.exceptions.UnprocessableRentalException;
import com.boardcampapi.api.models.CustomerModel;
import com.boardcampapi.api.models.GameModel;
import com.boardcampapi.api.models.RentalModel;
import com.boardcampapi.api.repositories.CustomerRepository;
import com.boardcampapi.api.repositories.GameRepository;
import com.boardcampapi.api.repositories.RentalRepository;

import jakarta.transaction.Transactional;

@Service
public class RentalService {
    
    final RentalRepository rentalRepository;
    final GameRepository gameRepository;
    final CustomerRepository customerRepository;

    RentalService(
        RentalRepository rentalRepository,
        GameRepository gameRepository,
        CustomerRepository customerRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.gameRepository = gameRepository;
        this.customerRepository = customerRepository;
    }

    public List<RentalModel> findAll() {
        return rentalRepository.findAll();
    }

    public RentalModel save(RentalDTO dto) {
        GameModel game = gameRepository.findById(dto.getGameId()).orElseThrow(
            () -> new GameNotFoundException("Game not found!")
        );

        CustomerModel customer = customerRepository.findById(dto.getCustomerId()).orElseThrow(
            () -> new CustomerNotFoundException("Customer not found!")
        );

        
        LocalDate rentDate = LocalDate.now();
        Long daysRented = dto.getDaysRented();
        Long originalPrice = dto.getDaysRented() * game.getPricePerDay();
        Long delayFee = 0L;

        RentalModel newRental = new RentalModel(rentDate, daysRented, originalPrice, delayFee, customer, game);
    
        return saveIfExistsAvailableGame(newRental);
    }

    @Transactional
    RentalModel saveIfExistsAvailableGame(RentalModel rental) {
        Long gameId = rental.getGame().getId();
        Long openRentals = rentalRepository.countOpenRentalsByGameId(gameId);
        Long stockTotal = rental.getGame().getStockTotal();
        
        if(openRentals >= stockTotal) {
            throw new UnprocessableRentalException("There is no available game in stock!");
        }

        return rentalRepository.save(rental);
    }

}
