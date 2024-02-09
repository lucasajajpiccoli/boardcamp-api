package com.boardcampapi.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.boardcampapi.api.dtos.RentalDTO;
import com.boardcampapi.api.exceptions.CustomerNotFoundException;
import com.boardcampapi.api.exceptions.GameNotFoundException;
import com.boardcampapi.api.exceptions.RentalNotFoundException;
import com.boardcampapi.api.exceptions.UnprocessableRentalException;
import com.boardcampapi.api.models.CustomerModel;
import com.boardcampapi.api.models.GameModel;
import com.boardcampapi.api.models.RentalModel;
import com.boardcampapi.api.repositories.CustomerRepository;
import com.boardcampapi.api.repositories.GameRepository;
import com.boardcampapi.api.repositories.RentalRepository;
import com.boardcampapi.api.services.RentalService;

@SpringBootTest
class RentalUnitTests {
    
    @InjectMocks
    RentalService rentalService;

    @Mock
    RentalRepository rentalRepository;

    @Mock
    GameRepository gameRepository;

    @Mock
    CustomerRepository customerRepository;

    @Test
    void givenWrongGameId_whenCreatingRental_thenThrowsError() {
        //given
        RentalDTO rental = new RentalDTO(1L, 1L, 3L);

        doReturn(Optional.empty()).when(gameRepository).findById(any());

        //when
        GameNotFoundException exception = assertThrows(
            GameNotFoundException.class,
            () -> rentalService.save(rental)
        );

        //then
        verify(gameRepository, times(1)).findById(any());
        verify(rentalRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Game not found!", exception.getMessage());
    }

    @Test
    void givenWrongCustomerId_whenCreatingRental_thenThrowsError() {
        //given
        RentalDTO rental = new RentalDTO(1L, 1L, 3L);
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);

        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(Optional.empty()).when(customerRepository).findById(any());

        //when
        CustomerNotFoundException exception = assertThrows(
            CustomerNotFoundException.class,
            () -> rentalService.save(rental)
        );

        //then
        verify(gameRepository, times(1)).findById(any());
        verify(customerRepository, times(1)).findById(any());
        verify(rentalRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Customer not found!", exception.getMessage());
    }

    @Test
    void givenRentalWithInsufficientStock_whenCreatingRental_thenThrowsError() {
        //given
        RentalDTO rental = new RentalDTO(1L, 1L, 3L);
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);
        CustomerModel customer = new CustomerModel(1L, "Customer", "12345678910");
        Long openRentals = game.getStockTotal() + 2;

        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(openRentals).when(rentalRepository).countOpenRentalsByGameId(any());

        //when
        UnprocessableRentalException exception = assertThrows(
            UnprocessableRentalException.class,
            () -> rentalService.save(rental)
        );

        //then
        verify(gameRepository, times(1)).findById(any());
        verify(customerRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).countOpenRentalsByGameId(any());
        verify(rentalRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("There is no available game in stock!", exception.getMessage());
    }

    @Test
    void givenRentalWithOpenRentalsEqualsToStockTotalPlus1_whenCreatingRental_thenThrowsError() {
        //given
        RentalDTO rental = new RentalDTO(1L, 1L, 3L);
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);
        CustomerModel customer = new CustomerModel(1L, "Customer", "12345678910");
        Long openRentals = game.getStockTotal() + 1;

        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(openRentals).when(rentalRepository).countOpenRentalsByGameId(any());

        //when
        UnprocessableRentalException exception = assertThrows(
            UnprocessableRentalException.class,
            () -> rentalService.save(rental)
        );

        //then
        verify(gameRepository, times(1)).findById(any());
        verify(customerRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).countOpenRentalsByGameId(any());
        verify(rentalRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("There is no available game in stock!", exception.getMessage());
    }

    @Test
    void givenRentalWithOpenRentalsEqualsToStockTotal_whenCreatingRental_thenThrowsError() {
        //given
        RentalDTO rental = new RentalDTO(1L, 1L, 3L);
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);
        CustomerModel customer = new CustomerModel(1L, "Customer", "12345678910");
        Long openRentals = game.getStockTotal();

        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(openRentals).when(rentalRepository).countOpenRentalsByGameId(any());

        //when
        UnprocessableRentalException exception = assertThrows(
            UnprocessableRentalException.class,
            () -> rentalService.save(rental)
        );

        //then
        verify(gameRepository, times(1)).findById(any());
        verify(customerRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).countOpenRentalsByGameId(any());
        verify(rentalRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("There is no available game in stock!", exception.getMessage());
    }

    @Test
    void givenValidRentalWithOpenRentalsEqualsToStockTotalMinus1_whenCreatingRental_thenCreatesRental() {
        //given
        RentalDTO rental = new RentalDTO(1L, 1L, 3L);
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);
        CustomerModel customer = new CustomerModel(1L, "Customer", "12345678910");
        Long openRentals = game.getStockTotal() - 1;
        RentalModel newRental = new RentalModel(
            LocalDate.now(),
            rental.getDaysRented(),
            rental.getDaysRented() * game.getPricePerDay(),
            0L,
            customer,
            game
        );

        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(openRentals).when(rentalRepository).countOpenRentalsByGameId(any());
        doReturn(newRental).when(rentalRepository).save(any());

        //when
        RentalModel result = rentalService.save(rental);

        //then
        verify(gameRepository, times(1)).findById(any());
        verify(customerRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).countOpenRentalsByGameId(any());
        verify(rentalRepository, times(1)).save(any());
        assertEquals(newRental, result);
    }

    @Test
    void givenValidRental_whenCreatingRental_thenCreatesRental() {
        //given
        RentalDTO rental = new RentalDTO(1L, 1L, 3L);
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);
        CustomerModel customer = new CustomerModel(1L, "Customer", "12345678910");
        Long openRentals = game.getStockTotal() - 2;
        RentalModel newRental = new RentalModel(
            LocalDate.now(),
            rental.getDaysRented(),
            rental.getDaysRented() * game.getPricePerDay(),
            0L,
            customer,
            game
        );

        doReturn(Optional.of(game)).when(gameRepository).findById(any());
        doReturn(Optional.of(customer)).when(customerRepository).findById(any());
        doReturn(openRentals).when(rentalRepository).countOpenRentalsByGameId(any());
        doReturn(newRental).when(rentalRepository).save(any());

        //when
        RentalModel result = rentalService.save(rental);

        //then
        verify(gameRepository, times(1)).findById(any());
        verify(customerRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).countOpenRentalsByGameId(any());
        verify(rentalRepository, times(1)).save(any());
        assertEquals(newRental, result);
    }

    @Test
    void givenWrongRentalId_whenUpdatingRental_thenThrowsError() {
        //given
        Long id = 1L;

        doReturn(Optional.empty()).when(rentalRepository).findById(any());

        //when
        RentalNotFoundException exception = assertThrows(
            RentalNotFoundException.class,
            () -> rentalService.update(id)
        );

        //then
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("Rental not found!", exception.getMessage());
    }

    @Test
    void givenCloseRental_whenUpdatingRental_thenThrowsError() {
        //given
        Long id = 1L;
        LocalDate returnDate = LocalDate.of(2024,1,5);
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);
        CustomerModel customer = new CustomerModel(1L, "Customer", "12345678910");
        RentalModel rental = new RentalModel(id,LocalDate.of(2024,1,1),3L,returnDate,4500L,0L,customer,game);

        doReturn(Optional.of(rental)).when(rentalRepository).findById(any());

        //when
        UnprocessableRentalException exception = assertThrows(
            UnprocessableRentalException.class,
            () -> rentalService.update(id)
        );

        //then
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(0)).save(any());
        assertNotNull(exception);
        assertEquals("This rental is already closed!", exception.getMessage());
    }

    @Test
    void givenValidRental_whenUpdatingRental_thenUpdatesRental() {
        //given
        Long id = 1L;
        LocalDate returnDate = null;
        GameModel game = new GameModel(1L, "Game", "http://", 5L, 1500L);
        CustomerModel customer = new CustomerModel(1L, "Customer", "12345678910");
        RentalModel rental = new RentalModel(id,LocalDate.of(2024,1,1),3L,returnDate,4500L,0L,customer,game);
        RentalModel newRental = new RentalModel(id,LocalDate.of(2024,1,1),3L,LocalDate.of(2024,1,5),4500L,1500L,customer,game);

        doReturn(Optional.of(rental)).when(rentalRepository).findById(any());
        doReturn(newRental).when(rentalRepository).save(any());

        //when
        RentalModel result = rentalService.update(id);

        //then
        verify(rentalRepository, times(1)).findById(any());
        verify(rentalRepository, times(1)).save(any());
        assertEquals(newRental, result);
    }

}
