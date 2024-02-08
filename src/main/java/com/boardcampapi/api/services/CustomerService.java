package com.boardcampapi.api.services;

import org.springframework.stereotype.Service;

import com.boardcampapi.api.dtos.CustomerDTO;
import com.boardcampapi.api.exceptions.CustomerCpfConflictException;
import com.boardcampapi.api.exceptions.CustomerNotFoundException;
import com.boardcampapi.api.models.CustomerModel;
import com.boardcampapi.api.repositories.CustomerRepository;

@Service
public class CustomerService {
    
    final CustomerRepository customerRepository;

    CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerModel findById(Long id) {
        return customerRepository.findById(id).orElseThrow(
            () -> new CustomerNotFoundException("Customer not found!")
        );
    }

    public CustomerModel save(CustomerDTO dto) {
        if (customerRepository.existsByCpf(dto.getCpf())) {
            throw new CustomerCpfConflictException("A customer with this cpf already exists!");
        }

        CustomerModel customer = new CustomerModel(dto);
        return customerRepository.save(customer);
    }

}
