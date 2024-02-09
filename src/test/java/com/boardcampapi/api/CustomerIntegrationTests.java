package com.boardcampapi.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

import com.boardcampapi.api.dtos.CustomerDTO;
import com.boardcampapi.api.models.CustomerModel;
import com.boardcampapi.api.repositories.CustomerRepository;
import com.boardcampapi.api.repositories.GameRepository;
import com.boardcampapi.api.repositories.RentalRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerIntegrationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @BeforeEach
    @AfterEach
    void cleanUpDatabase() {
        rentalRepository.deleteAll();
        customerRepository.deleteAll();
        gameRepository.deleteAll();
    }

    @Test
    void givenWrongCustomerId_whenGettingCustomer_thenThrowsError() {
        //given
        CustomerModel customer = new CustomerModel(null, "Customer", "12345678910");
        CustomerModel createdCustomer = customerRepository.save(customer);
        customerRepository.deleteById(createdCustomer.getId());

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers/{id}",
            HttpMethod.GET,
            null,
            String.class,
            createdCustomer.getId()
        );

        //then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Customer not found!", response.getBody());
        assertEquals(0, customerRepository.count());
    }

    @Test
    void givenValidCustomerId_whenGettingCustomer_thenGetsCustomer() {
        //given
        CustomerModel customer = new CustomerModel(null, "Customer", "12345678910");
        CustomerModel createdCustomer = customerRepository.save(customer);

        //when
        ResponseEntity<CustomerModel> response = restTemplate.exchange(
            "/customers/{id}",
            HttpMethod.GET,
            null,
            CustomerModel.class,
            createdCustomer.getId()
        );

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, customerRepository.count());
    }

    @Test
    void givenCustomerWithCpfOfLength10_whenCreatingCustomer_thenThrowsError() {
        //given
        String name = "Customer";
        String cpf = "1234567890";
        CustomerDTO customer = new CustomerDTO(name, cpf);

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customer);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
    }

    @Test
    void givenCustomerWithCpfOfLength12_whenCreatingCustomer_thenThrowsError() {
        //given
        String name = "Customer";
        String cpf = "123456789012";
        CustomerDTO customer = new CustomerDTO(name, cpf);

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customer);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
    }

    @Test
    void givenCustomerWithInvalidName_whenCreatingCustomer_thenThrowsError() {
        //given
        String name = " ";
        String cpf = "12345678901";
        CustomerDTO customer = new CustomerDTO(name, cpf);

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customer);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(0, customerRepository.count());
    }

    @Test
    void givenRepeatedCustomer_whenCreatingCustomer_thenThrowsError() {
        //given
        String name = "Customer";
        String cpf = "12345678901";
        CustomerDTO customer = new CustomerDTO(name, cpf);
        CustomerModel customerConflict = new CustomerModel(null, name, cpf);
        customerRepository.save(customerConflict);

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customer);

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/customers",
            HttpMethod.POST,
            body,
            String.class
        );

        //then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("A customer with this cpf already exists!", response.getBody());
        assertEquals(1, customerRepository.count());
    }

    @Test
    void givenValidCustomer_whenCreatingCustomer_thenCreatesCustomer() {
        //given
        String name = "Customer";
        String cpf = "12345678901";
        CustomerDTO customer = new CustomerDTO(name, cpf);

        HttpEntity<CustomerDTO> body = new HttpEntity<>(customer);

        //when
        ResponseEntity<CustomerModel> response = restTemplate.exchange(
            "/customers",
            HttpMethod.POST,
            body,
            CustomerModel.class
        );

        //then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, customerRepository.count());
    }
    
}
