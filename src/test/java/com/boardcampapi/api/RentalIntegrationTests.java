package com.boardcampapi.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.boardcampapi.api.dtos.RentalDTO;
import com.boardcampapi.api.models.CustomerModel;
import com.boardcampapi.api.models.GameModel;
import com.boardcampapi.api.models.RentalModel;
import com.boardcampapi.api.repositories.CustomerRepository;
import com.boardcampapi.api.repositories.GameRepository;
import com.boardcampapi.api.repositories.RentalRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class RentalIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CustomerRepository customerRepository;
    
    @BeforeEach
    @AfterEach
    void cleanUpDatabase() {
        rentalRepository.deleteAll();
        gameRepository.deleteAll();
        customerRepository.deleteAll();
    }
    
    @Test
    void givenInvalidCustomerId_whenCreatingRental_thenThrowsError() {
        //given
        Long customerId = null;
        Long gameId = 1L;
        Long daysRented = 1L;        
        RentalDTO rental = new RentalDTO(customerId, gameId, daysRented);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rental);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenInvalidGameId_whenCreatingRental_thenThrowsError() {
        //given
        Long customerId = 1L;
        Long gameId = null;
        Long daysRented = 1L;        
        RentalDTO rental = new RentalDTO(customerId, gameId, daysRented);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rental);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenInvalidDaysRented_whenCreatingRental_thenThrowsError() {
        //given
        Long customerId = 1L;
        Long gameId = 1L;
        Long daysRented = -1L;        
        RentalDTO rental = new RentalDTO(customerId, gameId, daysRented);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rental);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenInexistentCustomerId_whenCreatingRental_thenThrowsError() {
        //given
        CustomerModel customer = new CustomerModel(null, "Customer", "12345678901");
        CustomerModel createdCustomer = customerRepository.save(customer);
        GameModel game = new GameModel(null, "Game", "https://", 3L, 1500L);
        GameModel createdGame = gameRepository.save(game);
        customerRepository.deleteById(createdCustomer.getId());

        RentalDTO rental = new RentalDTO(createdCustomer.getId(), createdGame.getId(), 5L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rental);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found!", response.getBody());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenInexistentGameId_whenCreatingRental_thenThrowsError() {
        //given
        CustomerModel customer = new CustomerModel(null, "Customer", "12345678901");
        CustomerModel createdCustomer = customerRepository.save(customer);
        GameModel game = new GameModel(null, "Game", "https://", 3L, 1500L);
        GameModel createdGame = gameRepository.save(game);
        gameRepository.deleteById(createdGame.getId());

        RentalDTO rental = new RentalDTO(createdCustomer.getId(), createdGame.getId(), 5L);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rental);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Game not found!", response.getBody());
        assertEquals(0, rentalRepository.count());
    }

    @Test
    void givenUnavailableRental_whenCreatingRental_thenThrowsError() {
        //given
        Long daysRented = 5L;
        CustomerModel customer = new CustomerModel(null, "Customer", "12345678901");
        CustomerModel createdCustomer = customerRepository.save(customer);
        GameModel game = new GameModel(null, "Game", "https://", 3L, 1500L);
        GameModel createdGame = gameRepository.save(game);
        RentalModel rental = new RentalModel(
            null,
            LocalDate.now(),
            daysRented,
            null,
            daysRented * createdGame.getPricePerDay(),
            0L,
            createdCustomer,
            createdGame   
        );
        rentalRepository.save(rental);
        rental.setId(null);
        rentalRepository.save(rental);
        rental.setId(null);
        rentalRepository.save(rental);

        RentalDTO rentalDto = new RentalDTO(createdCustomer.getId(), createdGame.getId(), daysRented);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDto);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/rentals",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("There is no available game in stock!", response.getBody());
        assertEquals(3, rentalRepository.count());
    }

    @Test
    void givenAvailableRental_whenCreatingRental_thenCreatesRental() {
        //given
        Long daysRented = 5L;
        CustomerModel customer = new CustomerModel(null, "Customer", "12345678901");
        CustomerModel createdCustomer = customerRepository.save(customer);
        GameModel game = new GameModel(null, "Game", "https://", 3L, 1500L);
        GameModel createdGame = gameRepository.save(game);
        RentalModel rental = new RentalModel(
            null,
            LocalDate.now(),
            daysRented,
            null,
            daysRented * createdGame.getPricePerDay(),
            0L,
            createdCustomer,
            createdGame   
        );
        rentalRepository.save(rental);
        rental.setId(null);
        rentalRepository.save(rental);

        RentalDTO rentalDto = new RentalDTO(createdCustomer.getId(), createdGame.getId(), daysRented);

        HttpEntity<RentalDTO> body = new HttpEntity<>(rentalDto);

        //when
        ResponseEntity<RentalModel> response = restTemplate.exchange(
            "/rentals",
            HttpMethod.POST,
            body,
            RentalModel.class
        );

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(3, rentalRepository.count());
    }

}
